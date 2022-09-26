package fiek.unipr.mostwantedapp.fragment.admin;

import static fiek.unipr.mostwantedapp.utils.BitmapHelper.addBorder;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;
import static fiek.unipr.mostwantedapp.utils.EditTextHelper.disableEditable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.gallery.CustomizedGalleryAdapter;

public class SingleReportFragment extends Fragment {

    private View view;
    private GoogleMap mMap;
    private Bundle singleReportMapBundle;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private DocumentReference assigned_report_doc;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private UploadTask uploadTask;

    private TextInputEditText et_report_uid, et_report_informer, et_report_date_time, et_report_title,
            et_report_description;
    private TextInputLayout autocomplete_report_status_layout;
    private ImageView imageView;
    private Gallery image_gallery;
    private MaterialAutoCompleteTextView auto_complete_report_status;
    private Button btnSaveReport;

    private String date_time, title, docId, description, informer_person, status, uID, personId, wanted_person, urlOfProfile;
    private Double latitude, longitude;
    private int totalImages;
    private String[] images;

    private CustomizedGalleryAdapter customGalleryAdapter;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
                if(wanted_person != null && personId != null)
                {
                    setMarker(getContext(), personId, wanted_person);
                }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_report, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();

        initializeFields();

        getFromBundle(singleReportMapBundle);

        setReportInformation(uID, date_time, informer_person, title, description, status);

        setDropDownOptions();

        btnSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(auto_complete_report_status.getText().toString(), docId);
            }
        });

        disableEditable(et_report_date_time);
        disableEditable(et_report_uid);
        disableEditable(et_report_informer);
        disableEditable(et_report_title);
        disableEditable(et_report_description);

        onBackPressed();

        return view;
    }

    private void initializeFields() {
        et_report_uid = view.findViewById(R.id.et_report_uid);
        et_report_informer = view.findViewById(R.id.et_report_informer);
        et_report_date_time = view.findViewById(R.id.et_report_date_time);
        et_report_title = view.findViewById(R.id.et_report_title);
        et_report_description = view.findViewById(R.id.et_report_description);
        imageView = view.findViewById(R.id.imageView);
        image_gallery = view.findViewById(R.id.image_gallery);
        auto_complete_report_status = view.findViewById(R.id.auto_complete_report_status);
        btnSaveReport = view.findViewById(R.id.btnSaveReport);
        autocomplete_report_status_layout = view.findViewById(R.id.autocomplete_report_status_layout);
    }

    private void setDropDownOptions() {
        String[] status_state = getContext().getResources().getStringArray(R.array.status_state);
        ArrayAdapter<String> statusStateAdapter = new ArrayAdapter<>(getContext(),
                R.layout.drop_down_item_report_status_state, status_state);
        auto_complete_report_status.setAdapter(statusStateAdapter);
        String status_old = auto_complete_report_status.getText().toString();
        auto_complete_report_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                autocomplete_report_status_layout.setHintTextColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setBoxStrokeColor(getContext().getResources().getColor(R.color.gray));
                autocomplete_report_status_layout.setBoxStrokeColorStateList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setCounterTextColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setEndIconTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.gray)));

                String status_new = auto_complete_report_status.getText().toString();

                if(status_old.equals(status_new)){
                    btnSaveReport.setVisibility(View.GONE);
                }else {
                    btnSaveReport.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void save(String value, String docId) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", value);

        firebaseFirestore.collection(LOCATION_REPORTS)
                .document(docId)
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), getContext().getText(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                        btnSaveReport.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getContext().getText(R.string.failed_to_save), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setReportInformation(String uID, String date_time, String informer_person, String title, String description, String status) {
        et_report_uid.setText(uID);
        et_report_date_time.setText(date_time);
        et_report_informer.setText(informer_person);
        et_report_title.setText(title);
        et_report_description.setText(description);
        auto_complete_report_status.setText(status);
    }

    private void getFromBundle(Bundle bundle) {

        try {
            bundle = getArguments();
            date_time = bundle.getString("date_time");
            title = bundle.getString("title");
            description = bundle.getString("description");
            informer_person = bundle.getString("informer_person");
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            status = bundle.getString("status");
            uID = bundle.getString("uID");
            docId = bundle.getString("docId");
            personId = bundle.getString("personId");
            wanted_person = bundle.getString("wanted_person");
            totalImages = bundle.getInt("totalImages");
            images = bundle.getStringArray("images");

            if(images != null && images.length > 0){
                image_gallery.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                setFirstImageSelectedInGallery(0);
                customGalleryAdapter = new CustomizedGalleryAdapter(getContext(), images);
                image_gallery.setAdapter(customGalleryAdapter);

                image_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Whichever image is clicked, that is set in the  selectedImageView
                        // position will indicate the location of image
                        Glide.with(getContext())
                                .asBitmap()
                                .load(images[position])
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        imageView.setImageBitmap(resource);
                                        return true;
                                    }
                                })
                                .preload();
                    }
                });
            }else {
                image_gallery.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void setFirstImageSelectedInGallery(int position) {
        Glide.with(getContext())
                .asBitmap()
                .load(images[position])
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setImageBitmap(resource);
                        return true;
                    }
                })
                .preload();
    }

    private void setMarker(Context context, String personId, String wanted_person) {
        firebaseFirestore
                .collection(WANTED_PERSONS)
                .whereEqualTo("personId", personId)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots != null)
                        {
                               for (int i = 0; i<queryDocumentSnapshots.size();i++)
                               {
                                   urlOfProfile = queryDocumentSnapshots.getDocuments().get(i).getString("urlOfProfile");
                                   try {
                                       Glide.with(context)
                                               .asBitmap()
                                               .load(urlOfProfile)
                                               .apply(new RequestOptions().override(200, 200))
                                               .listener(new RequestListener<Bitmap>() {
                                                   @Override
                                                   public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                       return false;
                                                   }

                                                   @Override
                                                   public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                       Bitmap newBitmap = addBorder(resource, getContext());
                                                       LatLng latLng = new LatLng(latitude, longitude);

                                                       Geocoder geocoder;
                                                       List<Address> addresses = null;
                                                       geocoder = new Geocoder(getContext(), Locale.getDefault());

                                                       try {
                                                           addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                           String address = addresses.get(0).getAddressLine(0);
                                                           String city = addresses.get(0).getLocality();
                                                           String state = addresses.get(0).getAdminArea();
                                                           String country = addresses.get(0).getCountryName();
                                                           String postalCode = addresses.get(0).getPostalCode();
                                                           String knownName = addresses.get(0).getFeatureName();

                                                           mMap.addMarker(new MarkerOptions()
                                                                   .position(latLng)
                                                                   .title(wanted_person)
                                                                   .snippet(address + ", "+ city+ ", "+ postalCode)
                                                                   .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                                                           mMap.setMinZoomPreference(DEFAULT_ZOOM);
                                                           mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                                       } catch (IOException e) {
                                                           e.printStackTrace();
                                                       }

                                                       return true;
                                                   }
                                               })
                                               .circleCrop()
                                               .preload();
                                   } catch(Exception e) {
                                       Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getContext().getText(R.string.error_failed_to_get_image_from_database)+"", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.single_report_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void onBackPressed() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Fragment fragment = new NotificationFragment();
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, fragment)
                .commit();
    }

}
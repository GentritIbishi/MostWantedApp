package fiek.unipr.mostwantedapp.fragment.admin;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static fiek.unipr.mostwantedapp.utils.BitmapHelper.addBorder;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.FREE;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.Constants.VERIFIED;
import static fiek.unipr.mostwantedapp.utils.Constants.WANTED_PERSONS;
import static fiek.unipr.mostwantedapp.utils.EditTextHelper.disableEditable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.gallery.CustomizedGalleryAdapter;
import fiek.unipr.mostwantedapp.models.Report;
import fiek.unipr.mostwantedapp.utils.CheckInternet;
import fiek.unipr.mostwantedapp.utils.StringHelper;

public class SingleReportFragment extends Fragment {

    private View view;
    private Context mContext;
    private GoogleMap mMap;

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

    private String date_time, title, docId, description, prizeToWin, address, informer_person_urlOfProfile, informer_person, status, uID, personId, wanted_person, urlOfProfile;
    private Double latitude, longitude;
    private int totalImages;
    private String[] images;

    private TextView tv_singleReportProgressBar;
    private ProgressBar singleReportProgressBarReport;
    private int progress;

    private CustomizedGalleryAdapter customGalleryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFromBundle();
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            Bundle bundle = getArguments();
            if (bundle != null) {
                String wanted_person = getArguments().getString("wanted_person");
                String date_time = bundle.getString("date_time");
                String title = bundle.getString("title");
                String description = bundle.getString("description");
                String informer_person = bundle.getString("informer_person");
                Double latitude = bundle.getDouble("latitude");
                Double longitude = bundle.getDouble("longitude");
                String status = bundle.getString("status");
                String uID = bundle.getString("uID");
                String docId = bundle.getString("docId");
                String personId = bundle.getString("personId");

                if (CheckInternet.isConnected(mContext)) {
                    setMarker(mContext, personId, wanted_person, latitude, longitude);
                } else {
                    CheckInternet.showSettingsAlert(mContext);
                }

            } else {
                Log.e(TAG, "onMapReady: ", new Throwable("NOTSET!"));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_report, container, false);
        mContext = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseAuth.getCurrentUser().getUid();

        initializeFields();

        getFromBundle();

        setReportInformation(uID, date_time, informer_person, title, description, status);

        setDropDownOptions();

        btnSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String new_status = auto_complete_report_status.getText().toString();
                save(mContext, uID, latitude, longitude, new_status, docId, personId);

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

    private void updateProgressBar() {
        singleReportProgressBarReport.setProgress(progress);
        tv_singleReportProgressBar.setText(this.progress + "%");
        singleReportProgressBarReport.setVisibility(View.VISIBLE);
        tv_singleReportProgressBar.setVisibility(View.VISIBLE);
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
        tv_singleReportProgressBar = view.findViewById(R.id.tv_singleReportProgressBar);
        singleReportProgressBarReport = view.findViewById(R.id.singleReportProgressBarReport);
    }

    private void setDropDownOptions() {
        String[] status_state = mContext.getResources().getStringArray(R.array.status_state);
        ArrayAdapter<String> statusStateAdapter = new ArrayAdapter<>(mContext,
                R.layout.drop_down_item_report_status_state, status_state);
        auto_complete_report_status.setAdapter(statusStateAdapter);
        String status_old = auto_complete_report_status.getText().toString();
        auto_complete_report_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                autocomplete_report_status_layout.setHintTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setBoxStrokeColor(mContext.getResources().getColor(R.color.gray));
                autocomplete_report_status_layout.setBoxStrokeColorStateList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setCounterTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.gray)));
                autocomplete_report_status_layout.setEndIconTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.gray)));
            }
        });

    }

    private void save(Context context, String uID, Double latitude, Double longitude, String status, String docId, String personId) {
        progress += 25;
        updateProgressBar();
        process(context, uID, latitude, longitude, status, docId, personId);
    }

    private void process(Context context, String uID, Double latitude, Double longitude, String status, String docId, String personId) {
        firebaseFirestore.collection(LOCATION_REPORTS)
                .document(docId)
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (status.equals(VERIFIED)) {
                            progress += 12.5;
                            updateProgressBar();
                            //check if user with uid is anonymous or phone or regular
                            if (!firebaseAuth.getCurrentUser().isAnonymous() && StringHelper.empty(firebaseAuth.getCurrentUser().getPhoneNumber())) {
                                updateBalance(context, latitude, longitude, uID, personId);
                            }
                        } else {
                            progress += 75;
                            updateProgressBar();
                            dismissProgressBar();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR IN BEGIN", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void updateBalance(Context context, Double latitude, Double longitude, String uID, String personId) {
        //duhet me marr prize prej wanted person
        //duhet me marr balance prej userit
        //duhet me bo check nese ka same reported location with same address nese jo atehere ja shton vlenre full
        //nese po atehere e pjeston qate vlere me numrin e raportimeve
        //duhet me ja shtu balance+=vleren qe eka fitu
        progress += 12.5;
        updateProgressBar();
        firebaseFirestore.collection(WANTED_PERSONS)
                .document(personId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String prize = documentSnapshot.getString("prize");
                        progress += 12.5;
                        updateProgressBar();
                        if (!StringHelper.empty(prize)) {
                            if (!prize.equalsIgnoreCase(FREE)) {
                                getBalanceFromUser(context, latitude, longitude, uID, prize);
                            }
                        } else {
                            dismissProgressBar();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorNePrize", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void dismissProgressBar() {
        tv_singleReportProgressBar.setVisibility(GONE);
        singleReportProgressBarReport.setVisibility(GONE);
        progress = 0;
    }

    private void getBalanceFromUser(Context context, Double latitude, Double longitude, String uID, String prize) {
        //duhet me marr balance prej userit
        //duhet me bo check nese ka same reported location with same address
        firebaseFirestore.collection(USERS)
                .document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double balance = documentSnapshot.getDouble("balance");
                        String[] prizeArray = prize.split(" ");
                        Double finalPrize = Double.valueOf(prizeArray[0]);
                        progress += 12.5;
                        updateProgressBar();
                        checkIfHaveMoreThanOneReportWithSameLocation(context, personId, latitude, longitude, balance, finalPrize);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorNeBalance", e.getMessage());
                        dismissProgressBar();
                    }
                });


    }

    private void checkIfHaveMoreThanOneReportWithSameLocation(Context context, String personId, Double latitude, Double longitude, Double balance, Double prize) {
        //nese jo atehere ja shton vleren full
        //nese po atehere e pjeston qate vlere me numrin e raportimeve
        String address = getAddress(context, latitude, longitude);
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereEqualTo("personId", personId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            String addressInDoc = queryDocumentSnapshots.getDocuments().get(i).getString("address");
                            progress += 12.5;
                            updateProgressBar();
                            if (address.equals(addressInDoc)) {
                                count++;
                            }
                        }

                        if (count == 0) {
                            //duhet me ja mbledh balance sa e ka plus prize
                            double fullPrize = balance + prize;
                            addPrizeToWinner(uID, fullPrize);
                        } else {
                            double equal_prize = prize / count;
                            double notFullPrize = balance + equal_prize;
                            addPrizeToWinner(uID, notFullPrize);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorNeKompareAdreses", e.getMessage());
                        dismissProgressBar();
                    }
                });
    }

    private void addPrizeToWinner(String uID, Double balance) {
        //duhet me ja shtu balance+=vleren qe eka fitu

        firebaseFirestore.collection(USERS)
                .document(uID)
                .update("balance", balance)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progress += 12.5;
                        updateProgressBar();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressBar();
                            }
                        }, 1500);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorNeUpdateBalance", e.getMessage());
                        dismissProgressBar();
                    }
                });

    }

    public String getAddress(Context ctx, double latitude, double longitude) {
        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);
            }
        } catch (IOException e) {
            Toast.makeText(ctx, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return fullAdd;
    }

    private void setReportInformation(String uID, String date_time, String informer_person, String title, String description, String status) {
        et_report_uid.setText(uID);
        et_report_date_time.setText(date_time);
        et_report_informer.setText(informer_person);
        et_report_title.setText(title);
        et_report_description.setText(description);
        auto_complete_report_status.setText(status);
    }

    private void getFromBundle() {

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
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
                address = bundle.getString("address");
                informer_person_urlOfProfile = bundle.getString("informer_person_urlOfProfile");
                prizeToWin = bundle.getString("prizeToWin");
            }

            if (images != null && images.length > 0) {
                image_gallery.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                setFirstImageSelectedInGallery(0);
                customGalleryAdapter = new CustomizedGalleryAdapter(mContext, images);
                image_gallery.setAdapter(customGalleryAdapter);

                image_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Whichever image is clicked, that is set in the  selectedImageView
                        // position will indicate the location of image
                        Glide.with(mContext)
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
            } else {
                image_gallery.setVisibility(GONE);
                imageView.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void setFirstImageSelectedInGallery(int position) {
        Glide.with(mContext)
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

    private void setMarker(Context context, String personId, String wanted_person, Double latitude, Double longitude) {
        firebaseFirestore
                .collection(WANTED_PERSONS)
                .whereEqualTo("personId", personId)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
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
                                                    Bitmap newBitmap = addBorder(resource, mContext, R.color.white);
                                                    LatLng latLng = new LatLng(latitude, longitude);

                                                    Geocoder geocoder;
                                                    List<Address> addresses;
                                                    geocoder = new Geocoder(mContext, Locale.getDefault());

                                                    try {
                                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                        String address = addresses.get(0).getAddressLine(0);
                                                        String city = addresses.get(0).getLocality();
                                                        String postalCode = addresses.get(0).getPostalCode();

                                                        mMap.addMarker(new MarkerOptions()
                                                                .position(latLng)
                                                                .title(wanted_person)
                                                                .snippet(address + ", " + city + ", " + postalCode)
                                                                .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                                                        mMap.setMinZoomPreference(DEFAULT_ZOOM);
                                                        mMap.setMaxZoomPreference(DEFAULT_ZOOM);
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    return true;
                                                }
                                            })
                                            .circleCrop()
                                            .preload();
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, mContext.getText(R.string.error_failed_to_get_image_from_database) + "", Toast.LENGTH_SHORT).show();
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
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fragment = new NotificationFragment();
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
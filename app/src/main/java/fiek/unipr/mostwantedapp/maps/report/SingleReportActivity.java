package fiek.unipr.mostwantedapp.maps.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.adapter.CustomizedGalleryAdapter;
import fiek.unipr.mostwantedapp.databinding.ActivitySingleReportBinding;
import fiek.unipr.mostwantedapp.maps.MapsActivity;

public class SingleReportActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySingleReportBinding binding;

    private Bundle singleReportMapBundle;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private DocumentReference assigned_report_doc;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private UploadTask uploadTask;

    private String date_time, description, informer_person, status, uID, wanted_person, urlOfProfile;
    private Double latitude, longitude;
    private int totalImages;
    private String[] images;

    private CustomizedGalleryAdapter customGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySingleReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();

        singleReportMapBundle = new Bundle();
        getFromBundle(singleReportMapBundle);

        binding.etReportStatus.setText(status);
        binding.etReportDateTime.setText(date_time);
        binding.etReportUid.setText(uID);
        binding.etReportInformer.setText(informer_person);
        binding.etReportDescription.setText(description);

        binding.etReportStatus.setFocusable(false);
        binding.etReportDateTime.setFocusable(false);
        binding.etReportUid.setFocusable(false);
        binding.etReportInformer.setFocusable(false);
        binding.etReportDescription.setFocusable(false);

        initMap();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(wanted_person != null) {
            setMarker(wanted_person);
        }

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.single_report_map);
        mapFragment.getMapAsync(this);
    }

    private void getFromBundle(Bundle bundle) {

        try {
            bundle = getIntent().getExtras();
            date_time = bundle.getString("date_time");
            description = bundle.getString("description");
            informer_person = bundle.getString("informer_person");
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            status = bundle.getString("status");
            uID = bundle.getString("uID");
            wanted_person = bundle.getString("wanted_person");
            totalImages = bundle.getInt("totalImages");
            images = bundle.getStringArray("images");

            setFirstImageSelectedInGallery(0);
            customGalleryAdapter = new CustomizedGalleryAdapter(getApplicationContext(), images);
            binding.imageGallery.setAdapter(customGalleryAdapter);


            binding.imageGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Whichever image is clicked, that is set in the  selectedImageView
                    // position will indicate the location of image
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(images[position])
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    binding.imageView.setImageBitmap(resource);
                                    return true;
                                }
                            })
                            .preload();
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void setFirstImageSelectedInGallery(int position) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(images[position])
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        binding.imageView.setImageBitmap(resource);
                        return true;
                    }
                })
                .preload();
    }

    private void setMarker(String wanted_person) {
        firebaseFirestore
                .collection("wanted_persons")
                .document(wanted_person)
                .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                urlOfProfile = documentSnapshot.getString("urlOfProfile");
                                try {
                                    Glide.with(SingleReportActivity.this)
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
                                                    Bitmap newBitmap = addBorder(resource, getApplicationContext());
                                                    LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .title(getApplicationContext().getText(R.string.last_seen)+" "+wanted_person)
                                                            .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                                                    mMap.setMinZoomPreference(17);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                    return true;
                                                }
                                            })
                                            .circleCrop()
                                            .preload();
                                } catch(Exception e) {
                                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingleReportActivity.this, getApplicationContext().getText(R.string.error_failed_to_get_image_from_database)+"", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static Bitmap addBorder(Bitmap resource, Context context) {
        int w = resource.getWidth();
        int h = resource.getHeight();
        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        p.setAntiAlias(true);
        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(resource, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ContextCompat.getColor(context, R.color.verydark));
        p.setStrokeWidth(10);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
        return output;
    }

    private void getTotalImage(int totalImages) {
        String[] str = new String[totalImages];
        for(int i = 0; i<totalImages; i++){
            //search for
            str[i] = "image"+i;
        }
    }

}
package fiek.unipr.mostwantedapp.activity.maps.admin;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static fiek.unipr.mostwantedapp.utils.BitmapHelper.BitmapFromVector;
import static fiek.unipr.mostwantedapp.utils.BitmapHelper.addBorder;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.DYNAMIC_DOMAIN;
import static fiek.unipr.mostwantedapp.utils.Constants.INVESTIGATORS;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.REPORTS_ASSIGNED;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;
import static fiek.unipr.mostwantedapp.utils.StringHelper.removeFirstLetterAndLastLetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsBinding;
import fiek.unipr.mostwantedapp.models.ReportAssigned;
import fiek.unipr.mostwantedapp.models.ReportAssignedUser;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;
import fiek.unipr.mostwantedapp.utils.WindowHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String NO_LOCATION_REPORT = "";

    private String uID, last_seen_address, first_address, second_address, third_address, forth_address,
            urlOfPdfUploaded, date, shortUrl, personId, firstName, lastName, parentName, fullName, birthday, gender, address, age, eyeColor, hairColor, height,
            phy_appearance, status, prize, urlOfProfile, weight, registration_date;

    private double last_seen_latitude, last_seen_longitude, lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4;

    private List<String> acts;


    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private DocumentReference assigned_report_doc;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private UploadTask uploadTask;
    private String urlLongEncoded;
    private String[] INVESTIGATOR_ARRAY;
    private Bundle mapsBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowHelper.setFullScreenActivity(getWindow());
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();

        mapsBundle = new Bundle();
        getFromBundle(mapsBundle);

        initMap();

        // below code is used for
        // checking our permissions.
        if (!checkPermissionForPDF()) {
            requestPermissionForPDF();
        }

        binding.btnAssignInvestigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnAssignInvestigator.setVisibility(View.GONE);
                binding.generateConstraint.setVisibility(View.VISIBLE);
            }
        });

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.generatedSuccessfully.setVisibility(View.GONE);
                binding.btnAssignInvestigator.setVisibility(View.VISIBLE);
            }
        });

        binding.btnShareUrlOfPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(urlOfPdfUploaded != null){
                    btnShare(urlOfPdfUploaded);
                }
            }
        });

        binding.btnGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.generateReportProgressBar.setVisibility(View.VISIBLE);
                binding.btnGenerateReport.setEnabled(false);
                String first_investigator = binding.theFirstInvestigator.getText().toString();
                String second_investigator = binding.theSecondInvestigator.getText().toString();

                if(TextUtils.isEmpty(first_investigator)){
                    binding.theFirstInvestigator.setError(getText(R.string.error_first_investigator_required));
                    binding.theFirstInvestigator.requestFocus();
                }
                if(TextUtils.isEmpty(second_investigator)){
                    binding.theSecondInvestigator.setError(getText(R.string.error_second_investigator_required));
                    binding.theSecondInvestigator.requestFocus();
                }
                else {
                    try {

                        CollectionReference collRef = firebaseFirestore.collection(ASSIGNED_REPORTS);
                        String reportAssigned_id = collRef.document().getId();

                        assignedReport(getApplicationContext(), reportAssigned_id, last_seen_latitude, last_seen_longitude,
                                lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4, first_investigator, second_investigator, fullName);

                        //create pdf file and upload in firestore and add link option for share
                        generatePDF(reportAssigned_id, first_investigator, second_investigator, last_seen_address, first_address,
                                second_address, third_address, forth_address);

                        binding.generateReportProgressBar.setVisibility(View.INVISIBLE);
                        binding.btnGenerateReport.setEnabled(true);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        setInvestigatorInArray();

    }

    private void getFromBundle(Bundle bundle) {

        try {
            bundle = getIntent().getExtras();
            personId = bundle.getString("personId");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            parentName = bundle.getString("parentName");
            fullName = bundle.getString("fullName");
            birthday = bundle.getString("birthday");
            gender = bundle.getString("gender");
            address = bundle.getString("address");
            age = bundle.getString("age");
            eyeColor = bundle.getString("eyeColor");
            hairColor = bundle.getString("hairColor");
            height = bundle.getString("height");
            weight = bundle.getString("weight");
            phy_appearance = bundle.getString("phy_appearance");
            acts = bundle.getStringArrayList("acts");
            last_seen_longitude = bundle.getDouble("latitude");
            last_seen_longitude = bundle.getDouble("longitude");
            prize = bundle.getString("prize");
            status = bundle.getString("status");
            registration_date = bundle.getString("registration_date");
            urlOfProfile = bundle.getString("urlOfProfile");

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void setInvestigatorInArray() {
        firebaseFirestore.collection(INVESTIGATORS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> investigatorList = new ArrayList<>();
                        for (int i=0; i<queryDocumentSnapshots.getDocuments().size();i++){
                            investigatorList.add(queryDocumentSnapshots.getDocuments().get(i).getString("fullName"));
                        }
                        ArrayAdapter<String> investigator_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, investigatorList);
                        binding.theFirstInvestigator.setAdapter(investigator_adapter);
                        binding.theSecondInvestigator.setAdapter(investigator_adapter);
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_REXHEP_LUCI_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_REXHEP_LUCI_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_RR_REXHEP_LUCI_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_NR2_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_NR2_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_NR2_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_SHESHI_I_LIRISE_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_SHESHI_I_LIRISE_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_SHESHI_I_LIRISE_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_E80_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_E80_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_E80_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_BISLIM_BAJGORA_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_BISLIM_BAJGORA_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_BISLIM_BAJGORA_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_KRALJA_MILUTINA_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_KRALJA_MILUTINA_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_KRALJA_MILUTINA_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_MULLA_IDRIZI_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_MULLA_IDRIZI_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_RR_MULLA_IDRIZI_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_WILLIAM_WALKER_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_WILLIAM_WALKER_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_RR_WILLIAM_WALKER_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_GJAKOVE_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_GJAKOVE_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_GJAKOVE_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_PERLINE_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_PERLINE_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_PERLINE_TITLE)));
        setStationAsMarker(Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_JONI_LATITUDE))), Double.valueOf(String.valueOf(this.getText(R.string.POLICE_STATION_RR_JONI_LONGITUDE))), String.valueOf(this.getText(R.string.POLICE_STATION_RR_JONI_TITLE)));

        setLocations(fullName);
    }

    private void setLocations(String fullName) {
        //ky function i merr locations_reports te wanted person qe nuk jon equal me latest latitude
        // limit 4
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereEqualTo("wanted_person", fullName)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .limit(5)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++)
                        {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            Double latitude = doc.getDouble("latitude");
                            Double longitude = doc.getDouble("longitude");
                            String date_time = doc.getString("date_time");
                            String description = doc.getString("description");

                            if(i==0 && latitude != null && longitude != null)
                            {
                                last_seen_latitude = latitude;
                                last_seen_longitude = longitude;
                                setBorderOnPointWithMoveCamera(getApplicationContext(),
                                        urlOfProfile, 200, 200,
                                        latitude, longitude, getApplicationContext().getString(R.string.last_seen), description, fullName,
                                        R.color.yellow);
                            }
                            else if(i==1 && latitude != null && longitude != null)
                            {
                                lat1 = latitude;
                                lon1 = longitude;
                                setBorderOnPointWithOutMoveCamera(getApplicationContext(),
                                        urlOfProfile, 100, 100,
                                        latitude, longitude, date_time, description, fullName,
                                        R.color.white);
                            }else if(i==2 && latitude != null && longitude != null)
                            {
                                lat2 = latitude;
                                lon2 = longitude;
                                setBorderOnPointWithOutMoveCamera(getApplicationContext(),
                                        urlOfProfile, 100, 100,
                                        latitude, longitude, date_time, description, fullName,
                                        R.color.white);
                            }else if(i==3 && latitude != null && longitude != null)
                            {
                                lat3 = latitude;
                                lon3 = longitude;
                                setBorderOnPointWithOutMoveCamera(getApplicationContext(),
                                        urlOfProfile, 100, 100,
                                        latitude, longitude, date_time, description, fullName,
                                        R.color.white);
                            }else if(i==4 && latitude != null && longitude != null)
                            {
                                lat4 = latitude;
                                lon4 = longitude;
                                setBorderOnPointWithOutMoveCamera(getApplicationContext(),
                                        urlOfProfile, 100, 100,
                                        latitude, longitude, date_time, description, fullName,
                                        R.color.white);
                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setBorderOnPointWithMoveCamera(Context context, String urlOfProfile, int width, int height, Double latitude, Double longitude, String title, String description, String fullName, int borderColorId) {
        Glide.with(context)
                .asBitmap()
                .load(urlOfProfile)
                .apply(new RequestOptions().override(width, height))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap newBitmap = addBorder(resource, getApplicationContext(), borderColorId);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title+" "+fullName)
                                .snippet(description)
                                .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        return true;
                    }
                })
                .circleCrop()
                .preload();
    }

    private void setBorderOnPointWithOutMoveCamera(Context context, String urlOfProfile, int width, int height, Double latitude, Double longitude, String title, String description, String fullName, int borderColorId) {
        Glide.with(context)
                .asBitmap()
                .load(urlOfProfile)
                .apply(new RequestOptions().override(width, height))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap newBitmap = addBorder(resource, getApplicationContext(), borderColorId);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title+" "+fullName)
                                .snippet(description)
                                .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                        return true;
                    }
                })
                .circleCrop()
                .preload();
    }

    private void setStationAsMarker(Double latitude, Double longitude, String police_station_title) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(police_station_title)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_police_google)));
    }

    private void assignedReport(Context ctx,
                                String reportAssigned_id,
                                double lat1, double lon1,
                                double lat2, double lon2,
                                double lat3, double lon3,
                                double lat4, double lon4,
                                double lat5, double lon5,
                                String first_investigator, String second_investigator,
                                String fullNameOfWantedPerson) {

        last_seen_address = getAddress(ctx, lat1, lon1);
        first_address = getAddress(ctx, lat2, lon2);
        second_address = getAddress(ctx, lat3, lon3);
        third_address = getAddress(ctx, lat4, lon4);
        forth_address = getAddress(ctx, lat5, lon5);
        date = DateHelper.getDateTime();

        ReportAssigned reportAssigned = new ReportAssigned(reportAssigned_id, first_investigator, second_investigator, fullNameOfWantedPerson
                ,last_seen_address, first_address, second_address, third_address, forth_address, date);


        firebaseFirestore.collection(ASSIGNED_REPORTS).document(reportAssigned_id).set(reportAssigned).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ctx, R.string.report_assigned_successfully, Toast.LENGTH_SHORT).show();
                binding.generateConstraint.setVisibility(View.GONE);
                binding.generatedSuccessfully.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getAddress(Context ctx, double latitude, double longitude) {
        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);
            }
        }catch (IOException e) {
            Toast.makeText(ctx, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return fullAdd;
    }

    private void uploadPDFtoFirebase(Uri pdfUri, String reportAssigned_id) {
        //upload image to storage in firebase
        StorageReference profRef = storageReference.child(ASSIGNED_REPORTS+"/"+firebaseAuth.getCurrentUser().getUid()+"/"+fullName+"/"+reportAssigned_id+"/"+ASSIGNED_REPORTS_PDF);
        profRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save uri of pdf uploaded file
                        urlOfPdfUploaded = uri.toString();
                        setShortUrl(uri);
                        saveReportUrlToUserCollection(reportAssigned_id, urlOfPdfUploaded);
                        saveReportUrlToReportCollection(reportAssigned_id, assigned_report_doc, firebaseFirestore, urlOfPdfUploaded, ASSIGNED_REPORTS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setShortUrl(Uri link) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(link)
                .setDomainUriPrefix(DYNAMIC_DOMAIN)
                // Set parameters
                // ...
                .buildShortDynamicLink();

        shortLinkTask.addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {
                    // Short link created
                    Uri shortLink = task.getResult().getShortLink();
                    System.out.println(shortLink.toString());
                } else {
                    Toast.makeText(MapsActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveReportUrlToReportCollection(String reportAssigned_id, DocumentReference documentReference, FirebaseFirestore firebaseFirestore, String urlOfPdfUploaded,
                                                 String collection) {
        if(date!=null) {
            documentReference = firebaseFirestore.collection(collection).document(reportAssigned_id);
            documentReference.update("assigned_report_url", urlOfPdfUploaded).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task != null && task.isSuccessful()) {
                        Toast.makeText(MapsActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MapsActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveReportUrlToUserCollection(String reportAssigned_id, String urlOfPdfUploaded) {
        ReportAssignedUser reportAssignedUser = new ReportAssignedUser(reportAssigned_id, urlOfPdfUploaded);

        firebaseFirestore.collection(USERS)
                .document(firebaseAuth.getUid())
                .collection(REPORTS_ASSIGNED)
                .document(reportAssigned_id)
                .set(reportAssignedUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MapsActivity.this, getApplicationContext().getText(R.string.successfully), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkPermissionForPDF() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int permission3 = ContextCompat.checkSelfPermission(getApplicationContext(), MANAGE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED && permission3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForPDF() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void generatePDF(String reportAssigned_id, String first_investigator, String second_investigator,
                             String last_seen_address, String first_address,
                             String second_address, String third_address,
                             String forth_address) throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        // File directory = getFilesDir();
        File file = new File(pdfPath, "GFG.pdf");
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);

        Document document = new Document(pdfDocument, PageSize.A4);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        document.setMargins(15,15,15,15);

        Drawable ic_republic_of_kosovo = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_republic_of_kosovo);
        Bitmap bmp_ic_republic_of_kosovo = ((BitmapDrawable)ic_republic_of_kosovo).getBitmap();
        ByteArrayOutputStream stream_ic_republic_of_kosovo = new ByteArrayOutputStream();
        bmp_ic_republic_of_kosovo.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_republic_of_kosovo);
        byte[] bitmap_data_ic_republic_of_kosovo = stream_ic_republic_of_kosovo.toByteArray();

        ImageData image_data_ic_republic_of_kosovo = ImageDataFactory.create(bitmap_data_ic_republic_of_kosovo);
        Image image_ic_republic_of_kosovo = new Image(image_data_ic_republic_of_kosovo);

        Drawable ic_kp = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_kp_10_opacity);
        Bitmap bmp_ic_kp = ((BitmapDrawable)ic_kp).getBitmap();
        ByteArrayOutputStream stream_ic_kp = new ByteArrayOutputStream();
        bmp_ic_kp.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_kp);
        byte[] bitmap_data_ic_kp = stream_ic_kp.toByteArray();

        ImageData image_data_ic_kp = ImageDataFactory.create(bitmap_data_ic_kp);
        Image image_ic_kp = new Image(image_data_ic_kp);

        // Initial point of the line in begin
        canvas.moveTo(0, pdfDocument.getDefaultPageSize().getHeight()/1.225);

        // Drawing the line in begin
        canvas.lineTo(pdfDocument.getDefaultPageSize().getWidth(), pdfDocument.getDefaultPageSize().getHeight()/1.225);

        //image ic_kp as background of pdf
        canvas.addImage(image_data_ic_kp, 0, 0, pdfDocument.getDefaultPageSize().getWidth(), false);
        canvas.restoreState();

        DeviceRgb setColor = new DeviceRgb(8, 106, 119);
        float columnWidth[] = {120, 200, 120, 120};
        Table table1 = new Table(columnWidth);
        table1.setMargin(20);

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.location_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.address_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.latitude_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.longitude_cell)))));

        for(int i = 0; i<5; i++)
        {
            if(i==0 && last_seen_address != null)
            {
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.last_seen_location)))));
                table1.addCell(new Cell().add(new Paragraph(last_seen_address)));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(last_seen_latitude))));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(last_seen_longitude))));
            }else if(i==1 && first_address != null)
            {
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.first_location)))));
                table1.addCell(new Cell().add(new Paragraph(first_address)));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat1))));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon1))));
            }else if(i==2 && second_address != null)
            {
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.second_location)))));
                table1.addCell(new Cell().add(new Paragraph(second_address)));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat2))));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon2))));
            }else if(i==3 && third_address != null)
            {
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.third_location)))));
                table1.addCell(new Cell().add(new Paragraph(third_address)));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat3))));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon3))));
            }else if(i==4 && forth_address != null)
            {
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.forth_location)))));
                table1.addCell(new Cell().add(new Paragraph(forth_address)));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat4))));
                table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon4))));
            }
        }

        table1.setHorizontalAlignment(HorizontalAlignment.CENTER);

        image_ic_republic_of_kosovo.setWidth(80);
        image_ic_republic_of_kosovo.setHeight(90);

        image_ic_kp.setWidth(100);
        image_ic_kp.setHeight(100);

        image_ic_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);

        image_ic_kp.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph republika_e_kosoves = new Paragraph(String.valueOf(this.getText(R.string.republika_e_kosoves)));
        Paragraph republika_kosovo_republic_of_kosovo = new Paragraph(String.valueOf(this.getText(R.string.republika_kosovo_republic_of_kosovo)));
        Paragraph mpb_three_language = new Paragraph(String.valueOf(this.getText(R.string.mpb_three_language)));
        Paragraph confidential_report = new Paragraph(String.valueOf(this.getText(R.string.confidential_report)));
        Paragraph location_report_of = new Paragraph(this.getText(R.string.location_reports_of)+" "+fullName);
        Paragraph _first_investigator = new Paragraph(this.getText(R.string.investigator)+" "+first_investigator);
        Paragraph _second_investigator = new Paragraph(this.getText(R.string.investigator)+" "+second_investigator);

        _first_investigator.setFixedPosition(60, 60, _first_investigator.getWidth());
        _second_investigator.setFixedPosition(430, 60, _second_investigator.getWidth());


        canvas.moveTo(55, pdfDocument.getDefaultPageSize().getHeight()-800);
        canvas.lineTo(160, pdfDocument.getDefaultPageSize().getHeight()-800);

        canvas.moveTo(425, pdfDocument.getDefaultPageSize().getHeight()-800);
        canvas.lineTo(530, pdfDocument.getDefaultPageSize().getHeight()-800);


        republika_e_kosoves.setMultipliedLeading(0.5f);
        republika_kosovo_republic_of_kosovo.setMultipliedLeading(0.5f);
        mpb_three_language.setMultipliedLeading(0.5f);

        republika_e_kosoves.setFontSize(10f);
        republika_kosovo_republic_of_kosovo.setFontSize(10f);
        mpb_three_language.setFontSize(10f);
        confidential_report.setFontSize(22f);
        location_report_of.setFontSize(18f);

        republika_e_kosoves.setHorizontalAlignment(HorizontalAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        mpb_three_language.setHorizontalAlignment(HorizontalAlignment.CENTER);
        confidential_report.setHorizontalAlignment(HorizontalAlignment.CENTER);
        location_report_of.setHorizontalAlignment(HorizontalAlignment.CENTER);

        republika_e_kosoves.setTextAlignment(TextAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setTextAlignment(TextAlignment.CENTER);
        mpb_three_language.setTextAlignment(TextAlignment.CENTER);
        confidential_report.setTextAlignment(TextAlignment.CENTER);
        location_report_of.setTextAlignment(TextAlignment.CENTER);

        confidential_report.setMarginTop(pdfDocument.getDefaultPageSize().getHeight()/5);

        document.add(image_ic_republic_of_kosovo);
        document.add(republika_e_kosoves);
        document.add(republika_kosovo_republic_of_kosovo);
        document.add(mpb_three_language);
        document.add(confidential_report);
        document.add(location_report_of);
        document.add(table1);
        document.add(_first_investigator);
        document.add(_second_investigator);
        document.close();

        Uri uri = Uri.fromFile(file);
        uploadPDFtoFirebase(uri, reportAssigned_id);

        Toast.makeText(this, R.string.pdf_file_generated_successfully, Toast.LENGTH_SHORT).show();
    }

    private void btnShare(String sub) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = String.valueOf(this.getText(R.string.share_pdf_generated_link));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_TEXT, sub);
        startActivity(Intent.createChooser(intent, body));
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAdmin);
        mapFragment.getMapAsync(this);
    }

}
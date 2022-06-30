package fiek.unipr.mostwantedapp.maps;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsBinding;
import fiek.unipr.mostwantedapp.helpers.ScalingUtilities;
import fiek.unipr.mostwantedapp.models.ReportAssigned;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String newLatitude, newLongitude, fullName, status, urlOfProfile, uID;
    private String last_seen_address, first_address, second_address, third_address, forth_address;
    private double latitude, longitude;
    private double lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4;

    // declaring width and height
    // for our PDF file. exchange width with height if u want landscape
    private int A4_PORTRAIT_pageWidth = 595;
    private int A4_PORTRAIT_pageHeight = 842;

    // creating a bitmap variable
    // for storing our images
    private Bitmap bmp, scaledbmp, kp_bmp, kp_scaledbmp, profile_picture_bmp, profile_picture_scaledbmp;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    private static String POLICE_STATION_RR_REXHEP_LUCI_TITLE = "POLICE STATION - Rr. Rexhep Luci, Prishtine 10000";
    private static double POLICE_STATION_RR_REXHEP_LUCI_LATITUDE = 42.6626792;
    private static double POLICE_STATION_RR_REXHEP_LUCI_LONGITUDE = 21.1572832;

    private static String POLICE_STATION_NR2_TITLE = "POLICE STATION Nr.2 - Hamez Jashari, Prishtine 10000";
    private static double POLICE_STATION_NR2_LATITUDE = 42.659114;
    private static double POLICE_STATION_NR2_LONGITUDE = 21.167420;

    private static String POLICE_STATION_SHESHI_I_LIRISE_TITLE = "POLICE STATION - Sheshi i Lirise, Fushe Kosove 12000";
    private static double POLICE_STATION_SHESHI_I_LIRISE_LATITUDE = 42.6347623;
    private static double POLICE_STATION_SHESHI_I_LIRISE_LONGITUDE = 21.0848419;

    private static String POLICE_STATION_E80_TITLE = "POLICE STATION - E80, Prishtine 10000";
    private static double POLICE_STATION_E80_LATITUDE = 42.681564;
    private static double POLICE_STATION_E80_LONGITUDE = 21.159672;

    private static String POLICE_STATION_BISLIM_BAJGORA_TITLE = "POLICE STATION - Bislim Bajgora, Mitrovice 40000";
    private static double POLICE_STATION_BISLIM_BAJGORA_LATITUDE = 42.890092;
    private static double POLICE_STATION_BISLIM_BAJGORA_LONGITUDE = 20.874400;

    private static String POLICE_STATION_KRALJA_MILUTINA_TITLE = "POLICE STATION - Kralja Milutina, Graçanice 10500";
    private static double POLICE_STATION_KRALJA_MILUTINA_LATITUDE = 42.601494;
    private static double POLICE_STATION_KRALJA_MILUTINA_LONGITUDE = 21.192242;

    private static String POLICE_STATION_RR_MULLA_IDRIZI_TITLE = "POLICE STATION - Rr. Mulla Idrizi, Gjilan 60000";
    private static double POLICE_STATION_RR_MULLA_IDRIZI_LATITUDE = 42.4631162;
    private static double POLICE_STATION_RR_MULLA_IDRIZI_LONGITUDE = 21.4696013;

    private static String POLICE_STATION_RR_JONI_TITLE = "POLICE STATION - Rr. JONI, Prizren 20000";
    private static double POLICE_STATION_RR_JONI_LATITUDE = 42.214859;
    private static double POLICE_STATION_RR_JONI_LONGITUDE = 20.732425;

    private static String POLICE_STATION_RR_WILLIAM_WALKER_TITLE = "POLICE STATION ALPHA - Rr. William Walker, Prizren 20000";
    private static double POLICE_STATION_RR_WILLIAM_WALKER_LATITUDE = 42.210322;
    private static double POLICE_STATION_RR_WILLIAM_WALKER_LONGITUDE = 20.730256;

    private static String POLICE_STATION_GJAKOVE_TITLE = "POLICE STATION - Gjakove 20000";
    private static double POLICE_STATION_GJAKOVE_LATITUDE = 42.375681;
    private static double POLICE_STATION_GJAKOVE_LONGITUDE = 20.442102;

    private static String POLICE_STATION_PERLINE_TITLE = "POLICE STATION - Perline 32000";
    private static double POLICE_STATION_PERLINE_LATITUDE = 42.609493;
    private static double POLICE_STATION_PERLINE_LONGITUDE = 20.575675;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = firebaseUser.getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAdmin);
        mapFragment.getMapAsync(this);

        // below code is used for
        // checking our permissions.
        if (checkPermissionForPDF()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionForPDF();
        }

        try {
            Intent in = getIntent();
            if(in != null)
            {
                newLatitude = in.getStringExtra("latitude");
                newLongitude = in.getStringExtra("longitude");
                fullName = in.getStringExtra("fullName");
                status = in.getStringExtra("status");
                urlOfProfile = in.getStringExtra("urlOfProfile");

            }else {
                newLongitude = newLongitude;
                newLatitude = newLatitude;
            }
        }catch (Exception e) {
            e.getMessage();
        }

        binding.btnAssignInvestigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnAssignInvestigator.setVisibility(View.GONE);
                binding.generateConstraint.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setStationAsMarker(POLICE_STATION_RR_REXHEP_LUCI_LATITUDE, POLICE_STATION_RR_REXHEP_LUCI_LONGITUDE, POLICE_STATION_RR_REXHEP_LUCI_TITLE);
        setStationAsMarker(POLICE_STATION_NR2_LATITUDE, POLICE_STATION_NR2_LONGITUDE, POLICE_STATION_NR2_TITLE);
        setStationAsMarker(POLICE_STATION_SHESHI_I_LIRISE_LATITUDE, POLICE_STATION_SHESHI_I_LIRISE_LONGITUDE, POLICE_STATION_SHESHI_I_LIRISE_TITLE);
        setStationAsMarker(POLICE_STATION_E80_LATITUDE, POLICE_STATION_E80_LONGITUDE, POLICE_STATION_E80_TITLE);
        setStationAsMarker(POLICE_STATION_BISLIM_BAJGORA_LATITUDE, POLICE_STATION_BISLIM_BAJGORA_LONGITUDE, POLICE_STATION_BISLIM_BAJGORA_TITLE);
        setStationAsMarker(POLICE_STATION_KRALJA_MILUTINA_LATITUDE, POLICE_STATION_KRALJA_MILUTINA_LONGITUDE, POLICE_STATION_KRALJA_MILUTINA_TITLE);
        setStationAsMarker(POLICE_STATION_RR_MULLA_IDRIZI_LATITUDE, POLICE_STATION_RR_MULLA_IDRIZI_LONGITUDE, POLICE_STATION_RR_MULLA_IDRIZI_TITLE);
        setStationAsMarker(POLICE_STATION_RR_WILLIAM_WALKER_LATITUDE, POLICE_STATION_RR_WILLIAM_WALKER_LONGITUDE, POLICE_STATION_RR_WILLIAM_WALKER_TITLE);
        setStationAsMarker(POLICE_STATION_GJAKOVE_LATITUDE, POLICE_STATION_GJAKOVE_LONGITUDE, POLICE_STATION_GJAKOVE_TITLE);
        setStationAsMarker(POLICE_STATION_PERLINE_LATITUDE, POLICE_STATION_PERLINE_LONGITUDE, POLICE_STATION_PERLINE_TITLE);
        setStationAsMarker(POLICE_STATION_RR_JONI_LATITUDE, POLICE_STATION_RR_JONI_LONGITUDE, POLICE_STATION_RR_JONI_TITLE);

        setLocations(fullName, newLatitude, newLongitude);

        try {
            Glide.with(MapsActivity.this)
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
                            LatLng latLng = new LatLng(Double.valueOf(newLatitude), Double.valueOf(newLongitude));
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(getApplicationContext().getText(R.string.last_seen)+" "+fullName)
                                    .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                            return true;
                        }
                    })
                    .circleCrop()
                    .preload();
        } catch(Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


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

    private void setLocations(String fullName, String newLatitude, String newLongitude) {
        //select all location reports where fullname is like this
        firebaseFirestore.collection("locations_reports")
                .whereEqualTo("wanted_person", fullName)
                .whereNotEqualTo("latitude", newLatitude)
                .limit(4)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            Double latitude = doc.getDouble("latitude");
                            Double longitude = doc.getDouble("longitude");
                            String date_time = doc.getString("date_time");
                            String description = "";
                            description = doc.getString("description");

                            if(i==0){
                                lat1 = latitude;
                                lon1 = longitude;
                            }

                            if(i==1){
                                lat2 = latitude;
                                lon2 = longitude;
                            }

                            if(i==2){
                                lat3 = latitude;
                                lon3 = longitude;
                            }

                            if(i==3){
                                lat4 = latitude;
                                lon4 = longitude;
                            }

                            getImageOfWantedPerson(fullName);

                            //we can use urlOfProfile value if it is not null
                            if(urlOfProfile != null){
                                try {
                                    int finalI = i;
                                    String finalDescription = description;
                                    Glide.with(MapsActivity.this)
                                            .asBitmap()
                                            .load(urlOfProfile)
                                            .apply(new RequestOptions().override(100, 100))
                                            .listener(new RequestListener<Bitmap>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                    LatLng latLng = new LatLng(latitude, longitude);
                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .title(date_time+" "+fullName)
                                                            .snippet(finalDescription)
                                                            .icon(BitmapDescriptorFactory.fromBitmap(resource)));
                                                    mMap.setMinZoomPreference(17);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                    return true;
                                                }
                                            })
                                            .circleCrop()
                                            .preload();
                                }catch (Exception e){
                                    Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    System.out.println(e.getMessage());
                                }
                            }

                        }

                        binding.btnGenerateReport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String first_investigator = binding.theFirstInvestigator.getText().toString();
                                String second_investigator = binding.theSecondInvestigator.getText().toString();
                                assignedReport(getApplicationContext(), Double.valueOf(newLatitude), Double.valueOf(newLongitude),
                                        lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4, first_investigator, second_investigator, fullName);

                                //create pdf file and upload in firestore and add link option for share
                                try {
                                    generatePDF(first_investigator, second_investigator, last_seen_address, first_address,
                                            second_address, third_address, forth_address);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setStationAsMarker(Double latitude, Double longitude, String police_station_title) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(police_station_title)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_police_google)));
    }

    private void getImageOfWantedPerson(String fullName) {
        firebaseFirestore.collection("wanted_persons")
                .whereEqualTo("fullName", fullName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots != null){
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            urlOfProfile = doc.getString("urlOfProfile");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void assignedReport(Context ctx, double newLatitude, double newLongitude,
                                double lat1, double lon1,
                                double lat2, double lon2,
                                double lat3, double lon3,
                                double lat4, double lon4,
                                String first_investigator, String second_investigator,
                                String fullNameOfWantedPerson) {

        last_seen_address = getAddress(ctx, newLatitude, newLongitude);
        first_address = getAddress(ctx, lat1, lon1);
        second_address = getAddress(ctx, lat2, lon2);
        third_address = getAddress(ctx, lat3, lon3);
        forth_address = getAddress(ctx, lat4, lon4);

        ReportAssigned reportAssigned = new ReportAssigned(first_investigator, second_investigator, fullNameOfWantedPerson
                ,last_seen_address, first_address, second_address, third_address, forth_address);

        firebaseFirestore.collection("assigned_reports").document(getTimeDate()).set(reportAssigned).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ctx, R.string.report_assigned_successfully, Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, R.string.report_failed_to_assign, Toast.LENGTH_SHORT).show();
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


    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    private void uploadPDFtoFirebase(Uri pdfUri) {
        //upload image to storage in firebase
        StorageReference profRef = storageReference.child("assigned_reports/"+firebaseAuth.getCurrentUser().getUid()+"/"+getTimeDate()+"/"+fullName+"/assign_report.pdf");
        profRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                        documentReference.update("latest_assigned_report", uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task != null && task.isSuccessful()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MapsActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }, 3000);
                                }else {
                                    Toast.makeText(MapsActivity.this, "No pdf uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

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

    private void generatePDF(String first_investigator, String second_investigator,
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

        canvas.addImage(image_data_ic_kp, 0, pdfDocument.getDefaultPageSize().getHeight()/2+image_data_ic_kp.getHeight()/2, pdfDocument.getDefaultPageSize().getWidth(), false);
        canvas.restoreState();

        DeviceRgb setColor = new DeviceRgb(8, 106, 119);
        float columnWidth[] = {220, 120, 100, 120};
        Table table1 = new Table(columnWidth);
        table1.setMargin(20);

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.location_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.address_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.latitude_cell)))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.longitude_cell)))));

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.last_seen_location)))));
        table1.addCell(new Cell().add(new Paragraph(last_seen_address)));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(newLatitude))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(newLongitude))));

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.first_location)))));
        table1.addCell(new Cell().add(new Paragraph(first_address)));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat1))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon1))));

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.second_location)))));
        table1.addCell(new Cell().add(new Paragraph(second_address)));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat2))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon2))));

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.third_location)))));
        table1.addCell(new Cell().add(new Paragraph(third_address)));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat3))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon3))));

        table1.addCell(new Cell().add(new Paragraph(String.valueOf(this.getText(R.string.forth_location)))));
        table1.addCell(new Cell().add(new Paragraph(forth_address)));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lat4))));
        table1.addCell(new Cell().add(new Paragraph(String.valueOf(lon4))));

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
        Paragraph _first_investigator = new Paragraph(this.getText(R.string.mr)+" "+first_investigator);
        Paragraph _second_investigator = new Paragraph(this.getText(R.string.mr)+" "+second_investigator);

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
        uploadPDFtoFirebase(uri);

        Toast.makeText(this, R.string.pdf_file_generated_successfully, Toast.LENGTH_SHORT).show();
    }
}
package fiek.unipr.mostwantedapp.activity.maps.admin;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static fiek.unipr.mostwantedapp.utils.BitmapHelper.BitmapFromVector;
import static fiek.unipr.mostwantedapp.utils.BitmapHelper.addBorder;
import static fiek.unipr.mostwantedapp.utils.BitmapHelper.drawableFromUrl;
import static fiek.unipr.mostwantedapp.utils.BitmapHelper.getCroppedBitmap;
import static fiek.unipr.mostwantedapp.utils.Constants.APPEARANCE_MODE_PREFERENCE;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.DARK_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.DEFAULT_ZOOM;
import static fiek.unipr.mostwantedapp.utils.Constants.INVESTIGATORS;
import static fiek.unipr.mostwantedapp.utils.Constants.LOCATION_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.REPORTS_ASSIGNED;
import static fiek.unipr.mostwantedapp.utils.Constants.SYSTEM_MODE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.databinding.ActivityMapsBinding;
import fiek.unipr.mostwantedapp.models.ReportAssigned;
import fiek.unipr.mostwantedapp.models.ReportAssignedUser;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.WindowHelper;

public class MapAdminActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context mContext;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private String uID, urlOfPdfUploaded, date, personId, firstName, lastName, parentName, fullName, birthday, gender, address, age, eyeColor, hairColor, height,
            phy_appearance, status, prize, urlOfProfile, weight, registration_date;

    private double last_seen_latitude, last_seen_longitude, lat1, lon1;

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
    private Bundle mapsBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowHelper.setFullScreenActivity(getWindow());
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = getApplicationContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        mapsBundle = new Bundle();
        getFromBundle(mapsBundle);

        initMap();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
                if (urlOfPdfUploaded != null) {
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

                if (TextUtils.isEmpty(first_investigator)) {
                    binding.theFirstInvestigator.setError(getText(R.string.error_first_investigator_required));
                    binding.theFirstInvestigator.requestFocus();
                    binding.generateReportProgressBar.setVisibility(View.INVISIBLE);
                    binding.btnGenerateReport.setEnabled(true);
                } else if (TextUtils.isEmpty(second_investigator)) {
                    binding.theSecondInvestigator.setError(getText(R.string.error_second_investigator_required));
                    binding.theSecondInvestigator.requestFocus();
                    binding.generateReportProgressBar.setVisibility(View.INVISIBLE);
                    binding.btnGenerateReport.setEnabled(true);
                } else if (first_investigator.equals(second_investigator)) {
                    binding.theSecondInvestigator.setError(getText(R.string.error_not_allowed_same_investigator));
                    binding.theSecondInvestigator.requestFocus();
                    binding.generateReportProgressBar.setVisibility(View.INVISIBLE);
                    binding.btnGenerateReport.setEnabled(true);
                } else {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                saveAndGeneratePDF(first_investigator, second_investigator);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        setInvestigatorInArray();

    }

    private void saveAndGeneratePDF(String first_investigator, String second_investigator) throws IOException {
        CollectionReference collRef = firebaseFirestore.collection(ASSIGNED_REPORTS);
        String reportAssigned_id = collRef.document().getId();

        //qyty duhet me i marr prap locations report
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereEqualTo("personId", personId)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            int rows = queryDocumentSnapshots.size();
                            int column = 5;
                            String[][] locations = new String[rows][column];
                            List<String> listLocations = new ArrayList<>();
                            // Creating a table
                            float[] pointColumnWidths = {20F, 200F, 200F, 200F, 200F};
                            Table table = new Table(pointColumnWidths);
                            try {
                                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.num)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.latitude_cell)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.longitude_cell)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.address_cell)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.document_id)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                                String number = i + "";
                                String latitude = String.valueOf(queryDocumentSnapshots.getDocuments().get(i).getDouble("latitude"));
                                String longitude = String.valueOf(queryDocumentSnapshots.getDocuments().get(i).getDouble("longitude"));
                                String address = getAddress(mContext, Double.parseDouble(latitude), Double.parseDouble(longitude));
                                String docId = queryDocumentSnapshots.getDocuments().get(i).getString("docId");

                                // i marrum krejt lokacionet i shtim ni list edhe ja qojm assigned report si list
                                // edhe gjenerate pdf ja qojm si list
                                // 0, latitude, longitude, address, docId
                                // 1. latitude, longitude, address, docId
                                // 2. latitude longitude, address, docId
                                locations[i][0] = number;
                                locations[i][1] = latitude;
                                locations[i][2] = longitude;
                                locations[i][3] = address;
                                locations[i][4] = docId;

                                Log.d("TABLE", " " + locations[i][0] + " " + locations[i][1] + " " + locations[i][2] + " " + locations[i][3] + " " + locations[i][4]);
                                table.addCell(new Cell().add(new Paragraph(locations[i][0])));
                                table.addCell(new Cell().add(new Paragraph(locations[i][1])));
                                table.addCell(new Cell().add(new Paragraph(locations[i][2])));
                                table.addCell(new Cell().add(new Paragraph(locations[i][3])));
                                table.addCell(new Cell().add(new Paragraph(locations[i][4])));

                                listLocations.add(mContext.getText(R.string.location) + locations[i][0] + ", " + locations[i][1] + ", " + locations[i][2] + ", " + locations[i][3] + ", " + locations[i][4]);
                            }

                            assignedReport(mContext, listLocations, reportAssigned_id, first_investigator, second_investigator, fullName);

                            //create pdf file and upload in firestore and add link option for share
                            Uri uri = null;
                            try {
                                uri = generatePDF(first_investigator, second_investigator, table);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            uploadPDFtoFirebase(uri, reportAssigned_id);
                        }
                    }
                });
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
            Log.d("TAG", e.getMessage());
        }

    }

    private void setInvestigatorInArray() {
        firebaseFirestore.collection(INVESTIGATORS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> investigatorList = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                            investigatorList.add(queryDocumentSnapshots.getDocuments().get(i).getString("fullName"));
                        }
                        ArrayAdapter<String> investigator_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, investigatorList);
                        binding.theFirstInvestigator.setAdapter(investigator_adapter);
                        binding.theSecondInvestigator.setAdapter(investigator_adapter);
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkAndSetModeMap(mContext);
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

        setLocations(personId);
    }

    private void checkAndSetModeMap(Context mContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String mode = sharedPreferences.getString(APPEARANCE_MODE_PREFERENCE, SYSTEM_MODE);
        int nightModeFlags = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode.equals(DARK_MODE) || (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_in_night));
            binding.tvPdfGeneratedSuccesfully.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.generatedSuccessfully.setBackgroundResource(R.drawable.bg_report_persistent_dark);
            binding.generateConstraint.setBackgroundResource(R.drawable.bg_report_persistent_dark);
//
            binding.theFirstInvestigator.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.theFirstInvestigator.setHighlightColor(mContext.getResources().getColor(R.color.white));
            binding.theFirstInvestigator.setLinkTextColor(mContext.getResources().getColor(R.color.white));
            binding.theFirstInvestigator.setHintTextColor(mContext.getResources().getColor(R.color.white));

            binding.theSecondInvestigator.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.theSecondInvestigator.setHighlightColor(mContext.getResources().getColor(R.color.white));
            binding.theSecondInvestigator.setLinkTextColor(mContext.getResources().getColor(R.color.white));
            binding.theSecondInvestigator.setHintTextColor(mContext.getResources().getColor(R.color.white));

            binding.autocompleteTheFirstInvestigatorLayout.setBoxBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.autocompleteTheFirstInvestigatorLayout.setHintTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));

            binding.autocompleteTheSecondInvestigatorLayout.setBoxBackgroundColor(mContext.getResources().getColor(R.color.darkmode));
            binding.autocompleteTheSecondInvestigatorLayout.setHintTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
        }
    }

    private void setLocations(String personId) {
        //ky function i merr locations_reports te wanted person qe nuk jon equal me latest latitude
        firebaseFirestore.collection(LOCATION_REPORTS)
                .whereEqualTo("personId", personId)
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(i);
                            Double latitude = doc.getDouble("latitude");
                            Double longitude = doc.getDouble("longitude");
                            String date_time = doc.getString("date_time");
                            String description = doc.getString("description");

                            if (i == 0 && latitude != null && longitude != null) {
                                last_seen_latitude = latitude;
                                last_seen_longitude = longitude;
                                setBorderOnPointWithMoveCamera(mContext,
                                        urlOfProfile, 200, 200,
                                        latitude, longitude, mContext.getString(R.string.last_seen), description, fullName,
                                        R.color.white);
                            } else if (latitude != null && longitude != null) {
                                lat1 = latitude;
                                lon1 = longitude;
                                setBorderOnPointWithOutMoveCamera(mContext,
                                        urlOfProfile, 100, 100,
                                        latitude, longitude, date_time, description, fullName,
                                        R.color.white);
                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapAdminActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Bitmap newBitmap = addBorder(resource, mContext, borderColorId);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title + " " + fullName)
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
                        Bitmap newBitmap = addBorder(resource, mContext, borderColorId);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title + " " + fullName)
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
                .icon(BitmapFromVector(mContext, R.drawable.ic_police_google)));
    }

    private void assignedReport(Context context,
                                List<String> listLocations,
                                String reportAssigned_id,
                                String first_investigator, String second_investigator,
                                String fullNameOfWantedPerson) {

        ReportAssigned reportAssigned = new ReportAssigned(
                reportAssigned_id,
                first_investigator,
                second_investigator,
                fullNameOfWantedPerson,
                DateHelper.getDateTime(),
                listLocations
        );

        firebaseFirestore.collection(ASSIGNED_REPORTS).document(reportAssigned_id).set(reportAssigned)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void uploadPDFtoFirebase(Uri pdfUri, String reportAssigned_id) {
        //upload image to storage in firebase
        StorageReference profRef = storageReference.child(ASSIGNED_REPORTS + "/" + firebaseAuth.getCurrentUser().getUid() + "/" + fullName + "/" + reportAssigned_id + "/" + ASSIGNED_REPORTS_PDF);
        profRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save uri of pdf uploaded file
                        urlOfPdfUploaded = uri.toString();
                        saveReportUrlToUserCollection(reportAssigned_id, urlOfPdfUploaded);
                        saveReportUrlToReportCollection(reportAssigned_id, assigned_report_doc, firebaseFirestore, urlOfPdfUploaded);
                        binding.generateConstraint.setVisibility(View.GONE);
                        binding.generatedSuccessfully.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapAdminActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveReportUrlToReportCollection(String reportAssigned_id, DocumentReference documentReference, FirebaseFirestore firebaseFirestore, String urlOfPdfUploaded) {
        if (date != null) {
            documentReference = firebaseFirestore.collection(ASSIGNED_REPORTS).document(reportAssigned_id);
            documentReference.update("assigned_report_url", urlOfPdfUploaded).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MapAdminActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveReportUrlToUserCollection(String reportAssigned_id, String urlOfPdfUploaded) {
        ReportAssignedUser reportAssignedUser = new ReportAssignedUser(reportAssigned_id, urlOfPdfUploaded);

        firebaseFirestore.collection(USERS)
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection(REPORTS_ASSIGNED)
                .document(reportAssigned_id)
                .set(reportAssignedUser)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapAdminActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkPermissionForPDF() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(mContext, WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(mContext, READ_EXTERNAL_STORAGE);
        int permission3 = ContextCompat.checkSelfPermission(mContext, MANAGE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED && permission3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForPDF() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private Uri generatePDF(String first_investigator, String second_investigator, Table table) throws IOException {

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

        document.setMargins(15, 15, 15, 15);

        Drawable ic_republic_of_kosovo = ContextCompat.getDrawable(mContext, R.drawable.ic_republic_of_kosovo);
        assert ic_republic_of_kosovo != null;
        Bitmap bmp_ic_republic_of_kosovo = ((BitmapDrawable) ic_republic_of_kosovo).getBitmap();
        ByteArrayOutputStream stream_ic_republic_of_kosovo = new ByteArrayOutputStream();
        bmp_ic_republic_of_kosovo.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_republic_of_kosovo);
        byte[] bitmap_data_ic_republic_of_kosovo = stream_ic_republic_of_kosovo.toByteArray();

        ImageData image_data_ic_republic_of_kosovo = ImageDataFactory.create(bitmap_data_ic_republic_of_kosovo);
        Image image_ic_republic_of_kosovo = new Image(image_data_ic_republic_of_kosovo);

        Drawable ic_kp = ContextCompat.getDrawable(mContext, R.drawable.ic_kp_10_opacity);
        assert ic_kp != null;
        Bitmap bmp_ic_kp = ((BitmapDrawable) ic_kp).getBitmap();
        ByteArrayOutputStream stream_ic_kp = new ByteArrayOutputStream();
        bmp_ic_kp.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_kp);
        byte[] bitmap_data_ic_kp = stream_ic_kp.toByteArray();

        ImageData image_data_ic_kp = ImageDataFactory.create(bitmap_data_ic_kp);
        Image image_ic_kp = new Image(image_data_ic_kp);

        Drawable profile = drawableFromUrl(urlOfProfile);
        Bitmap bmp_profile = ((BitmapDrawable) profile).getBitmap();
        Bitmap bmp_circle_profile = getCroppedBitmap(bmp_profile);
        ByteArrayOutputStream stream_profile = new ByteArrayOutputStream();
        bmp_circle_profile.compress(Bitmap.CompressFormat.PNG, 100, stream_profile);
        byte[] bitmap_data_profile = stream_profile.toByteArray();

        ImageData image_data_profile = ImageDataFactory.create(bitmap_data_profile);
        Image image_profile = new Image(image_data_profile);

//        // Initial point of the line in begin
//        canvas.moveTo(0, pdfDocument.getDefaultPageSize().getHeight() / 1.225);
//
//        // Drawing the line in begin
//        canvas.lineTo(pdfDocument.getDefaultPageSize().getWidth(), pdfDocument.getDefaultPageSize().getHeight() / 1.225);

        //image ic_kp as background of pdf
        canvas.addImage(image_data_ic_kp, 0, 0, pdfDocument.getDefaultPageSize().getWidth(), false);
        canvas.restoreState();

        DeviceRgb setColor = new DeviceRgb(8, 106, 119);

        table.setMargin(5);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setTextAlignment(TextAlignment.CENTER);

        image_ic_republic_of_kosovo.setWidth(80);
        image_ic_republic_of_kosovo.setHeight(90);

        image_ic_kp.setWidth(100);
        image_ic_kp.setHeight(100);

        image_ic_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);

        image_ic_kp.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph republika_e_kosoves = new Paragraph(String.valueOf(this.getText(R.string.republika_e_kosoves)));
        Paragraph republika_kosovo_republic_of_kosovo = new Paragraph(String.valueOf(this.getText(R.string.republika_kosovo_republic_of_kosovo)));
        Paragraph mpb_three_language = new Paragraph(String.valueOf(this.getText(R.string.mpb_three_language)));
        Paragraph empty = new Paragraph("");
        Paragraph confidential_report = new Paragraph(String.valueOf(this.getText(R.string.confidential_report)));

        image_profile.setWidth(80);
        image_profile.setHeight(80);
        image_profile.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph p_fullName = new Paragraph(fullName);
        Paragraph info = new Paragraph(mContext.getText(R.string.birthday) + ": " + birthday + "\n")
                .add(mContext.getText(R.string.gender) + ": " + gender + "\n")
                .add(mContext.getText(R.string.age) + ": " + age + "\n")
                .add(mContext.getText(R.string.eye_color) + ": " + eyeColor + "\n")
                .add(mContext.getText(R.string.hair_color) + ": " + hairColor + "\n")
                .add(mContext.getText(R.string.height) + ": " + height + "\n")
                .add(mContext.getText(R.string.weight) + ": " + weight + "\n")
                .add(mContext.getText(R.string.phy_appearance) + ": " + phy_appearance + "\n");

        Paragraph _first_investigator = new Paragraph(this.getText(R.string.investigator) + " " + first_investigator);
        Paragraph _second_investigator = new Paragraph(this.getText(R.string.investigator) + " " + second_investigator);

        _first_investigator.setFixedPosition(60, 60, _first_investigator.getWidth());
        _second_investigator.setFixedPosition(430, 60, _second_investigator.getWidth());


        canvas.moveTo(55, pdfDocument.getDefaultPageSize().getHeight() - 800);
        canvas.lineTo(160, pdfDocument.getDefaultPageSize().getHeight() - 800);

        canvas.moveTo(425, pdfDocument.getDefaultPageSize().getHeight() - 800);
        canvas.lineTo(530, pdfDocument.getDefaultPageSize().getHeight() - 800);


        republika_e_kosoves.setMultipliedLeading(0.5f);
        republika_kosovo_republic_of_kosovo.setMultipliedLeading(0.5f);
        mpb_three_language.setMultipliedLeading(0.5f);

        republika_e_kosoves.setFontSize(10f);
        republika_kosovo_republic_of_kosovo.setFontSize(10f);
        mpb_three_language.setFontSize(10f);
        confidential_report.setFontSize(22f);
        p_fullName.setFontSize(18f);

        republika_e_kosoves.setHorizontalAlignment(HorizontalAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        mpb_three_language.setHorizontalAlignment(HorizontalAlignment.CENTER);
        confidential_report.setHorizontalAlignment(HorizontalAlignment.CENTER);
        p_fullName.setHorizontalAlignment(HorizontalAlignment.CENTER);

        republika_e_kosoves.setTextAlignment(TextAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setTextAlignment(TextAlignment.CENTER);
        mpb_three_language.setTextAlignment(TextAlignment.CENTER);
        confidential_report.setTextAlignment(TextAlignment.CENTER);
        p_fullName.setTextAlignment(TextAlignment.CENTER);
        confidential_report.setMarginTop(pdfDocument.getDefaultPageSize().getHeight() / 18);

        info.setTextAlignment(TextAlignment.CENTER);

        document.add(image_ic_republic_of_kosovo);
        document.add(republika_e_kosoves);
        document.add(republika_kosovo_republic_of_kosovo);
        document.add(mpb_three_language);
        document.add(empty);
        document.add(confidential_report);
        document.add(image_profile);
        document.add(p_fullName);
        document.add(info);
        document.add(table);
        document.add(_first_investigator);
        document.add(_second_investigator);
        document.close();

        return Uri.fromFile(file);
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
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

}
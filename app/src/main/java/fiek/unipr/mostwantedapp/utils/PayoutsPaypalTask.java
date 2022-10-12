package fiek.unipr.mostwantedapp.utils;

import static fiek.unipr.mostwantedapp.utils.BitmapHelper.getCroppedBitmap;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS;
import static fiek.unipr.mostwantedapp.utils.Constants.ASSIGNED_REPORTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.EURO;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE_PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUT_CONFIG;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL_SANDBOX_KEY_BEARER;
import static fiek.unipr.mostwantedapp.utils.Constants.PENDING;
import static fiek.unipr.mostwantedapp.utils.Constants.REFUSED;
import static fiek.unipr.mostwantedapp.utils.Constants.REPORTS_ASSIGNED;
import static fiek.unipr.mostwantedapp.utils.Constants.USD;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.activity.maps.admin.MapsActivity;
import fiek.unipr.mostwantedapp.models.InvoicesPaid;
import fiek.unipr.mostwantedapp.models.ReportAssignedUser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayoutsPaypalTask extends TimerTask {

    public static int responseCode = 0;
    private Context mContext;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String urlOfPdfUploaded;

    @Override
    public void run() {
        // Your task process
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    process(USD);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void process(String currency) throws JSONException {
        firebaseFirestore.collection(INVOICE)
                .whereEqualTo("status", PENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.size() != 0) {
                                CollectionReference collRef = firebaseFirestore.collection(PAYOUTS);
                                String sender_batch_id = collRef.document().getId();
                                int rows = queryDocumentSnapshots.size();

                                JSONObject main = new JSONObject();

                                //static side
                                JSONObject sender_batch_header = new JSONObject();
                                sender_batch_header.put("sender_batch_id", sender_batch_id);
                                sender_batch_header.put("recipient_type", "EMAIL");
                                sender_batch_header.put("email_subject", "You have money!");
                                sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

                                String[] arrayTransactionId = new String[rows];
                                String[] arrayUserId = new String[rows];
                                String[] arrayAmount = new String[rows];
                                JSONArray arrayItems = new JSONArray();

                                String[][] saveInvoices = new String[rows][12];

                                //per save invoices
                                float [] pointColumnWidths = {200F, 200F, 200F, 200F, 200F};
                                Table table = new Table(pointColumnWidths);
                                try {
                                    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                                    table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_transaction_id)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                    table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_account)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                    table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_created_date_time)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                    table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_amount)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);
                                    table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_status)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //per save invoices

                                Log.d("TAG", "onSuccess: JSON ADDED, TABLE ADDED");

                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                    String updated_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("updated_date_time");
                                    String transactionID = queryDocumentSnapshots.getDocuments().get(i).getString("transactionID");
                                    String userId = queryDocumentSnapshots.getDocuments().get(i).getString("userId");
                                    String account = queryDocumentSnapshots.getDocuments().get(i).getString("account");
                                    String status = queryDocumentSnapshots.getDocuments().get(i).getString("status");
                                    Double amount = queryDocumentSnapshots.getDocuments().get(i).getDouble("amount");
                                    String fullName = queryDocumentSnapshots.getDocuments().get(i).getString("fullName");
                                    String address = queryDocumentSnapshots.getDocuments().get(i).getString("address");
                                    String bankName = queryDocumentSnapshots.getDocuments().get(i).getString("bankName");
                                    String accountNumber = queryDocumentSnapshots.getDocuments().get(i).getString("accountNumber");
                                    String paypalEmail = queryDocumentSnapshots.getDocuments().get(i).getString("paypalEmail");

                                    if (!StringHelper.empty(created_date_time)) {
                                        Date givenDate = new SimpleDateFormat(DATE).parse(created_date_time);
                                        if (!DateHelper.checkDayBefore24(givenDate)) {
                                            JSONObject item = new JSONObject();
                                            item.put("sender_item_id", "item" + i);
                                            item.put("recipient_wallet", "PAYPAL");
                                            item.put("receiver", paypalEmail);

                                            JSONObject amountObj = new JSONObject();
                                            amountObj.put("value", amount);
                                            amountObj.put("currency", currency);
                                            item.put("amount", amountObj);
                                            arrayItems.put(item);
                                            arrayTransactionId[i] = transactionID;
                                            arrayUserId[i] = userId;
                                            arrayAmount[i] = String.valueOf(amount);

                                            //per save invoices
                                            saveInvoices[i][0] = created_date_time;
                                            saveInvoices[i][1] = updated_date_time;
                                            saveInvoices[i][2] = transactionID;
                                            saveInvoices[i][3] = userId;
                                            saveInvoices[i][4] = account;
                                            saveInvoices[i][5] = PAID;
                                            saveInvoices[i][6] = String.valueOf(amount);
                                            saveInvoices[i][7] = fullName;
                                            saveInvoices[i][8] = address;
                                            saveInvoices[i][9] = bankName;
                                            saveInvoices[i][10] = accountNumber;
                                            saveInvoices[i][11] = paypalEmail;

                                            table.addCell(new Cell().add(new Paragraph(transactionID)));
                                            table.addCell(new Cell().add(new Paragraph(paypalEmail)));
                                            table.addCell(new Cell().add(new Paragraph(created_date_time)));
                                            table.addCell(new Cell().add(new Paragraph(account)));
                                            table.addCell(new Cell().add(new Paragraph(status)));
                                            //per save invoices

                                        }
                                    }
                                }
                                main.put("sender_batch_header", sender_batch_header);
                                main.put("items", arrayItems);

                                Log.d("TAG", "onSuccess: JSON DONE, TABLE DONE");

                                //process payment when array done
                                processPayoutsWithPaypal(main, arrayTransactionId, arrayUserId, arrayAmount, saveInvoices, table);

                            }
                        } catch (ParseException | JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    private void processPayoutsWithPaypal(JSONObject main, String[] arrayTransactionId,
                                          String[] arrayUserId, String[] arrayAmount,
                                          String[][] saveInvoices, Table table) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, main.toString(4));
        Request request = new Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", PAYPAL_SANDBOX_KEY_BEARER)
                .build();
        Response response = client.newCall(request).execute();

        // Reset the response code
        responseCode = 0;

        if ((responseCode = response.code()) == 201) {
            Log.d("TAG", "onSuccess: RESPONSE 201");

            savePayoutMade(saveInvoices, table);

            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PAID, arrayTransactionId[i]);
            }

            for (int i = 0; i < arrayUserId.length; i++) {
                updateBalanceUser(Double.parseDouble(arrayAmount[i]), arrayUserId[i]);
            }

        } else if ((responseCode = response.code()) == 204) {
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(REFUSED, arrayTransactionId[i]);
            }
        } else {
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PENDING, arrayTransactionId[i]);
            }
        }

    }

    private void savePayoutMade(String[][] paidInvoices, Table table) {

        CollectionReference collRef = firebaseFirestore.collection(INVOICE_PAID);
        String id = collRef.document().getId();

//        InvoicesPaid invoicesPaid = new InvoicesPaid(id, paidInvoices);

//        firebaseFirestore.collection(PAYOUT_CONFIG)
//                .document(PAYOUT_CONFIG)
//                .set(invoicesPaid)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Uri uri = null;
//                        try {
//                            uri = generatePDF(table);
//                            uploadPDFtoFirebase(uri, id);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("BULK INVOICE FAILED", e.getMessage());
//                    }
//                });

    }

    private void uploadPDFtoFirebase(Uri pdfUri, String id) {
        //upload image to storage in firebase
        StorageReference profRef = storageReference.child(INVOICE_PAID + "/" + id + "/" + PAYOUTS_PDF);
        profRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save uri of pdf uploaded file
                        urlOfPdfUploaded = uri.toString();
                        saveReportUrlToPayoutsCollection(id, urlOfPdfUploaded);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveReportUrlToPayoutsCollection(String id, String urlOfPdfUploaded) {
        firebaseFirestore.collection(INVOICE_PAID)
                .document(id)
                .update("urlOfPdfUploaded", urlOfPdfUploaded)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Uri generatePDF(Table table) throws FileNotFoundException {
            String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            // File directory = getFilesDir();
            File file = new File(pdfPath, "Payout.pdf");
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
            Bitmap bmp_ic_republic_of_kosovo = ((BitmapDrawable) ic_republic_of_kosovo).getBitmap();
            ByteArrayOutputStream stream_ic_republic_of_kosovo = new ByteArrayOutputStream();
            bmp_ic_republic_of_kosovo.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_republic_of_kosovo);
            byte[] bitmap_data_ic_republic_of_kosovo = stream_ic_republic_of_kosovo.toByteArray();

            ImageData image_data_ic_republic_of_kosovo = ImageDataFactory.create(bitmap_data_ic_republic_of_kosovo);
            Image image_ic_republic_of_kosovo = new Image(image_data_ic_republic_of_kosovo);

            Drawable ic_kp = ContextCompat.getDrawable(mContext, R.drawable.ic_kp_10_opacity);
            Bitmap bmp_ic_kp = ((BitmapDrawable) ic_kp).getBitmap();
            ByteArrayOutputStream stream_ic_kp = new ByteArrayOutputStream();
            bmp_ic_kp.compress(Bitmap.CompressFormat.PNG, 100, stream_ic_kp);
            byte[] bitmap_data_ic_kp = stream_ic_kp.toByteArray();

            ImageData image_data_ic_kp = ImageDataFactory.create(bitmap_data_ic_kp);
            Image image_ic_kp = new Image(image_data_ic_kp);


            table.setMargin(5);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setTextAlignment(TextAlignment.CENTER);

            image_ic_republic_of_kosovo.setWidth(80);
            image_ic_republic_of_kosovo.setHeight(90);

            image_ic_kp.setWidth(100);
            image_ic_kp.setHeight(100);

            image_ic_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);

            image_ic_kp.setHorizontalAlignment(HorizontalAlignment.CENTER);

            Paragraph republika_e_kosoves = new Paragraph(String.valueOf(mContext.getText(R.string.republika_e_kosoves)));
            Paragraph republika_kosovo_republic_of_kosovo = new Paragraph(String.valueOf(mContext.getText(R.string.republika_kosovo_republic_of_kosovo)));
            Paragraph mpb_three_language = new Paragraph(String.valueOf(mContext.getText(R.string.mpb_three_language)));
            Paragraph empty = new Paragraph("");
            Paragraph confidential_report = new Paragraph(String.valueOf(mContext.getText(R.string.confidential_report)));

            republika_e_kosoves.setMultipliedLeading(0.5f);
            republika_kosovo_republic_of_kosovo.setMultipliedLeading(0.5f);
            mpb_three_language.setMultipliedLeading(0.5f);

            republika_e_kosoves.setFontSize(10f);
            republika_kosovo_republic_of_kosovo.setFontSize(10f);
            mpb_three_language.setFontSize(10f);
            confidential_report.setFontSize(22f);

            republika_e_kosoves.setHorizontalAlignment(HorizontalAlignment.CENTER);
            republika_kosovo_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            mpb_three_language.setHorizontalAlignment(HorizontalAlignment.CENTER);
            confidential_report.setHorizontalAlignment(HorizontalAlignment.CENTER);

            republika_e_kosoves.setTextAlignment(TextAlignment.CENTER);
            republika_kosovo_republic_of_kosovo.setTextAlignment(TextAlignment.CENTER);
            mpb_three_language.setTextAlignment(TextAlignment.CENTER);
            confidential_report.setTextAlignment(TextAlignment.CENTER);
            confidential_report.setMarginTop(pdfDocument.getDefaultPageSize().getHeight() / 18);

            document.add(image_ic_republic_of_kosovo);
            document.add(republika_e_kosoves);
            document.add(republika_kosovo_republic_of_kosovo);
            document.add(mpb_three_language);
            document.add(empty);
            document.add(confidential_report);
            document.close();

            Uri uri = Uri.fromFile(file);
        Log.d("TAG", "generatePDF: "+uri);
            return uri;
    }

    private void updateStatusInvoice(String status, String transactionId) {
        Log.d("TAG", "updateStatusInvoice DONE");
        firebaseFirestore.collection(INVOICE)
                .document(transactionId)
                .update("status", status).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                    }
                });
    }

    private void updateBalanceUser(Double amount, String userId) {
        Log.d("TAG", "updateBalanceUser");
        firebaseFirestore.collection(USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double totalPaid = documentSnapshot.getDouble("totalPaid");
                        updateBalanceTotalPaidUser(userId, amount, totalPaid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                    }
                });
    }

    private void updateBalanceTotalPaidUser(String userId, Double amount, Double totalPaid) {
        Log.d("TAG", "updateBalanceTotalPaidUser");
        Double totalPaidNew = totalPaid + amount;
        firebaseFirestore.collection(USERS)
                .document(userId)
                .update("balance", BALANCE_DEFAULT,
                        "totalPaid", totalPaidNew).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdatefieldError", e.getMessage());
                    }
                });
    }

}

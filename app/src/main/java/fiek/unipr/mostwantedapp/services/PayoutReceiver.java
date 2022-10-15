package fiek.unipr.mostwantedapp.services;

import static fiek.unipr.mostwantedapp.utils.Constants.BALANCE_DEFAULT;
import static fiek.unipr.mostwantedapp.utils.Constants.DATE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE;
import static fiek.unipr.mostwantedapp.utils.Constants.INVOICE_PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAID;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYOUTS_PDF;
import static fiek.unipr.mostwantedapp.utils.Constants.PAYPAL_SANDBOX_KEY_BEARER;
import static fiek.unipr.mostwantedapp.utils.Constants.PENDING;
import static fiek.unipr.mostwantedapp.utils.Constants.REFUSED;
import static fiek.unipr.mostwantedapp.utils.Constants.USD;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.models.InvoicesPaid;
import fiek.unipr.mostwantedapp.utils.DateHelper;
import fiek.unipr.mostwantedapp.utils.StringHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayoutReceiver extends BroadcastReceiver {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public static int responseCode = 0;
    private String urlOfPdfUploaded;

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable() {
            public void run()
            {
                try {
                    process(USD, context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void process(String currency, Context mContext) throws JSONException {

        String date = DateHelper.getDate();
        String start = date + " " + "00:00:00";
        String end = date + " " + "23:59:59";
        firebaseFirestore.collection(INVOICE)
                .whereGreaterThan("created_date_time", start)
                .whereLessThan("created_date_time", end)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            try {
                                //collection
                                int rows = queryDocumentSnapshots.size();
                                List<String> saveInvoices = new ArrayList<>();
                                String[] arrayTransactionId = new String[rows];
                                String[] arrayUserId = new String[rows];
                                String[] arrayAmount = new String[rows];

                                float[] pointColumnWidths = {200F, 200F, 200F, 200F, 200F};
                                Table table = new Table(pointColumnWidths);

                                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_transaction_id)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_account)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_created_date_time)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_amount)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(mContext.getText(R.string.column_name_status)))).setFont(font).setBackgroundColor(ColorConstants.BLUE).setFontColor(ColorConstants.WHITE)).setTextAlignment(TextAlignment.CENTER);

                                CollectionReference collRef = firebaseFirestore.collection(PAYOUTS);
                                String sender_batch_id = collRef.document().getId();

                                JSONObject main = new JSONObject();

                                JSONObject sender_batch_header = new JSONObject();
                                sender_batch_header.put("sender_batch_id", sender_batch_id);
                                sender_batch_header.put("recipient_type", "EMAIL");
                                sender_batch_header.put("email_subject", "You have money!");
                                sender_batch_header.put("email_message", "You received a payment. Thanks for using our service!");

                                JSONArray arrayItems = new JSONArray();


                                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                    String created_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("created_date_time");
                                    String updated_date_time = queryDocumentSnapshots.getDocuments().get(i).getString("updated_date_time");
                                    String transactionID = queryDocumentSnapshots.getDocuments().get(i).getString("transactionID");
                                    String userId = queryDocumentSnapshots.getDocuments().get(i).getString("userId");
                                    String account = queryDocumentSnapshots.getDocuments().get(i).getString("account");
                                    String new_status = PAID;
                                    Double amount = queryDocumentSnapshots.getDocuments().get(i).getDouble("amount");
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

                                            saveInvoices.add(i+transactionID+", "+paypalEmail+", "+created_date_time+", "+amount+", "+new_status);

                                            table.addCell(new Cell().add(new Paragraph(transactionID)));
                                            table.addCell(new Cell().add(new Paragraph(paypalEmail)));
                                            table.addCell(new Cell().add(new Paragraph(created_date_time)));
                                            table.addCell(new Cell().add(new Paragraph(String.valueOf(amount))));
                                            table.addCell(new Cell().add(new Paragraph(new_status)));
                                        }
                                    }
                                }

                                main.put("sender_batch_header", sender_batch_header);
                                main.put("items", arrayItems);

                                Log.d("TAG", "onSuccess: JSON DONE, TABLE DONE");

                                Log.d("JSON", main.toString(4));


                                processPayoutsWithPaypal(mContext,
                                        main,
                                        arrayTransactionId,
                                        arrayUserId,
                                        arrayAmount,
                                        saveInvoices,
                                        table);

                            } catch (ParseException | JSONException | IOException e) {
                                Log.d("TAG", e.getMessage());
                            }
                        }else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    private void processPayoutsWithPaypal(Context mContext, JSONObject main, String[] arrayTransactionId,
                                          String[] arrayUserId, String[] arrayAmount,
                                          List<String> saveInvoices, Table table) throws IOException, JSONException
    {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, main.toString(4));

        Request request = new Request.Builder()
                .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", PAYPAL_SANDBOX_KEY_BEARER)
                .build();

        Response response = client.newCall(request).execute();

        Log.d("RESPONSE", response.toString());

        // Reset the response code
        responseCode = 0;

        if ((responseCode = response.code()) == 201)
        {
            Log.d("TAG", "onSuccess: RESPONSE 201");

            for (int i = 0; i < arrayTransactionId.length; i++)
            {
                updateStatusInvoice(PAID, arrayTransactionId[i]);
            }

            for (int i = 0; i < arrayUserId.length; i++) {
                updateBalanceUser(Double.parseDouble(arrayAmount[i]), arrayUserId[i]);
            }

            savePayoutMade(mContext, saveInvoices, table);

        } else if ((responseCode = response.code()) == 204) {
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(REFUSED, arrayTransactionId[i]);
            }
        } else {
            Log.d("TAG", "onSuccess: PENDING");
            for (int i = 0; i < arrayTransactionId.length; i++) {
                updateStatusInvoice(PENDING, arrayTransactionId[i]);
            }
        }

    }

    private void savePayoutMade(Context mContext, List<String> paidInvoices, Table table) {
        CollectionReference collRef = firebaseFirestore.collection(INVOICE_PAID);
        String id = collRef.document().getId();

        InvoicesPaid invoicesPaid = new InvoicesPaid(
                id,
                paidInvoices,
                DateHelper.getDateTime());

        firebaseFirestore.collection(INVOICE_PAID)
                .document(id)
                .set(invoicesPaid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("CREATED DOCUMENT", "TRUE");
                        Uri uri = null;
                        try {
                            uri = generatePDF(mContext, table);
                        } catch (IOException e) {
                            Log.d("GENERATE_ERROR", e.getMessage());
                        }
                        uploadPDFtoFirebase(mContext, uri, id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("BULK INVOICE FAILED", e.getMessage());
                    }
                });

    }

    private void uploadPDFtoFirebase(Context mContext, Uri pdfUri, String id) {
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
                        saveReportUrlToPayoutsCollection(mContext, id, urlOfPdfUploaded);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPLOAD ERROR", e.getMessage());
                    }
                });
            }
        });
    }

    private void saveReportUrlToPayoutsCollection(Context mContext, String id, String urlOfPdfUploaded) {
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

    private Uri generatePDF(Context mContext, Table table) throws FileNotFoundException {

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

        Paragraph republika_e_kosoves = new Paragraph(String.valueOf(mContext.getText(R.string.republika_e_kosoves)));
        Paragraph republika_kosovo_republic_of_kosovo = new Paragraph(String.valueOf(mContext.getText(R.string.republika_kosovo_republic_of_kosovo)));
        Paragraph mpb_three_language = new Paragraph(String.valueOf(mContext.getText(R.string.mpb_three_language)));
        Paragraph empty = new Paragraph("");
        Paragraph payouts = new Paragraph(String.valueOf(mContext.getText(R.string.payouts)));

        republika_e_kosoves.setMultipliedLeading(0.5f);
        republika_kosovo_republic_of_kosovo.setMultipliedLeading(0.5f);
        mpb_three_language.setMultipliedLeading(0.5f);

        republika_e_kosoves.setFontSize(10f);
        republika_kosovo_republic_of_kosovo.setFontSize(10f);
        mpb_three_language.setFontSize(10f);
        payouts.setFontSize(22f);

        republika_e_kosoves.setHorizontalAlignment(HorizontalAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        mpb_three_language.setHorizontalAlignment(HorizontalAlignment.CENTER);
        payouts.setHorizontalAlignment(HorizontalAlignment.CENTER);

        republika_e_kosoves.setTextAlignment(TextAlignment.CENTER);
        republika_kosovo_republic_of_kosovo.setTextAlignment(TextAlignment.CENTER);
        mpb_three_language.setTextAlignment(TextAlignment.CENTER);
        payouts.setTextAlignment(TextAlignment.CENTER);
        payouts.setMarginTop(pdfDocument.getDefaultPageSize().getHeight() / 18);

        document.add(image_ic_republic_of_kosovo);
        document.add(republika_e_kosoves);
        document.add(republika_kosovo_republic_of_kosovo);
        document.add(mpb_three_language);
        document.add(empty);
        document.add(payouts);
        document.add(table);
        document.close();

        Uri uri = Uri.fromFile(file);
        Log.d("TAG", "generatePDF: " + uri);
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

package fiek.unipr.mostwantedapp.fragment.admin.register.user;

import static fiek.unipr.mostwantedapp.utils.Constants.PROFILE_PICTURE;
import static fiek.unipr.mostwantedapp.utils.Constants.USERS;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import fiek.unipr.mostwantedapp.LoginActivity;
import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.utils.CircleTransform;

public class SetProfileUserFragment extends Fragment {

    private View view;
    private String userID;
    private String fullName, role;

    private CircleImageView circleImageViewUser;
    private ImageView ic_add_user;
    private TextView tv_addprofile_user;
    private ProgressBar progressBarUser;
    private Button btnSkipFragSetProfileUser;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private Bundle bundle;

    public SetProfileUserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_profile_user, container, false);

        circleImageViewUser = view.findViewById(R.id.circleImageViewUser);
        ic_add_user = view.findViewById(R.id.ic_add_user);
        tv_addprofile_user = view.findViewById(R.id.tv_addprofile_user);
        progressBarUser = view.findViewById(R.id.progressBarUser);
        btnSkipFragSetProfileUser = view.findViewById(R.id.btnSkipFragSetProfileUser);

        bundle = getArguments();
        getAndSetFromBundle(bundle);

        StorageReference profileRef = storageReference.child(USERS+"/" + userID + "/"+PROFILE_PICTURE);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(circleImageViewUser);
            }
        });

        ic_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        btnSkipFragSetProfileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage(getContext().getText(R.string.success_redirect_to_login));
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                }, 4500);
            }
        });

        onBackPressed();

        return view;
    }

    private void getAndSetFromBundle(Bundle bundle) {
        if(bundle != null){
            userID = bundle.getString("userID");
            fullName = bundle.getString("fullName");
            role = bundle.getString("role");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        progressDialog.setMessage(getContext().getText(R.string.loading));
        progressDialog.show();
        StorageReference profRef = storageReference.child(USERS+"/" + userID + "/"+PROFILE_PICTURE);
        profRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.show();
                        Picasso.get().load(uri).transform(new CircleTransform()).into(circleImageViewUser);
                        DocumentReference docRef = firebaseFirestore.collection(USERS).document(userID);
                        docRef.update("urlOfProfile", uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task != null && task.isSuccessful()) {
                                    progressDialog.setMessage(getContext().getText(R.string.profile_picture_added));
                                    tv_addprofile_user.setText(R.string.profile_picture_added);
                                    progressDialog.setMessage(getContext().getText(R.string.success_redirect_to_login));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBarUser.setVisibility(View.GONE);
                                            firebaseAuth.signOut();
                                            Intent intent = new Intent(getContext(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                        }
                                    }, 3000);
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage(e.getMessage());
                                progressDialog.dismiss();
                            }
                        }, 4500);
                        progressBarUser.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    private void onBackPressed() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Toast.makeText(getContext(), getContext().getText(R.string.error_you_need_to_add_photo_for_person), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

}
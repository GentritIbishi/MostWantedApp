package fiek.unipr.mostwantedapp.helpers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fiek.unipr.mostwantedapp.dashboard.AdminDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.UserDashboardActivity;
import fiek.unipr.mostwantedapp.dashboard.AdminUserDashboardActivity;

public class SessionManagement extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SessionManagement.context = getApplicationContext();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseUser != null)
        {
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        String role = task.getResult().getString("role");
                        if(role !=null && role.matches("User"))
                        {
                            Intent user = new Intent(SessionManagement.this, AdminUserDashboardActivity.class);
                            user.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(user);
                        }
                        else if(role != null && role.matches("Admin"))
                        {
                            Intent admin = new Intent(SessionManagement.this, AdminDashboardActivity.class);
                            admin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(admin);
                        }
                    }
                }
            });
            //Nese so as user as Admin, por nese firebaseUser eshte login po ska privilegje atehere qoje te informeri nese o login
            sendInformerToDashboard();
        }

    }

    public void sendInformerToDashboard() {
        Intent informer = new Intent(SessionManagement.this, UserDashboardActivity.class);
        informer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(informer);
    }
}

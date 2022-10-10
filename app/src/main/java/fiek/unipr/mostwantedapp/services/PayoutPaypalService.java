package fiek.unipr.mostwantedapp.services;

import android.app.Service;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class PayoutPaypalService extends Worker {

    public PayoutPaypalService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }

    public static void oneOffRequest() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PayoutPaypalService.class)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .setConstraints(setCons())
                .build();
        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }

    public static Constraints setCons() {
        Constraints constraints = new Constraints.Builder().build();
        return constraints;
    }
}

package com.example.workmanager.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.workmanager.R;
import com.example.workmanager.utils.Constant;

import org.jetbrains.annotations.NotNull;

import static com.example.workmanager.utils.Constant.KEY_TASK_OUTPUT;

public class Myworker extends Worker {


//    private static final String KEY_MESSAGE= "message";

    public Myworker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data = getInputData();
        String desc = data.getString(Constant.KEY_TASK_DESC);
        displayNotification("Hey I am your work", desc);

//        Data data1 = new Data.Builder()
//        .putString(KEY_TASK_OUTPUT, "Task Finished Successfully")
//                .build();
        Log.d("log", "Worker works");

        Data outputData = new Data.Builder()
                .putString(KEY_TASK_OUTPUT, "This is output message")
                .build();

    //  setOutputData(data1);

        return Result.success(outputData);


    }
    private void displayNotification(String task, String desc) {

        NotificationManager manager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Myapp", "Myapp", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);

        manager.notify(1, builder.build());

    }


}

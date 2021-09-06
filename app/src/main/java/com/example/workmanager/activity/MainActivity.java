package com.example.workmanager.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.workmanager.alertreceiver.AlertReceiver;
import com.example.workmanager.R;
import com.example.workmanager.timepickerfragment.TimePickerFragment;
import com.example.workmanager.worker.Myworker;
import com.example.workmanager.utils.Constant;

import java.text.DateFormat;
import java.util.Calendar;

import static com.example.workmanager.utils.Constant.KEY_TASK_DESC;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    Data data = new Data.Builder()
            .putString(KEY_TASK_DESC, "Hey I am sending the work data")
            .build();

//    Constraints constraints = new Constraints.Builder()
//
//            .setRequiresCharging(true)
//            .build();

    final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Myworker.class)
            .setInputData(data)
          //  .setConstraints(constraints)
            .build();

    findViewById(R.id.btn_work).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WorkManager.getInstance().enqueue(request);
        }
    });

    final TextView textView = findViewById(R.id.textView);

        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId())
            .observe(this, new Observer<WorkInfo>() {
        @Override
        public void onChanged(@Nullable WorkInfo workInfo) {

            if (workInfo != null) {

                if (workInfo.getState().isFinished()) {

                  Data data = workInfo.getOutputData();
                    String output = data.getString(Constant.KEY_TASK_OUTPUT);

                    textView.append(output + "\n");
                }

                String status = workInfo.getState().name();
                textView.append(status + "\n");
            }
        }
    });
        mTextView = findViewById(R.id.textView);

        Button buttonTimePicker = findViewById(R.id.button_timepicker);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button buttonCancelAlarm = findViewById(R.id.button_cancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        mTextView.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        mTextView.setText("Alarm canceled");
    }
}
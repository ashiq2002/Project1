package com.setbitzero.taskmaneger.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.setbitzero.taskmaneger.adapter.StatusAdapter;
import com.setbitzero.taskmaneger.database.DatabaseHelper;
import com.setbitzero.taskmaneger.databinding.ActivityAddTaskBinding;
import com.setbitzero.taskmaneger.model.TaskModel;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {
    ActivityAddTaskBinding binding;
    DatabaseHelper databaseHelper;
    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());

        binding.taskStartTime.setOnClickListener(v->timeDialog(0, "Select Start Time"));
        binding.taskEndTime.setOnClickListener(v-> timeDialog(1, "Select End Time"));
        statusAdapter();

        insert();
    }

//    @SuppressLint("SetTextI18n")
//    private void dateDialog(){
//        final Calendar cldr = Calendar.getInstance();
//        int day = cldr.get(Calendar.DAY_OF_MONTH);
//        int month = cldr.get(Calendar.MONTH);
//        int year = cldr.get(Calendar.YEAR);
//        // date picker dialog
//        datePicker = new DatePickerDialog(AddTaskActivity.this,
//                (view, year1, month1, dayOfMonth) -> binding.taskStartTime.setText(month1 +" - "+dayOfMonth+" - "+ year1), year, month, day);
//        datePicker.setTitle("Select start Date");
//        datePicker.show();
//    }

    //show TimePickerDialog
    @SuppressLint("SetTextI18n")
    private void timeDialog(int key, String title){
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddTaskActivity.this, (timePicker, selectedHour, selectedMinute) -> {

            if(key == 0) binding.taskStartTime.setText(format12(selectedHour, selectedMinute));
            else if(key == 1) binding.taskEndTime.setText(format12(selectedHour, selectedMinute));
        }, hour, minute, false);
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }

    //convert hour 24 to 12
    @SuppressLint("DefaultLocale")
    private String format12(int selectedHour, int selectedMinute){
        String ampm = "AM";
        if (selectedHour >= 12)
            ampm = "PM";
        selectedHour = selectedHour % 12;
        if (selectedHour == 0)
            selectedHour = 12;

        return String.format("%d : %d %s", selectedHour, selectedMinute, ampm);
    }

    //set status spinner adapter
    private void statusAdapter(){
        StatusAdapter adapter = new StatusAdapter(AddTaskActivity.this, new String[]{"","Running", "Pending", "Complete"});
        binding.spinner.setAdapter(adapter);
    }

    //insert data into task database
    private void insertTask(String status){
        String title, description, sTime, eTime;
        title = binding.taskTitle.getText().toString();
        description = binding.taskDescription.getText().toString();
        sTime = binding.taskStartTime.getText().toString();
        eTime = binding.taskEndTime.getText().toString();

        binding.submitData.setOnClickListener(v->{
            databaseHelper.getTaskDao().insertTask(new TaskModel(title, description, status, sTime, eTime));
            binding.taskTitle.setText("");
            binding.taskDescription.setText("");
            binding.taskStartTime.setText("");
            binding.taskEndTime.setText("");
        });
    }

    //set status selected item
    private void insert(){
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = parent.getItemAtPosition(position).toString();
                if(!status.trim().isEmpty())
                insertTask(status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    //is edit text value null
    private boolean isNull(String[] item){
        return (item[0].trim().isEmpty()
                && item[1].trim().isEmpty()
                && item[2].trim().isEmpty()
                && item[3].trim().isEmpty());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
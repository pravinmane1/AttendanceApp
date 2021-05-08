package com.projectocean.attendanceapp.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.projectocean.attendanceapp.AttendanceDayActivity;
import com.projectocean.attendanceapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = root.findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                Date currentTime = calendar.getTime();

                String dateStr = dateFormat.format(currentTime);

                Intent i = new Intent(getContext(), AttendanceDayActivity.class);

                i.putExtra("day",dayOfWeek);
                i.putExtra("date",dateStr);

                startActivity(i);
               // Toast.makeText(getContext(), "date "+dateStr+" "+dayOfWeek, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}
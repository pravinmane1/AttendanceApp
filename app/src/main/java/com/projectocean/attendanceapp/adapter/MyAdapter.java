package com.projectocean.attendanceapp.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.projectocean.attendanceapp.dayFragments.FridayFragment;
import com.projectocean.attendanceapp.dayFragments.MondayFragment;
import com.projectocean.attendanceapp.dayFragments.SaturdayFragment;
import com.projectocean.attendanceapp.dayFragments.ThursdayFragment;
import com.projectocean.attendanceapp.dayFragments.TuesdayFragment;
import com.projectocean.attendanceapp.dayFragments.WednesdayFragment;


public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MondayFragment mondayFragment = new MondayFragment();
                return mondayFragment;
            case 1:
                TuesdayFragment tuesdayFragment = new TuesdayFragment();
                return tuesdayFragment;
            case 2:
                WednesdayFragment wednesdayFragment = new WednesdayFragment();
                return wednesdayFragment;
            case 3:
                ThursdayFragment thursdayFragment = new ThursdayFragment();
                return thursdayFragment;
            case 4:
                FridayFragment FridayFragment = new FridayFragment();
                return FridayFragment;
            case 5:
                SaturdayFragment SaturdayFragment = new SaturdayFragment();
                return SaturdayFragment;
            default:
                SaturdayFragment SaturdayFragment1 = new SaturdayFragment();
                return SaturdayFragment1;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
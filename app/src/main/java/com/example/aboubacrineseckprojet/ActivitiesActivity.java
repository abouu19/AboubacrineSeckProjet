package com.example.aboubacrineseckprojet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {
    private class ListAdapter extends BaseAdapter{
        List<ActivityDBHelper.Activity> activities;
        LayoutInflater vi;
        Context context;
        ListAdapter(Context context,List<ActivityDBHelper.Activity> activities){

            this.context = context;
            this.activities = activities;
            this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return this.activities.size();
        }

        @Override
        public Object getItem(int i) {
            return this.activities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view ==null){
                view = vi.inflate(R.layout.list_item,null);
                //view = getLayoutInflater().inflate(R.layout.list_item,viewGroup,false);
            }
            ActivityDBHelper.Activity activity = this.activities.get(i);
            try {
                ((ImageView)view.findViewById(R.id.icon_list_item)).setImageResource(activity.icon);
            }catch (Exception e){
                System.out.println("***************");
                System.out.println("found: "+activity.label+","+activity.icon);
                ((ImageView)view.findViewById(R.id.icon_list_item)).setImageResource(R.drawable.ic_still);
            }
            ((TextView)view.findViewById(R.id.label_list_item)).setText(activity.label);
            ((TextView)view.findViewById(R.id.date_list_item)).setText(activity.date.toString());
            return view;
        }
    }
    ActivityDBHelper activityDBHelper;
    Profile profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        if((profile = Utils.getSharedProfileOrElseLogin(this)) == null){
            return;
        }
        activityDBHelper = new ActivityDBHelper(this);
        List<ActivityDBHelper.Activity> activities = activityDBHelper.getProfileActivities(profile.id);
        ListView listView = findViewById(R.id.list_activities);

        listView.setAdapter(new ListAdapter(this,activities));
        ((Button)findViewById(R.id.clear)).setOnClickListener((v)->{
            activityDBHelper.clearProfileActivities(profile.id);
        });
    }
}
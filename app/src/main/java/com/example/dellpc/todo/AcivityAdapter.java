package com.example.dellpc.todo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dell pc on 13-Jan-17.
 */

public class AcivityAdapter  extends ArrayAdapter<Activities>{
    public AcivityAdapter(Context context, int resource, List<Activities> textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }
        TextView activity = (TextView) convertView.findViewById(R.id.activity_name);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView date = (TextView) convertView.findViewById(R.id.date_time);
        Activities activities = getItem(position);

        activity.setText(activities.getActivityName());
        category.setText(activities.getCategName());
        date.setText(activities.getDataTime());


        return convertView;
    }
}

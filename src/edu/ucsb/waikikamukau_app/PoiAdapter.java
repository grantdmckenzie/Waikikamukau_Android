package edu.ucsb.waikikamukau_app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PoiAdapter extends ArrayAdapter<Poi> {
	
	private static class ViewHolder {
        TextView name;
        TextView distance;
    }
    public PoiAdapter(Context context, ArrayList<Poi> pois) {
        super(context, R.layout.item_poi, pois);
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Poi poi = getItem(position); 
        
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
           viewHolder = new ViewHolder();
           LayoutInflater inflater = LayoutInflater.from(getContext());
           convertView = inflater.inflate(R.layout.item_poi, parent, false);
           viewHolder.name = (TextView) convertView.findViewById(R.id.poiName);
           viewHolder.distance = (TextView) convertView.findViewById(R.id.poiDistance);
           convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(poi.name);
        viewHolder.distance.setText(poi.distance_emp + " / " + poi.distance_metric);
        // Return the completed view to render on screen
        return convertView;
    }
 }
package edu.ucsb.waikikamukau_app;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PoiAdapter extends ArrayAdapter<Poi> {
	public Typeface coolvetica;
	private Context mContext;
	private static class ViewHolder {
        TextView name;
        TextView distance;
        TextView latitude;
        TextView longitude;
        TextView id;
        ImageView cat;
    }
	
    public PoiAdapter(Context context, ArrayList<Poi> pois) {
        super(context, R.layout.item_poi, pois);
        coolvetica =Typeface.createFromAsset(context.getAssets(),"fonts/Gotham-Book.ttf");
        mContext = context;
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
           viewHolder.latitude = (TextView) convertView.findViewById(R.id.poiLatitude);
           viewHolder.longitude = (TextView) convertView.findViewById(R.id.poiLongitude);
           viewHolder.cat = (ImageView) convertView.findViewById(R.id.poiCat);
           viewHolder.id = (TextView) convertView.findViewById(R.id.poiId);
           convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(poi.name);
        viewHolder.distance.setText(poi.distance_emp + " (" + poi.distance_metric+")");
        viewHolder.latitude.setText(poi.latitude+"");
        viewHolder.longitude.setText(poi.longitude+"");
        viewHolder.id.setText(poi.id+"");
        String packageName = mContext.getPackageName();
        int resId = mContext.getResources().getIdentifier("cat" + String.valueOf(poi.cat-1), "drawable", packageName);
        viewHolder.cat.setImageDrawable(mContext.getResources().getDrawable(resId));
       
        viewHolder.name.setTypeface(coolvetica);
        viewHolder.distance.setTypeface(coolvetica);
        // Return the completed view to render on screen
        return convertView;
    }
	public OnItemClickListener onItemClickListener() {
		// TODO Auto-generated method stub
		return null;
	}
 }
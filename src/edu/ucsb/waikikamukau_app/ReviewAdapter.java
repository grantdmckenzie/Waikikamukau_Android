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

public class ReviewAdapter extends ArrayAdapter<Review> {
	public Typeface coolvetica;
	private Context mContext;
	private static class ViewHolder {
        TextView author;
        TextView text;
        TextView date_hr;
    }
	
    public ReviewAdapter(Context context, ArrayList<Review> Reviews) {
        super(context, R.layout.item_review, Reviews);
        coolvetica =Typeface.createFromAsset(context.getAssets(),"fonts/Gotham-Book.ttf");
        mContext = context;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Review Review = getItem(position); 
        
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
           viewHolder = new ViewHolder();
           LayoutInflater inflater = LayoutInflater.from(getContext());
           convertView = inflater.inflate(R.layout.item_review, parent, false);
           viewHolder.author = (TextView) convertView.findViewById(R.id.reviewAuthor);
           viewHolder.text = (TextView) convertView.findViewById(R.id.reviewText);
           viewHolder.date_hr = (TextView) convertView.findViewById(R.id.reviewDate);
           convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.author.setText(Review.author);
        viewHolder.text.setText(Review.text);
        viewHolder.date_hr.setText(Review.date_hr);
       
        viewHolder.date_hr.setTypeface(coolvetica);
        viewHolder.author.setTypeface(coolvetica);
        viewHolder.text.setTypeface(coolvetica);
        // Return the completed view to render on screen
        return convertView;
    }
	public OnItemClickListener onItemClickListener() {
		// TODO Auto-generated method stub
		return null;
	}
 }
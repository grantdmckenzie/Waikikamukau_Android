package edu.ucsb.waikikamukau_app;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Review {
	public String author;
	public String text;
	public int date;
	public String date_hr;
	
	@SuppressLint("SimpleDateFormat")
	public Review(JSONObject object){
        try {
            this.author = object.getString("author");
            this.text = object.getString("d");
            this.date = object.getInt("ts");
            this.date_hr = new SimpleDateFormat("dd MMM yyy").format(new Date(this.date * 1000L));
       } catch (JSONException e) {
            e.printStackTrace();
       }
    }

    public static ArrayList<Review> fromJson(JSONArray jsonObjects) {
       ArrayList<Review> review = new ArrayList<Review>();
       for (int i = 0; i < jsonObjects.length(); i++) {
           try {
        	  Review singlereview = new Review(jsonObjects.getJSONObject(i));
        	  review.add(singlereview);
           } catch (JSONException e) {
              e.printStackTrace();
           }
      }
      return review;
    }
}

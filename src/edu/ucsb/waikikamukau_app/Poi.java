package edu.ucsb.waikikamukau_app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Poi {
	public String name;
	public double distance;
	public double latitude;
	public double longitude;
	public String distance_emp;
	public String distance_metric;
	
	public Poi(String name, double distance, double latitude, double longitude) {
		this.name = name;
		this.distance = distance;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Poi(JSONObject object){
        try {
            this.name = object.getString("name");
            this.distance = object.getDouble("distance");
            if (object.getDouble("distance") <= 304.8) {
    			this.distance_emp = (int) Math.round(object.getDouble("distance") * 3.28084) + "ft";
    		} else {
    			this.distance_emp = (double) Math.round(object.getDouble("distance") * 3.28084 / 5280*100)/100 + " mi";
   
    		}
            
            if (object.getDouble("distance") <= 500) {
    			this.distance_metric = (int) Math.round(object.getDouble("distance")) + "m";
    		} else {
    			this.distance_metric = (double) Math.round(object.getDouble("distance") / 1000 *100)/100 + " km";
  
    		}
       } catch (JSONException e) {
            e.printStackTrace();
       }
    }

    public static ArrayList<Poi> fromJson(JSONArray jsonObjects) {
       ArrayList<Poi> poi = new ArrayList<Poi>();
       for (int i = 0; i < jsonObjects.length(); i++) {
           try {
        	  Poi singlepoi = new Poi(jsonObjects.getJSONObject(i));
              poi.add(singlepoi);
           } catch (JSONException e) {
              e.printStackTrace();
           }
      }
      return poi;
    }
}
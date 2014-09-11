package edu.ucsb.waikikamukau_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Poi {
	public String name;
	public double distance;
	public double latitude;
	public double longitude;
	public String distance_emp;
	public String distance_metric;
	public int cat;
	public String id;
	
	public Poi(String name) {
		this.name = name;
		this.distance = 1;
		this.distance_emp = "";
		this.distance_metric = "";
		this.latitude = 1;
		this.longitude = 1;
		this.cat = 182;
		this.id = "new";
	}
	
	public Poi(JSONObject object){
        try {
            this.name = object.getString("name");
            this.distance = object.getDouble("dist");
            this.latitude = object.getDouble("lat");
            this.longitude = object.getDouble("lng");
            this.id = object.getString("w_id");
            this.id = object.getString("uri");
            this.cat = object.getInt("cat");
            if (this.distance <= 304.8) {
    			this.distance_emp = (int) Math.round(this.distance * 3.28084) + "ft";
    		} else {
    			this.distance_emp = (double) Math.round(this.distance * 3.28084 / 5280*100)/100 + " mi";
   
    		}		
            
            if (object.getDouble("dist") <= 500) {
    			this.distance_metric = (int) Math.round(this.distance) + "m";
    		} else {
    			this.distance_metric = (double) Math.round(this.distance / 1000 *100)/100 + " km";
  
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
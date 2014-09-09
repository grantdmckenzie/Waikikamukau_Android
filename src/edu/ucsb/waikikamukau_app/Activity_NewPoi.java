package edu.ucsb.waikikamukau_app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity_NewPoi extends Activity {
	private GoogleMap map;
    private Marker newpoiloc;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.layout_newpoi);
	     
	     
	     Bundle extras = getIntent().getExtras();
	     if (extras != null) {
             double poilatitude = extras.getDouble("poilat");
             double poilongitude = extras.getDouble("poilng");
             map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
             if (map!=null){
                 LatLng p = new LatLng(poilatitude, poilongitude);
                 //Marker marker = map.addMarker(new MarkerOptions().position(p));
                 map.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
                 map.setMapType(GoogleMap.MAP_TYPE_NONE);
                 // map.setOnCameraChangeListener(getCameraChangeListener());
                 map.getUiSettings().setZoomControlsEnabled(false);
                 TileOverlayOptions opts = new TileOverlayOptions();
                 opts.tileProvider(new MapBoxOnlineTileProvider("grantdmckenzie.je0ai8ba"));
                 opts.zIndex(1);
                 TileOverlay overlay = map.addTileOverlay(opts);

                 newpoiloc = map.addMarker(new MarkerOptions()
                         .position(p)
                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker01_30))
                         .draggable(true));

             }
	     }
	     
	     final Spinner spin = (Spinner)findViewById(R.id.spinner1);
	     final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
	     try {
	    	String d = loadJSONFromAsset();
			JSONObject obj = new JSONObject(d);
			
			JSONArray data = obj.getJSONArray("types");
			HashMap<String, String> map;
			Log.v("Wai", data.length()+"");
			 for(int i = 0; i < data.length(); i++){
				
				 JSONObject c = data.getJSONObject(i);
				 map = new HashMap<String, String>();
				 map.put("id", c.getString("id"));
				 map.put("parent", c.getString("parent"));
				 map.put("cat", c.getString("cat"));
				 MyArrList.add(map);
			 
			 }
             SimpleAdapter sAdap;
             sAdap = new SimpleAdapter(Activity_NewPoi.this, MyArrList, R.layout.item_addpoi,
                     new String[] {"id", "parent", "cat"}, new int[] {R.id.id, R.id.parent, R.id.cat});
             spin.setAdapter(sAdap);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         Button moo = (Button) findViewById(R.id.andybutton);
         moo.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                 EditText nameET = (EditText) findViewById(R.id.name);
                 Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                 String name = nameET.getText().toString();
                 try {
                    JSONObject catobj = new JSONObject(spinner.getSelectedItem().toString());
                    String cat = catobj.get("id").toString();

                     Intent poiSend = new Intent(Activity_NewPoi.this, Activity_NewPoiSend.class);
                     poiSend.putExtra("lat", newpoiloc.getPosition().latitude);
                     poiSend.putExtra("lng", newpoiloc.getPosition().longitude);
                     poiSend.putExtra("name", name);
                     poiSend.putExtra("cat", cat);

                     startActivity(poiSend);

                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });

	 }

	public String loadJSONFromAsset() {
	    String json = null;
	    try {
	
	        InputStream is = getAssets().open("poitypes.json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        json = new String(buffer, "UTF-8");
	
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    return json;
	}

}
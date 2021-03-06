package edu.ucsb.waikikamukau_app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Main extends Activity implements LocationListener {
 private MapView myOpenMapView;
 private MapController myMapController;
 private LocationManager locationManager;
 private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
 private ResourceProxy mResourceProxy;
 private ArrayList<OverlayItem> items;
 private Drawable mainMarker;
 private ArrayList<Poi> poiList;
 private PoiAdapter dataAdapter = null;
 double mLongitude = 31987968;
 double mLatitude = 34783155;
 
    /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     
     setContentView(R.layout.layout_main);
     setUpBottomTabs();
     
     mLongitude = 34.43;
     mLatitude = -119.92;

     
     locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);

     
     Typeface ralewaybold =Typeface.createFromAsset(getAssets(),"fonts/Gotham-Bold.ttf");
     TextView v = (TextView) findViewById(R.id.title);
     v.setTypeface(ralewaybold);


    
     
     
 }
 private void setUpBottomTabs() {
	 
	 ImageView map = (ImageView) findViewById(R.id.map_icon);
	 ImageView list = (ImageView) findViewById(R.id.list_icon);
	 map.setImageResource(R.drawable.map);
	 list.setImageResource(R.drawable.list_a);
     map.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
  	         Intent mapView = new Intent(Activity_Main.this, Activity_Map.class);
  	         mapView.putExtra("lat", mLatitude);
  	         mapView.putExtra("lng", mLongitude);
  	         startActivity(mapView);
         }
     });
     
     ImageView searchbutton = (ImageView) findViewById(R.id.searchbutton);
     searchbutton.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
  	         TextView tv = (TextView) findViewById(R.id.searchtext);
  	         String val = tv.getText().toString();
  	         String[] params = new String[4];
  	         params[0] = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
  	         params[1] = mLatitude+"";
  	         params[2] = mLongitude+"";
  	         params[3] = val;
  	         new GetNearby().execute(params);
  	         InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
  	    	 imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
         }
     });
 }
	 
 
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, this);
	}
	 @Override
	 protected void onPause() {
		  // TODO Auto-generated method stub
		  super.onPause();
		  locationManager.removeUpdates(this);
	 }


	    
	    
    private class GetNearby extends AsyncTask<String, Void, String> {
  	  private static final int REGISTRATION_TIMEOUT = 3 * 1000;
  	  private static final int WAIT_TIMEOUT = 30 * 1000;
  	  private final HttpClient httpclient = new DefaultHttpClient();
  	  final HttpParams params = httpclient.getParams();
  	  HttpResponse response;
  	  private String content =  null;
  	  private boolean error = false;
  	  private ProgressDialog dialog = new ProgressDialog(Activity_Main.this);

  	  protected void onPreExecute() {
  	   dialog.setMessage("Let's see what is nearby...");
  	   dialog.show();
  	  }

  	  protected String doInBackground(String... urls) {

  	   String URL = null;
  	   String queryParam = null;
  	   String lat = null;
  	   String lng = null;
  	   
  	   try {

  	   URL = urls[0];
  	   lat = urls[1];
  	   lng = urls[2];
  	   queryParam = urls[3];
  	   
  	   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
  	   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
  	   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

  	   HttpPost httpPost = new HttpPost(URL);
  	   //Log.v("Wai","URL: " +URL);
  	   //add name value pair for the country code
  	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
  	   nameValuePairs.add(new BasicNameValuePair("lat",String.valueOf(lat)));
  	   nameValuePairs.add(new BasicNameValuePair("lng",String.valueOf(lng)));
  	   nameValuePairs.add(new BasicNameValuePair("q",String.valueOf(queryParam)));
  	   httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
  	   response = httpclient.execute(httpPost);
  	   	
  	    StatusLine statusLine = response.getStatusLine();
  	    //Log.v("Wai","Status: " +response.getAllHeaders()[1]);
  	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
  	     ByteArrayOutputStream out = new ByteArrayOutputStream();
  	     response.getEntity().writeTo(out);
  	     out.close();
  	     content = out.toString();
  	    } else{
  	     //Closes the connection.
  	     Log.w("HTTP1:",statusLine.getReasonPhrase());
  	     response.getEntity().getContent().close();
  	     throw new IOException(statusLine.getReasonPhrase());
  	    }
  	   } catch (ClientProtocolException e) {
  	    Log.w("HTTP2:",e );
  	    content = e.getMessage();
  	    error = true;
  	    cancel(true);
  	   } catch (IOException e) {
  	    Log.w("HTTP3:",e );
  	    content = e.getMessage();
  	    error = true;
  	    cancel(true);
  	   }catch (Exception e) {
  	    Log.w("HTTP4:",e );
  	    content = e.getMessage();
  	    error = true;
  	    cancel(true);
  	   }

  	   return content;
  	  }

  	  protected void onCancelled() {
  	   dialog.dismiss();
  	   Toast toast = Toast.makeText(Activity_Main.this, "Error connecting to Server", Toast.LENGTH_LONG);
  	   toast.setGravity(Gravity.TOP, 25, 400);
  	   toast.show();

  	  }

  	  protected void onPostExecute(String content) {
  	   dialog.dismiss();
  	   Toast toast;
  	   if (error) {
  	    toast = Toast.makeText(Activity_Main.this, content, Toast.LENGTH_LONG);
  	    toast.setGravity(Gravity.TOP, 25, 400);
  	    toast.show();
  	   } else {
  	    displayPoiList(content);
  	   }
  	  }
  	  private void displayPoiList(String response) {
  		JSONObject responseObj = null; 

  	  try {

  	   responseObj = new JSONObject(response); 
  	   JSONArray poiListObj = responseObj.getJSONArray("response");

  	   poiList = new ArrayList<Poi>();
  	   for (int i=0; i<poiListObj.length(); i++){
  	    Poi poi = new Poi(poiListObj.getJSONObject(i));
  	    poiList.add(poi);
  	   }
  	   Poi poi = new Poi("Add New Point of Interest");
  	   poiList.add(poi);
  	   
  	   //create an ArrayAdaptar from the String Array
  	   dataAdapter = new PoiAdapter(getApplicationContext(), poiList);
  	   ListView listView = (ListView) findViewById(R.id.list);
  	   
  	   
  	   // Assign adapter to ListView
  	   listView.setAdapter(dataAdapter);
  	 
  	   //enables filtering for the contents of the given ListView
  	   //listView.setTextFilterEnabled(true);
  	 
	  	 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	  		 
	  	     public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
	  	    	 
	  	    	String poiId = ((TextView) view.findViewById(R.id.poiId)).getText().toString();
	  	    	if (!poiId.equals("new")) {
		  	         Intent poiDetails = new Intent(Activity_Main.this, Activity_Details.class);
		  	         String poiName = ((TextView) view.findViewById(R.id.poiName)).getText().toString();
		  	         String poiDist = ((TextView) view.findViewById(R.id.poiDistance)).getText().toString();
		  	         String poiLat = ((TextView) view.findViewById(R.id.poiLatitude)).getText().toString();
		  	         String poiLng = ((TextView) view.findViewById(R.id.poiLongitude)).getText().toString();
		  	         poiDetails.putExtra("poiname", poiName);
		  	         poiDetails.putExtra("poidistance", poiDist);
		  	         poiDetails.putExtra("poilat", poiLat);
		  	         poiDetails.putExtra("poilng", poiLng);
		  	         poiDetails.putExtra("poiid", poiId);
		  	         startActivity(poiDetails);
	  	    	} else {
	  	    		 Intent newPoi = new Intent(Activity_Main.this, Activity_NewPoi.class);
		  	         newPoi.putExtra("poilat", mLatitude);
		  	         newPoi.putExtra("poilng", mLongitude);
		  	         startActivity(newPoi);
	  	    	}
	  	     }
	  	});


  	  } catch (JSONException e) {
  	   e.printStackTrace();
  	  }

  	 }

  	 }

	@Override
	public void onLocationChanged(Location location) {
		 mLatitude = location.getLatitude();
         mLongitude = location.getLongitude();
	     Log.v("Wai", "Location Changed: " + mLatitude + ", " + mLongitude);

        String[] params = new String[4];
        params[0] = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
        params[1] = mLatitude+"";
        params[2] = mLongitude+"";
        params[3] = "";
        new GetNearby().execute(params);
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
    
}
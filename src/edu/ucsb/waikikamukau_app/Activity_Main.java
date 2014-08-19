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
     
     //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

     //Remove notification bar
     //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

     mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
     
     // String url = "http://api.geonames.org/postalCodeSearchJSON?postalcode=9011&maxRows=10&username=demo";
     //String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
     //new GetNearby().execute(url);
     
     GeoPoint startPoint = new GeoPoint(34.415343, -119.845135);
     myOpenMapView = (MapView)findViewById(R.id.openmapview);
     myOpenMapView.setMultiTouchControls(true);
     myMapController = (MapController) myOpenMapView.getController();
     myMapController.setZoom(16);
     myMapController.setCenter(startPoint);
     
     locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
     
     mainMarker = this.getResources().getDrawable(R.drawable.marker01_30);
     mainMarker.setBounds(0 - mainMarker.getIntrinsicWidth() / 2, 0 - mainMarker.getIntrinsicHeight(),mainMarker.getIntrinsicWidth() / 2, 0);
     
     items = new ArrayList<OverlayItem>();
     OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", startPoint);
     overlayItem.setMarker(mainMarker);
     items.add(overlayItem);
     
     Typeface ralewaybold =Typeface.createFromAsset(getAssets(),"fonts/Raleway-Light.ttf");
     TextView v = (TextView) findViewById(R.id.title);
     v.setTypeface(ralewaybold);

     
     /* DefaultResourceProxyImpl defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
     MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(items, null, defaultResourceProxyImpl);
     myOpenMapView.getOverlays().add(myItemizedIconOverlay); */
     
     this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new Glistener() , mResourceProxy);
     this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
     myOpenMapView.invalidate();
    
     
     
 }
 private void setUpBottomTabs() {
	 ImageView map = (ImageView) findViewById(R.id.map_icon);
     map.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
  	         Intent mapView = new Intent(Activity_Main.this, Activity_Map.class);
  	         mapView.putExtra("lat", mLatitude);
  	         mapView.putExtra("lng", mLongitude);
  	         startActivity(mapView);
         }
     });
 }
	 
 
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, this);
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
  	   
  	   try {

  	   URL = urls[0];
  	   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
  	   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
  	   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

  	   HttpPost httpPost = new HttpPost(URL);
  	   //Log.v("Wai","URL: " +URL);
  	   //add name value pair for the country code
  	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
  	   nameValuePairs.add(new BasicNameValuePair("lat",String.valueOf(mLatitude)));
  	   nameValuePairs.add(new BasicNameValuePair("lng",String.valueOf(mLongitude)));
  	   httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
  	   response = httpclient.execute(httpPost);
  	   	
  	    StatusLine statusLine = response.getStatusLine();
  	    // Log.v("Wai","Status: " +response.getAllHeaders()[1]);
  	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
  	     ByteArrayOutputStream out = new ByteArrayOutputStream();
  	     response.getEntity().writeTo(out);
  	     out.close();
  	     content = out.toString();
  	     // Log.v("Wai","Content: " +content);
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
  	   //create an ArrayAdaptar from the String Array
  	   dataAdapter = new PoiAdapter(getApplicationContext(), poiList);
  	   ListView listView = (ListView) findViewById(R.id.list);
  	   
  	   
  	   // Assign adapter to ListView
  	   listView.setAdapter(dataAdapter);

  	   //enables filtering for the contents of the given ListView
  	   listView.setTextFilterEnabled(true);
  	   
	  	 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	  		 
	  	     public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
	  	         // Toast.makeText(MainActivity.this, "Item with id ["+id+"] - Position ["+position+"]", Toast.LENGTH_SHORT).show();
	  	         Intent poiDetails = new Intent(Activity_Main.this, Activity_Details.class);
	  	         String poiName = ((TextView) view.findViewById(R.id.poiName)).getText().toString();
	  	         String poiDist = ((TextView) view.findViewById(R.id.poiDistance)).getText().toString();
	  	         String poiLat = ((TextView) view.findViewById(R.id.poiLatitude)).getText().toString();
	  	         String poiLng = ((TextView) view.findViewById(R.id.poiLongitude)).getText().toString();
	  	         String poiId = ((TextView) view.findViewById(R.id.poiId)).getText().toString();
	  	         poiDetails.putExtra("poiname", poiName);
	  	         poiDetails.putExtra("poidistance", poiDist);
	  	         poiDetails.putExtra("poilat", poiLat);
	  	         poiDetails.putExtra("poilng", poiLng);
	  	         poiDetails.putExtra("poiid", poiId);
	  	         startActivity(poiDetails);
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
        GeoPoint gpt = new GeoPoint(mLatitude, mLongitude);
        myMapController.setCenter(gpt);
        items.clear(); // COMMENT OUT THIS LINE IF YOU WANT A NEW ICON FOR EACH CHANGE OF POSITION
        OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", gpt);
        overlayItem.setMarker(mainMarker);
        items.add(overlayItem);
        this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,new Glistener() , mResourceProxy);
        this.myOpenMapView.getOverlays().clear();
        this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
        myOpenMapView.invalidate();
        String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
        new GetNearby().execute(url);
		
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
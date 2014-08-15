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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
 private MapView myOpenMapView;
 private MapController myMapController;
 private LocationManager locationManager;
 private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
 private ResourceProxy mResourceProxy;
 private ArrayList<OverlayItem> items;
 private Drawable mainMarker;
 private double lat;
 private double lng;
 private ArrayList<Poi> poiList;
 private PoiAdapter dataAdapter = null;
 double mLongtitude = 31987968;
 double mLatitude = 34783155;
 
    /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     
     lat = 34.43;
     lng = -119.92;
     
     mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
     setContentView(R.layout.main);
     // String url = "http://api.geonames.org/postalCodeSearchJSON?postalcode=9011&maxRows=10&username=demo";
     String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
     new GetNearby().execute(url);
     
     GeoPoint startPoint = new GeoPoint(34.415343, -119.845135);
     myOpenMapView = (MapView)findViewById(R.id.openmapview);
     myOpenMapView.setMultiTouchControls(true);
     myMapController = (MapController) myOpenMapView.getController();
     myMapController.setZoom(15);
     myMapController.setCenter(startPoint);
     
     locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
     
     mainMarker = this.getResources().getDrawable(R.drawable.marker01_30);
     mainMarker.setBounds(0 - mainMarker.getIntrinsicWidth() / 2, 0 - mainMarker.getIntrinsicHeight(),mainMarker.getIntrinsicWidth() / 2, 0);
     
     items = new ArrayList<OverlayItem>();
     OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", startPoint);
     overlayItem.setMarker(mainMarker);
     items.add(overlayItem);
     
     
     /* DefaultResourceProxyImpl defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
     MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(items, null, defaultResourceProxyImpl);
     myOpenMapView.getOverlays().add(myItemizedIconOverlay); */
     
     this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new Glistener() , mResourceProxy);
     this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
     myOpenMapView.invalidate();
    
     
 }
 class Glistener implements OnItemGestureListener<OverlayItem> {
     @Override
     public boolean onItemLongPress(int index, OverlayItem item) {
         //Toast.makeText(SampleWithMinimapItemizedoverlay.this, "Item " + item.mTitle,
         //        Toast.LENGTH_LONG).show();

         return false;
     }

     @Override
     public boolean onItemSingleTapUp(int index, OverlayItem item) {
        //Toast.makeText(SampleWithMinimapItemizedoverlay.this, "Item " + item.mTitle,
          //       Toast.LENGTH_LONG).show();
         return true; // We 'handled' this event.

     }

 }
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
  	  private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

  	  protected void onPreExecute() {
  	   dialog.setMessage("Getting your data... Please wait...");
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
  	   nameValuePairs.add(new BasicNameValuePair("lat",String.valueOf(lat)));
  	   nameValuePairs.add(new BasicNameValuePair("lng",String.valueOf(lng)));
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
  	   Toast toast = Toast.makeText(MainActivity.this, "Error connecting to Server", Toast.LENGTH_LONG);
  	   toast.setGravity(Gravity.TOP, 25, 400);
  	   toast.show();

  	  }

  	  protected void onPostExecute(String content) {
  	   dialog.dismiss();
  	   Toast toast;
  	   if (error) {
  	    toast = Toast.makeText(MainActivity.this, content, Toast.LENGTH_LONG);
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
  	   
  	  /* listView.setOnItemClickListener(new OnItemClickListener() {
  	    public void onItemClick(AdapterView<?> parent, View view,
  	      int position, long id) {
  	    // When clicked, show a toast with the TextView text
  	    Country country = (Country) parent.getItemAtPosition(position);
  	    Toast.makeText(getApplicationContext(),
  	      country.getCode(), Toast.LENGTH_SHORT).show();
  	    }
  	   }); */


  	  } catch (JSONException e) {
  	   e.printStackTrace();
  	  }

  	 }

  	 }

	@Override
	public void onLocationChanged(Location location) {
		 mLatitude = location.getLatitude();
         mLongtitude = location.getLongitude();
	     Log.v("Wai", "Location Changed: " + mLatitude + ", " + mLongtitude);
        GeoPoint gpt = new GeoPoint(mLatitude, mLongtitude);
        myMapController.setCenter(gpt);
        items.clear(); // COMMENT OUT THIS LINE IF YOU WANT A NEW ICON FOR EACH CHANGE OF POSITION
        OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", gpt);
        overlayItem.setMarker(mainMarker);
        items.add(overlayItem);
        this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,new Glistener() , mResourceProxy);
        this.myOpenMapView.getOverlays().clear();
        this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
        myOpenMapView.invalidate();
		
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
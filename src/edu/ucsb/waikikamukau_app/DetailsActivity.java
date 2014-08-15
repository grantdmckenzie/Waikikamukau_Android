package edu.ucsb.waikikamukau_app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	private MapView myOpenMapView;
	private MapController myMapController;
	private String poiid;
	private EditText andyvalue;
	private Button andybutton;
	private TextView poiDetails;
	private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
	private ResourceProxy mResourceProxy;
	private double poilatitude;
	private double poilongitude;
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String poiname = extras.getString("poiname");
            String poidist = extras.getString("poidistance");
            poilatitude = Double.parseDouble(extras.getString("poilat"));
            poilongitude = Double.parseDouble(extras.getString("poilng"));
            
            poiid = extras.getString("poiid");
            
            GeoPoint startPoint = new GeoPoint(poilatitude, poilongitude);
            myOpenMapView = (MapView)findViewById(R.id.openmapview);
            myOpenMapView.setMultiTouchControls(true);
            myMapController = (MapController) myOpenMapView.getController();
            
            Drawable mainMarker = this.getResources().getDrawable(R.drawable.marker01_30);
            mainMarker.setBounds(0 - mainMarker.getIntrinsicWidth() / 2, 0 - mainMarker.getIntrinsicHeight(),mainMarker.getIntrinsicWidth() / 2, 0);
            
            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", startPoint);
            overlayItem.setMarker(mainMarker);
            items.add(overlayItem);
            mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
            this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new Glistener() , mResourceProxy);
            this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
            myOpenMapView.invalidate();
            
            myMapController.setCenter(startPoint);
            myMapController.setZoom(15);
            Log.v("Wai", poilatitude + ", " + poilongitude);
            Log.v("Wai", startPoint.getLatitude() + ", " + startPoint.getLongitude());
            
            TextView tv = (TextView) findViewById(R.id.poiname);
            TextView dist = (TextView) findViewById(R.id.poidistance);
            poiDetails = (TextView) findViewById(R.id.poiDetails);
            andyvalue=(EditText)findViewById(R.id.andyedit);
    		andybutton=(Button)findViewById(R.id.andybutton);
    		andybutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                	setUp2send(andyvalue.getText().toString(), poiid);
                }
            });
            
            tv.setText(poiname);
            dist.setText("You are about "+poidist+" from the POI.");
            
            
            Typeface ralewaybold =Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
            Typeface ralewaythin =Typeface.createFromAsset(getAssets(),"fonts/Raleway-Light.ttf");
            tv.setTypeface(ralewaybold);
            dist.setTypeface(ralewaythin);
            poiDetails.setTypeface(ralewaythin);
            
            String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/GetAttributes"; 
            String[] params = {url,poiid};
            new GetDetails().execute(params);
        }
    }
	 private void setUp2send(String value, String id) {
		 
		 String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/UpdateAttributes"; 
		 String[] parts = value.split(":");
         String[] params = {url,id, parts[0].trim().toLowerCase(),parts[1].trim().toLowerCase()};
         andyvalue.setText("");
         new SendDetails().execute(params);
	 }
	 
	 private class GetDetails extends AsyncTask<String, Void, String> {
	  	  private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	  	  private static final int WAIT_TIMEOUT = 30 * 1000;
	  	  private final HttpClient httpclient = new DefaultHttpClient();
	  	  final HttpParams params = httpclient.getParams();
	  	  HttpResponse response;
	  	  private String content =  null;
	  	  private boolean error = false;
	  	  private ProgressDialog dialog = new ProgressDialog(DetailsActivity.this);

	  	  protected void onPreExecute() {
	  	   dialog.setMessage("Getting the good stuff...");
	  	   dialog.show();
	  	  }

	  	  protected String doInBackground(String... urls) {

	  	   String URL = null;
	  	   
	  	   try {

	  	   URL = urls[0];
	  	   String poi_id = urls[1];
	  	   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
	  	   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
	  	   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

	  	   HttpPost httpPost = new HttpPost(URL);
	  	   //Log.v("Wai","URL: " +URL);
	  	   //add name value pair for the country code
	  	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  	   nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(poi_id)));
	  	   httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	  	   response = httpclient.execute(httpPost);
	  	   	
	  	    StatusLine statusLine = response.getStatusLine();
	  	    // Log.v("Wai","Status: " +response.getAllHeaders()[1]);
	  	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	  	     ByteArrayOutputStream out = new ByteArrayOutputStream();
	  	     response.getEntity().writeTo(out);
	  	     out.close();
	  	     content = out.toString();
	  	     Log.v("Wai","Content: " +content);
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
	  	   Toast toast = Toast.makeText(DetailsActivity.this, "Error connecting to Server", Toast.LENGTH_LONG);
	  	   toast.setGravity(Gravity.TOP, 25, 400);
	  	   toast.show();

	  	  }

	  	  protected void onPostExecute(String content) {
	  	   dialog.dismiss();
	  	   Toast toast;
	  	   if (error) {
	  	    toast = Toast.makeText(DetailsActivity.this, content, Toast.LENGTH_LONG);
	  	    toast.setGravity(Gravity.TOP, 25, 400);
	  	    toast.show();
	  	   } else {
	  	    try {
				DisplayDetails(content);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  	   }
	  	  }
	  	 
	  	 }
	 private void DisplayDetails(String content) throws JSONException {
		 JSONObject responseObj = new JSONObject(content);
		 JSONArray poiListObj = responseObj.getJSONArray("response");
		 TextView poiDetails = (TextView) findViewById(R.id.poiDetails);
		 for (int i=0; i<poiListObj.length(); i++){
			JSONObject j = poiListObj.getJSONObject(i);
			Iterator keys = j.keys();
		    while(keys.hasNext()) {
		    	String key = (String)keys.next();
		        String value = (String)j.get(key);
		        poiDetails.append(key +" : " + value + "\n");
		    }
			
	  	 }
		 GeoPoint startPoint = new GeoPoint(poilatitude, poilongitude);
		 myMapController.setCenter(startPoint);
	 }
	 
	 private class SendDetails extends AsyncTask<String, Void, String> {
	  	  private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	  	  private static final int WAIT_TIMEOUT = 30 * 1000;
	  	  private final HttpClient httpclient = new DefaultHttpClient();
	  	  final HttpParams params = httpclient.getParams();
	  	  HttpResponse response;
	  	  private String content =  null;
	  	  private boolean error = false;
	  	  private ProgressDialog dialog = new ProgressDialog(DetailsActivity.this);

	  	  protected void onPreExecute() {
	  	   dialog.setMessage("Updating Waikikamukau...");
	  	   dialog.show();
	  	  }

	  	  protected String doInBackground(String... urls) {

	  	   String URL = null;
	  	   
	  	   try {

	  	   URL = urls[0];
	  	   String id = urls[1];
	  	   String akey = urls[2];
	  	   String aval = urls[3];
	  	   Log.v("Wai", URL + " " + id + " " + akey + " " + aval);
	  	   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
	  	   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
	  	   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

	  	   HttpPost httpPost = new HttpPost(URL);
	  	   //Log.v("Wai","URL: " +URL);
	  	   //add name value pair for the country code
	  	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	  	   nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id)));
	  	   nameValuePairs.add(new BasicNameValuePair(akey,String.valueOf(aval)));
	  	   httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	  	   
	  	   response = httpclient.execute(httpPost);
	  	   	
	  	    StatusLine statusLine = response.getStatusLine();
	  	    Log.v("Wai","SEND Status: " +response.getStatusLine());
	  	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	  	     ByteArrayOutputStream out = new ByteArrayOutputStream();
	  	     response.getEntity().writeTo(out);
	  	     out.close();
	  	     content = out.toString();
	  	     Log.v("Wai","SEND Content: " +content);
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
	  	   Toast toast = Toast.makeText(DetailsActivity.this, "Error connecting to Server", Toast.LENGTH_LONG);
	  	   toast.setGravity(Gravity.TOP, 25, 400);
	  	   toast.show();

	  	  }

	  	  protected void onPostExecute(String content) {
	  	   dialog.dismiss();
	  	   Toast toast;
	  	   if (error) {
	  	    toast = Toast.makeText(DetailsActivity.this, content, Toast.LENGTH_LONG);
	  	    toast.setGravity(Gravity.TOP, 25, 400);
	  	    toast.show();
	  	   } else {
	  		 String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/GetAttributes"; 
             String[] params = {url,poiid};
             poiDetails.setText("");
             new GetDetails().execute(params);
	  	   }
	  	  }
	  	 
	  	 }
}

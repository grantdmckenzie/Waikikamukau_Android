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

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_AddAttr extends Activity {
	private String poiid;
	private ArrayList<Review> reviewList;
	private ReviewAdapter dataAdapter = null;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.layout_addattr);
	     Bundle extras = getIntent().getExtras();
         if (extras != null) {
            String poiname = extras.getString("poiname");
            poiid = extras.getString("poiid");
            TextView tv = (TextView) findViewById(R.id.poiname);
            tv.setText(poiname);
            Typeface bold =Typeface.createFromAsset(getAssets(),"fonts/Gotham-Bold.ttf");
            tv.setTypeface(bold);
            
            Button update =(Button)findViewById(R.id.andybutton);
            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	EditText andyentry = (EditText) findViewById(R.id.andyentry);
                	EditText andyname = (EditText) findViewById(R.id.andyname);
                    String text = andyentry.getText().toString();
                    String author = andyname.getText().toString();
                    updatePOI(poiid, author, text);
                }
            });
            
         }
	 }
	 private void updatePOI(String poiid, String author, String text) {
		 String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/UpdateAttributes"; 
		 String[] parts = text.split(":");
		 String[] params = {url,poiid, parts[0].trim(),parts[1].trim(), author.trim()};
         //andyvalue.setText("");
         new SendDetails().execute(params);
	 }
	 private class SendDetails extends AsyncTask<String, Void, String> {
	  	  private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	  	  private static final int WAIT_TIMEOUT = 30 * 1000;
	  	  private final HttpClient httpclient = new DefaultHttpClient();
	  	  final HttpParams params = httpclient.getParams();
	  	  HttpResponse response;
	  	  private String content =  null;
	  	  private boolean error = false;
	  	  private ProgressDialog dialog = new ProgressDialog(Activity_AddAttr.this);

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
	  	   String author = urls[4];
	  	   Log.v("Wai", URL + " " + id + " " + akey + " " + aval);
	  	   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
	  	   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
	  	   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

	  	   HttpPost httpPost = new HttpPost(URL);
	  	   //Log.v("Wai","URL: " +URL);
	  	   //add name value pair for the country code
	  	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	  	   nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id)));
	  	   nameValuePairs.add(new BasicNameValuePair(akey,String.valueOf(aval)));
	  	   nameValuePairs.add(new BasicNameValuePair("author",String.valueOf(author)));
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
	  	   Toast toast = Toast.makeText(Activity_AddAttr.this, "Error connecting to Server", Toast.LENGTH_LONG);
	  	   toast.setGravity(Gravity.TOP, 25, 400);
	  	   toast.show();

	  	  }

	  	  protected void onPostExecute(String response) {
	  	   dialog.dismiss();
	  	   Toast toast;
	  	   if (error) {
	  	    toast = Toast.makeText(Activity_AddAttr.this, response, Toast.LENGTH_LONG);
	  	    toast.setGravity(Gravity.TOP, 25, 400);
	  	    toast.show();
	  	   } else {
	  		   // do something
	  	   }
	  	  }
	  	 
	  	 }

}

package edu.ucsb.waikikamukau_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Activity_NewPoiSend extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newpoisend);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double latitude = extras.getDouble("lat");
            double longitude = extras.getDouble("lng");
            String name = extras.getString("name");
            String cat = extras.getString("cat");

            String[] params = new String[4];
            params[0] = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/AddPoi"; // ?lat=34.43&lng=-119.92";
            params[1] = latitude+"";
            params[2] = longitude+"";   
            params[3] = name;
            params[4] = cat;
            new GetNearby().execute(params);

            TextView d = (TextView) findViewById(R.id.details);
            d.setText("New POI added with the following details: \n\nName:"+name+"\nLatitude: "+latitude+"\nLongitude: "+longitude);
        }
    }

    private class GetNearby extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        private ProgressDialog dialog = new ProgressDialog(Activity_NewPoiSend.this);

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
                    Log.w("HTTP1:", statusLine.getReasonPhrase());
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
            Toast toast = Toast.makeText(Activity_NewPoiSend.this, "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }

        protected void onPostExecute(String content) {
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(Activity_NewPoiSend.this, content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } else {
                displayResults(content);
            }
        }
        private void displayResults(String response) {


        }

    }

}

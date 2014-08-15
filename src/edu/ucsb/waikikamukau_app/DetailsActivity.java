package edu.ucsb.waikikamukau_app;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	private MapView myOpenMapView;
	 private MapController myMapController;
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String poiname = extras.getString("poiname");
            String poidist = extras.getString("poidistance");
            String poilatitude = extras.getString("poilat");
            String poilongitude = extras.getString("poilng");
            String poiid = extras.getString("poiid");
            
            GeoPoint startPoint = new GeoPoint(34.415343, -119.845135);
            myOpenMapView = (MapView)findViewById(R.id.openmapview);
            myOpenMapView.setMultiTouchControls(true);
            myMapController = (MapController) myOpenMapView.getController();
            myMapController.setZoom(17);
            myMapController.setCenter(startPoint);
            
            Drawable mainMarker = this.getResources().getDrawable(R.drawable.marker01_30);
            mainMarker.setBounds(0 - mainMarker.getIntrinsicWidth() / 2, 0 - mainMarker.getIntrinsicHeight(),mainMarker.getIntrinsicWidth() / 2, 0);
            
            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", startPoint);
            overlayItem.setMarker(mainMarker);
            items.add(overlayItem);
          
            
            TextView tv = (TextView) findViewById(R.id.poiname);
            TextView dist = (TextView) findViewById(R.id.poidistance);
            tv.setText(poiname);
            dist.setText("Distance to Point of Interest: "+poidist);
            
            Log.v("Wai", poiid + ", " + poilongitude);
            Typeface ralewaybold =Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
            Typeface ralewaythin =Typeface.createFromAsset(getAssets(),"fonts/Raleway-Light.ttf");
            tv.setTypeface(ralewaybold);
            dist.setTypeface(ralewaythin);
            
            
        }
    }
}

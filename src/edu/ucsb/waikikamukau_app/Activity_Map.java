package edu.ucsb.waikikamukau_app;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Activity_Map extends Activity {
	 private MapView myOpenMapView;
	 private MapController myMapController;
	 private LocationManager locationManager;
	 private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
	 private ResourceProxy mResourceProxy;
	 private ArrayList<OverlayItem> items;
	 private Drawable mainMarker;
	 private double mLongitude;
	 private double mLatitude;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLatitude = extras.getDouble("lat");
            mLongitude = extras.getDouble("lng");
        } else {
        	mLatitude = 34.43;
            mLongitude = -119.9;	
        }
        
        
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        
        // String url = "http://api.geonames.org/postalCodeSearchJSON?postalcode=9011&maxRows=10&username=demo";
        //String url = "http://stko-testing.geog.ucsb.edu:8080/Waikikamukau/Nearby"; // ?lat=34.43&lng=-119.92";
        //new GetNearby().execute(url);
        
        GeoPoint startPoint = new GeoPoint(mLatitude, mLongitude);
        myOpenMapView = (MapView)findViewById(R.id.openmapview);
        myOpenMapView.setMultiTouchControls(true);
        myMapController = (MapController) myOpenMapView.getController();
        myMapController.setZoom(16);
        
      
        
        mainMarker = this.getResources().getDrawable(R.drawable.marker01_30);
        mainMarker.setBounds(0 - mainMarker.getIntrinsicWidth() / 2, 0 - mainMarker.getIntrinsicHeight(),mainMarker.getIntrinsicWidth() / 2, 0);
        
        items = new ArrayList<OverlayItem>();
        OverlayItem overlayItem = new OverlayItem("Here", "SampleDescription", new GeoPoint(mLatitude, mLongitude));
        overlayItem.setMarker(mainMarker);
        items.add(overlayItem);
        
        this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items, new Glistener() , mResourceProxy);
        this.myOpenMapView.getOverlays().add(this.mMyLocationOverlay);
        myOpenMapView.invalidate();
        myMapController.setCenter(startPoint);
    }

}

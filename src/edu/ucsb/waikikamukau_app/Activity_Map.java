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
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

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
     private GoogleMap map;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);
        setUpBottomTabs();
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLatitude = extras.getDouble("lat");
            mLongitude = extras.getDouble("lng");
        } else {
        	mLatitude = 34.43;
            mLongitude = -119.9;	
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map!=null){
            LatLng p = new LatLng(mLatitude, mLongitude);
            //Marker marker = map.addMarker(new MarkerOptions().position(p));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
            // map.setOnCameraChangeListener(getCameraChangeListener());
            map.getUiSettings().setZoomControlsEnabled(false);
            TileOverlayOptions opts = new TileOverlayOptions();
            opts.tileProvider(new MapBoxOnlineTileProvider("grantdmckenzie.je0ai8ba"));
            opts.zIndex(1);
            TileOverlay overlay = map.addTileOverlay(opts);

        }
        

    }
	private void setUpBottomTabs() {
		 ImageView map = (ImageView) findViewById(R.id.map_icon);
		 ImageView list = (ImageView) findViewById(R.id.list_icon);
		 map.setImageResource(R.drawable.map_a);
		 list.setImageResource(R.drawable.list);
	     list.setOnClickListener(new OnClickListener() {
	         public void onClick(View v) {
	  	         Intent mapView = new Intent(Activity_Map.this, Activity_Main.class);
	  	         startActivity(mapView);
	         }
	     });
	 }

}

package com.example.gpsview;

import java.text.DecimalFormat;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class GpsView extends MapActivity 
{    
	private TextView textSpeed, textDistance;
	
	private LocationManager lm;
    private MyLocationListener locationListener;

    private MapView mapView;
    private MapController mc;
    private MyLocationOverlay myLocationOverlay;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        textSpeed = (TextView) findViewById(R.id.speed);
        textDistance = (TextView) findViewById(R.id.distance);
        
        //---use the LocationManager class to obtain GPS locations---
        lm = (LocationManager) 
            getSystemService(Context.LOCATION_SERVICE);    
        
        locationListener = new MyLocationListener();
        
        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 
            0, 
            0, 
            locationListener);
        
        mapView = (MapView) findViewById(R.id.mapview1);
        mc = mapView.getController();
        
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.runOnFirstFix(new Runnable() {
        	public void run() {
        		mc.animateTo(myLocationOverlay.getMyLocation());
        		mc.setZoom(16);
        	}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean supRetVal = super.onCreateOptionsMenu(menu);
    	menu.add(Menu.NONE, 0, Menu.NONE, "Reset");
    	menu.add(Menu.NONE, 1, Menu.NONE, "Exit");
    	return supRetVal;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case 0:
    			locationListener.totdist = 0;
    			textDistance.setText("0.00");
    			return true;
    		case 1:
    			this.finish();
    			return true;
    	}
    	return false;
    }
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }        
    
    private class MyLocationListener implements LocationListener 
    {
    	Location oldloc;
    	long currtime = 0, lasttime = 0;
    	public float totdist = 0;
    	
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {         
            	DecimalFormat onedecimal = new DecimalFormat("#0.0");
            	DecimalFormat twodecimal = new DecimalFormat("#0.00");
            	
            	lasttime = currtime;
            	currtime = System.currentTimeMillis();
            	
            	float dist = 0;
            	
            	if (oldloc != null) {
                    dist = loc.distanceTo(oldloc);
                    totdist += dist;
            	}
            	
            	long elapsedtime = (currtime - lasttime);
            	float speed = dist / elapsedtime * 1000;
            	
            	/*
                textview1.setText("Distance since last point: " + onedecimal.format(dist) + " meters" +
                		//" meters\nLat: " + loc.getLatitude() + "\nLong: " + loc.getLongitude() +
                		"\nTotal distance: " + onedecimal.format(totdist) + " meters" +
                		"\nSpeed: " + onedecimal.format(speed) + " m/s");
                */
            	
            	textSpeed.setText(onedecimal.format(speed*3.6));
            	textDistance.setText(twodecimal.format(totdist/1000));
            	
                GeoPoint p = new GeoPoint(
                        (int) (loc.getLatitude() * 1E6), 
                        (int) (loc.getLongitude() * 1E6));
                mc.animateTo(p);
                mc.setZoom(16);                
                mapView.invalidate();
                
            	oldloc = new Location(loc);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}

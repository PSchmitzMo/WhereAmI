package de.ps.android.wai;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.ps.android.wai.function.Persistence;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
						  implements OnMapClickListener,
						   			 OnInfoWindowClickListener
{    
	private GoogleMap googleMap;
	
	@Override    
	protected void onCreate(Bundle savedInstanceState) 
	{        
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.activity_main);
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
		
		if(status!= ConnectionResult.SUCCESS)
		{ 
			// Google Play Services are not available             
		   	int requestCode = 10;            
		   	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);            
		   	dialog.show();         
		}
		
		Persistence.init( this );
   
		// Getting reference to the SupportMapFragment of activity_main.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		// Getting GoogleMap object from the fragment
		googleMap = fm.getMap();
		googleMap.setOnMapClickListener(this);

		// Enabling MyLocation Layer of Google Map
		googleMap.setMyLocationEnabled(true);
		
	    Intent intent = getIntent();
	    double lat = intent.getDoubleExtra( "LAT", Double.NEGATIVE_INFINITY );
	    double lng = intent.getDoubleExtra( "LNG", Double.NEGATIVE_INFINITY );
	    
	    if( lat > Double.NEGATIVE_INFINITY )
	    {
		    LatLng position = new LatLng( lat, lng );
		    moveTo( position );
		    
		    BitmapDescriptorFactory.defaultMarker();
		    
			googleMap.addMarker(new MarkerOptions()
			.title( intent.getStringExtra("ADDRESS") )
			.position(position).
			icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_place)));
	    }
	    else
	    {
	        LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);//use of location services by firstly defining location manager.
	        String provider=lm.getBestProvider(new Criteria(), true);

			android.location.Location loc = lm.getLastKnownLocation(provider);
			if ( loc !=null )
			{
				LatLng latlng=new LatLng(loc.getLatitude(),loc.getLongitude());// This methods gets the users current longitude and latitude.
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,(float) 14.6));
			}
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	  } 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
		    // action with ID action_refresh was selected
		    case R.id.action_settings:
		      Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
		          .show();
		      break;
		    case R.id.action_address:
		    	startAddressActivity();
		    default:
		      break;
	    }

	    return true;
	  } 


	private void startAddressActivity()
	{
	    Intent intent = new Intent(this, AddressActivity.class);
	    startActivity(intent);
	}
	
	@Override
	public void onMapClick( LatLng position) 
	{
		googleMap.addMarker(new MarkerOptions()
							.title( "Hallo" )
							.position(position).
							icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_place)));
	}
	
	public void moveTo( LatLng position )
	{
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
	}

	@Override
	public void onInfoWindowClick(Marker arg0) 
	{
	}
}

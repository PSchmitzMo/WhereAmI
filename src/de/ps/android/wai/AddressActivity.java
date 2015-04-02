package de.ps.android.wai;

import de.ps.android.wai.function.Persistence;
import de.ps.android.wai.type.Address;
import de.ps.android.wai.type.Location;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class AddressActivity extends Activity 
						     implements AsyncTaskMaster
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_display);
		
		Spinner land = (Spinner) findViewById(R.id.land);
		land.requestFocus();	
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		if( savedInstanceState != null )
		{
			// Spinner land = (Spinner) findViewById(R.id.land);
			// @@@ land.setText( savedInstanceState.getString("country"));
			
			EditText ort = (EditText) findViewById(R.id.ort);
			String o = savedInstanceState.getString("place");
			ort.setText( o );

			EditText strasse = (EditText) findViewById(R.id.strasse);
			strasse.setText( savedInstanceState.getString("street"));
		}
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		// @@@ savedInstanceState.putString("country", ((Spinner)findViewById(R.id.land)).getSelectedItem().toString().toString() );
		savedInstanceState.putString("place", ((EditText)findViewById(R.id.ort)).getText().toString() );
		savedInstanceState.putString("street", ((EditText)findViewById(R.id.strasse)).getText().toString() );
		super.onSaveInstanceState(savedInstanceState);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.address, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.action_map:
				switchToMapActivity();
			default:
				break;
		}

		return true;
	}

	private void switchToMapActivity( Location location )
	{
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.putExtra( "LAT", location.getPosition().latitude );
	    intent.putExtra( "LNG", location.getPosition().longitude );
	    intent.putExtra( "ADDRESS", location.getAddress() );
	    finish();
	    startActivity(intent);
	}
	
	private void switchToMapActivity()
	{
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
	
	
	private void getCoordinates()
	{
		GeoAddressResolver gar = new GeoAddressResolver( this );
		
		Spinner land = (Spinner) findViewById(R.id.land);
		String l = land.getSelectedItem().toString();
		
		EditText ort = (EditText) findViewById(R.id.ort);
		String o = ort.getText().toString();

		EditText strasse = (EditText) findViewById(R.id.strasse);
		String s = strasse.getText().toString();
		
		String address = "";
		if( !l.isEmpty() )
			address = l;
		
		if( !o.isEmpty() )
			address += " " + o;

		if( !s.isEmpty() )
			address += " " + s;
		
		gar.execute( address );
	}
	
	private void saveAddress()
	{
		Address address = new Address();  
		
		
		Spinner land = (Spinner) findViewById(R.id.land);
		String l = land.getSelectedItem().toString();
		
		EditText ort = (EditText) findViewById(R.id.ort);
		String o = ort.getText().toString();

		EditText strasse = (EditText) findViewById(R.id.strasse);
		String s = strasse.getText().toString();
		
		address.setCountry( l );
		address.setPlace( o );
		address.setStreet( s );
		
		Persistence.instance().saveAddress( address );
	}

	public void onSearchClick(View v)	
	{
		switch (v.getId() ) 
		{
		    case R.id.search:
		    	getCoordinates();
		    default:
		    	break;
		}
	}

	public void onFavoriteClick(View v)	
	{
		switch (v.getId() ) 
		{
		    case R.id.toFavorites:
		    	saveAddress();
		    default:
		    	break;
		}
	}
	
	
	@Override
	public void onError( Throwable ex )
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		
		dlgAlert.setMessage( ex.getMessage() );
		dlgAlert.setTitle("Error");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}

	@Override
	public void onResult( Location result) 
	{
		switchToMapActivity( result );
	}

}

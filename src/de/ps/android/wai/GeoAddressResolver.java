package de.ps.android.wai;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 

import com.google.android.gms.maps.model.LatLng;

import de.ps.android.wai.type.Location;

import android.os.AsyncTask;

public class GeoAddressResolver extends AsyncTask<String, Void, Location>
{
	AsyncTaskMaster master = null;
    private Throwable exception = null;
    
    public GeoAddressResolver( AsyncTaskMaster master )
    {
    	this.master = master;
    }
    
    protected Location doInBackground(String... addresses) 
	{
	    String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
	                  addresses[0] + "&sensor=false";
	    
	    uri = uri.replaceAll(" ", "%20");
	    
	    HttpGet httpGet = new HttpGet(uri);
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();
	
	    try 
	    {
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) 
	        {
	            stringBuilder.append((char) b);
	        }
	    } 
	    catch (ClientProtocolException  e) 
	    {
	        e.printStackTrace();
	        exception = e;
	        return null;
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        exception = e;
	        return null;
	    }

	    JSONObject jsonObject = new JSONObject();
	    try 
	    {
	        jsonObject = new JSONObject(stringBuilder.toString());
	
	        JSONArray resArray = (JSONArray)jsonObject.get("results");
	        
	        if( ( resArray!= null ) && ( resArray.length() > 0 ) )
	        {
		        double lng = resArray.getJSONObject(0)
		        			 .getJSONObject("geometry").getJSONObject("location")
		        			 .getDouble("lng");
		
		        double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
		            .getJSONObject("geometry").getJSONObject("location")
		            .getDouble("lat");
		        
		        Location location = new Location( addresses[0], 
		        								  new LatLng( lat, lng ) );
		    	
		        return location;
	        }
	        else
	        {
	        	exception = new Exception( "Adresse ist unbekannt");
	        	return null;
	        }
	    } 
	    catch (JSONException e) 
	    {
	        e.printStackTrace();
	        exception = e;
	        return null;
	    }

	}
    
    @Override
    protected void onPostExecute( Location location ) 
    {
    	if( exception != null )
    		master.onError( exception );
    	else
    		master.onResult( location );
    }
    
}

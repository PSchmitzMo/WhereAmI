package de.ps.android.wai.function;

import java.util.ArrayList;
import java.util.List;

import de.ps.android.wai.type.Address;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Persistence extends SQLiteOpenHelper  
{
	private static Persistence instance = null;
	private SQLiteDatabase database;
	
	private static final String DATABASE_NAME = "AddressDB";
    private static final int DATABASE_VERSION = 3;
	private static final String ADDRESS_TABLE_NAME = "address";
	
	private static final String COL_ID = "_id";
	private static final String COL_COUNTRY = "country";
	private static final String COL_PLACE = "place";
	private static final String COL_STREET = "street";
	
	    private static final String ADDRESS_TABLE_CREATE =
	                "CREATE TABLE " + ADDRESS_TABLE_NAME + " (" +
	                		COL_ID + " integer primary key autoincrement, " + 
	                		COL_COUNTRY + " text, " +
	                		COL_PLACE + " text, " +
	                		COL_STREET + " TEXT);";
	    
	private static String getDbPath( Context context )
	{
		return context.getExternalFilesDir(null).getAbsolutePath() + "/";		
	}

	private Persistence(Context context)
	{
        super(context, getDbPath( context ) + DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        instance = this;
	}
	
	public static void init( Context context )
	{
		instance = new Persistence(context);
	}
	
	public static Persistence instance()
	{
		return instance;
	}
	
	public void saveAddress( Address address )
	{
	    ContentValues values = new ContentValues();
	    
	    values.put(COL_COUNTRY, address.getCountry());
	    values.put(COL_PLACE, address.getPlace());
	    values.put(COL_STREET, address.getStreet());
	    
	    database.insert( ADDRESS_TABLE_NAME, null, values);
	}
	
	public void deleteAddress( Address address )
	{
		
	}
	
	public List<Address> readAddresses()
	{
		List<Address> addresses = new ArrayList<Address>();
		Cursor cursor = database.query( ADDRESS_TABLE_NAME, 
										null, 
										null, 
										null, 
										null, 
										null, 
										null );		
		
		if( cursor.moveToFirst() )
			return addresses;
		
		do
		{
			int cIndex = cursor.getColumnIndex( COL_COUNTRY );
			int pIndex = cursor.getColumnIndex( COL_PLACE );
			int sIndex = cursor.getColumnIndex( COL_STREET );
			
			Address address = new Address();
			address.setCountry( cursor.getString( cIndex ) );
			address.setPlace( cursor.getString( pIndex ) );
			address.setStreet( cursor.getString( sIndex ) );
			
			addresses.add( address );
		}
		while( !cursor.moveToNext() );
		
		return addresses;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			db.execSQL(ADDRESS_TABLE_CREATE);
		}
		catch( Throwable ex )
		{
			Log.d( "PERSISTENCE", ex.toString() );
		}
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, 
						   int oldVersion, int newVersion) 
	{
		try
		{
			db.execSQL( "drop table " + ADDRESS_TABLE_NAME );
			db.execSQL(ADDRESS_TABLE_CREATE);
		}
		catch( Throwable ex )
		{
			Log.d( "PERSISTENCE", ex.toString() );
		}
	}
}

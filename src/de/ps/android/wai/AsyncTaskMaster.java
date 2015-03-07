package de.ps.android.wai;

import de.ps.android.wai.type.Location;


interface AsyncTaskMaster 
{
	void onError( Throwable ex );
	void onResult( Location result );
}

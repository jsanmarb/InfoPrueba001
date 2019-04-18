package cl.infositio.infoprueba001;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	public static final String	TAG												= "infoprueba001";

	public static final String	EXTRA_MESSAGE									= "cl.infositio.infoprueba001.MESSAGE";

	public final static int		MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE	= 1;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}



	/**
	 * Called when the user taps the Send button
	 */
	public void sendMessage(View view)
	{
		// Intent intent= new Intent(this, DisplayMessageActivity.class);
		// EditText editText= (EditText) findViewById(R.id.editText);
		// String message= editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		// startActivity(intent);
		this.descargar_archivo(0);
	}



	private void descargar_archivo(int etapa)
	{
		int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		Log.d(TAG, "permissionCheck: " + permissionCheck);

		if(etapa == 0)
		{
			// Here, thisActivity is the current activity
			if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{

				// Should we show an explanation?
				if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				{

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.

				}
				else
				{

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
				}
			}
			else
				this.descargar_archivo(1);
		}
		else
		{
			// eliminamos el archivo anterior
			try
			{
				String storage= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/inforeader.apk";
				File file= new File(storage);
				if(file.exists()) file.delete();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			// intentamos descarga en directorio publico
			DownloadManager downloadmanager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri= Uri.parse("http://descargas.infositio.cl/inforeader/saesa/inforeader_enlac/InfoReader3_EnlaC_v_0_7_QA.apk");

			DownloadManager.Request request= new DownloadManager.Request(uri);
			request.setTitle("InfoReader");
			request.setDescription("Descargando");
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setVisibleInDownloadsUi(true);
			// request.setDestinationUri(Uri.parse("file://apks/inforeader.apk"));
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "inforeader.apk");

			Log.d(TAG, "Environment.DIRECTORY_DOWNLOADS: " + Environment.DIRECTORY_DOWNLOADS);

			downloadmanager.enqueue(request);
		}
	}



	public void instalar_apk(View view)
	{
		Log.d(TAG, "instalar_apk: iniciando.");

		String storage= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/inforeader.apk";
		File file= new File(storage);
		Uri uri;

		if(!file.exists())
		{
			Toast.makeText(super.getApplicationContext(), "¡Primero debes descargar el apk!", Toast.LENGTH_LONG).show();
			return;
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			Log.d(TAG, "authority: " + this.getApplicationContext().getPackageName());
			Log.d(TAG, "file: " + file + "; existe: " + file.exists());

			uri= FileProvider.getUriForFile(super.getApplicationContext(), "cl.infositio.infoprueba001.fileprovider", file);
		}
		else
			uri= Uri.fromFile(file);

		Intent intent= new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		super.getApplicationContext().startActivity(intent);
	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch(requestCode)
		{
			case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
			{
				// If request is cancelled, the result arrays are empty.
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					this.descargar_archivo(1);

				}
				else
				{

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
}

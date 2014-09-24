package com.andres.sun4all;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Main extends Activity {

	private ImageView img;
	private int contador = 0;
	//private Bitmap loadedImage;
	//private String address = "http://www.losporque.com/wp-content/uploads/2008/09/el_origen_de_las_manchas_solares.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img = (ImageView) findViewById(R.id.ImgFoto);
		img.setImageResource(R.drawable.sol);
		TextView txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		//downloadFile(address);
	}
	void downloadFile(String address){
		URL imageUrl = null;
		try{
			imageUrl = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			Log.i("downloadFile", "2 -> "+address);
			conn.connect();
			Log.i("downloadFile", "3");
			//loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
			Log.i("downloadFile", "4");
			//imageView.setImageBitmap(loadedImage);
			Log.i("downloadFile", "5");
			
		}catch(Exception e){
			Log.i("downloadFile", "estoy en catch");
			Toast.makeText(getApplicationContext(), "Error al cargar la imagen"+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
}

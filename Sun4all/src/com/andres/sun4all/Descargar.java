package com.andres.sun4all;

import java.io.InputStream;
import java.net.URL;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Descargar extends AsyncTask<String, void, void> 
{
	Drawable imgLoad;
	ProgressBar progressbar;
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressbar.setVisibility(View.VISIBLE);
	}
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		imgLoad = LoadImageFromWeb(params[0]);
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(progressbar.isShown())
		{
			progressbar.setVisibility(View.GONE);
			imgLogo.setVisibility(View.VISIBLE);
			imgLogo.setBackgroundDrawable(imgLoad);
		}
	}
	/** Constructor*/
	void Descargar(String link){

	}
	Bitmap getBitmap(){
		return bitmap;
	}

}
/*Bitmap bitmap;
URL url;
String link;*/

/*void Descargar(String link){
	try{
		this.link = link;
		url = new URL(link);
		InputStream in = url.openStream();
		BufferedOutputStream  out = new BufferedOutputStream
				(new FileOutputStream("testImage.png"));
		int i;
		while((i=in.read())!= -1){
			out.write(i);
		}
		out.close();
		in.close();
		
		BufferedInputStream buf = new BufferedInputStream(in);
		bitmap = BitmapFactory.decodeStream(buf);
		//image.setImageBitmap(bitmap);
		if (in != null) {
	        in.close();
        }
        if (buf != null) {
        	buf.close();
        }
	}catch (Exception e){
		Log.e("Error reading file", e.toString());
	}
	
}*/
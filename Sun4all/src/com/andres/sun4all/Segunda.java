package com.andres.sun4all;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class Segunda extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segunda);
		
		
		//TouchImageView im = (TouchImageView)findViewById(R.id.ImgFoto2);
		TextView t = (TextView)findViewById(R.id.textView1);
	}

	
}

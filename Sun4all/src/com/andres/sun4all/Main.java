package com.andres.sun4all;

import java.io.IOException;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class Main extends Activity {

	private int contador = 0;
	Imagen IM;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		Log.i("onCreate", "creando");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		ImageView img = (ImageView) findViewById(R.id.ImgFoto);
		//img.setImageResource(R.drawable.sol);
		img.setOnTouchListener(handlerMover);
		
		IM = new Imagen(img);
		
		TextView txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		
		final Button btnAdd =(Button)findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Main.this, Imagen.class);
			}
			
		});
		//downloadFile(address);
	}
	
	View.OnTouchListener handlerMover = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//
			return IM.touch(v, event);
			//
			//Log.i("pantalla", String.valueOf(event.getX())+" - "+String.valueOf(event.getY()));
		    /*ImageView view = (ImageView) v;
		    dumpEvent(event);

		    // Handle touch events here...
		    switch (event.getAction() & MotionEvent.ACTION_MASK) {
		    case MotionEvent.ACTION_DOWN:
		        savedMatrix.set(matrix);
		        start.set(event.getX(), event.getY());
		        Log.d("accion", "mode=PULSADO");
		        mode = PULSADO;
		        break;
		    case MotionEvent.ACTION_POINTER_DOWN:
		    	//Log.i("accion","ACTION_POINTER_DOWN");
		        oldDist = spacing(event);
		        Log.d("accion", "oldDist=" + oldDist);
		        if (oldDist > 10f) {
		            savedMatrix.set(matrix);
		            midPoint(mid, event);
		            mode = ZOOM;
		            Log.d("accion", "mode=ZOOM");
		        }
		        break;
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		    	Log.i("accion","ACTION_UP"+mode);
		        mode = NONE;
		        Log.d("accion", "mode=NONE");
		        break;
		    case MotionEvent.ACTION_MOVE:
		    	//Log.i("accion","ACTION_MOVE");
		        if (mode == PULSADO) {
		            Log.i("mode","drag");
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x, event.getY()
		                    - start.y);
		        } else if (mode == ZOOM) {
		        	Log.i("mode","zoom");
		            float newDist = spacing(event);
		            //Log.d(TAG, "newDist=" + newDist);
		            if (newDist > 10f) {
		                matrix.set(savedMatrix);
		                float scale = newDist / oldDist;
		                //scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
		                matrix.postScale(scale, scale, mid.x, mid.y);
		            }
		        }
		        break;
		    }

		    view.setImageMatrix(matrix);
		    logMatrix(matrix, view);
		    return true;*/
		}
	};
	

	

	/*private void dumpEvent(MotionEvent event) {
	    String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
	            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
	    StringBuilder sb = new StringBuilder();
	    int action = event.getAction();
	    int actionCode = action & MotionEvent.ACTION_MASK;
	    sb.append("event ACTION_").append(names[actionCode]);
	    if (actionCode == MotionEvent.ACTION_POINTER_DOWN
	            || actionCode == MotionEvent.ACTION_POINTER_UP) {
	        sb.append("(pid ").append(
	                action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	        sb.append(")");
	    }
	    sb.append("[");
	    for (int i = 0; i < event.getPointerCount(); i++) {
	        sb.append("#").append(i);
	        sb.append("(pid ").append(event.getPointerId(i));
	        sb.append(")=").append((int) event.getX(i));
	        sb.append(",").append((int) event.getY(i));
	        if (i + 1 < event.getPointerCount())
	            sb.append(";");
	    }
	    sb.append("]");
	    //Log.d(TAG, sb.toString());
	}

	*//** Determine the space between the first two fingers *//*
	private float spacing(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}

	*//** Calculate the mid point of the first two fingers *//*
	private void midPoint(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}*/

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
	/*void downloadFile(String address)//esto no va
	{
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
	}*/
}

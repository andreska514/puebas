package com.andres.sun4all;

//https://github.com/sephiroth74/ImageViewZoom
import java.io.IOException;


import android.util.FloatMath;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Imagen {

	//variables para el zoom
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	private static final float MIN_ZOOM = 1.0f;
	private static final float MAX_ZOOM = 3f;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;
	
	
	int mode = NONE;
	
	Imagen(ImageView img)//Constructor
	{
		//img = (ImageView) findViewById(R.id.ImgFoto);
		img.setImageResource(R.drawable.sol);
		
		//img.setOnTouchListener(handlerMover);
	}
	
	private void logMatrix(Matrix matrix, ImageView imageView){
		float[] values = new float[9];
		matrix.getValues(values);
		float globalX = values[2];
        float globalY = values[5];
        float width = values[0]* imageView.getWidth();
        float height = values[4] * imageView.getHeight();

        Log.i("Log value", "x: " + globalX 
        		+ " y: " + globalY + "width: " + width 
        		+ " height: " + height);
	}

    private void dumpEvent(MotionEvent event) {
	    String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
	            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
	    StringBuilder sb = new StringBuilder();
	    int action = event.getAction();
	    int actionCode = action & MotionEvent.ACTION_MASK;
	    sb.append("event ACTION_").append(names[actionCode]);
	    if (actionCode == MotionEvent.ACTION_POINTER_DOWN
	            || actionCode == MotionEvent.ACTION_POINTER_UP) 
	    {
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

	/** Determina el espacio entre los 2 primeros dedos*/
	private float spacing(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}

	/** Calcula el punto medio entre los 2 dedos*/
	private void midPoint(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
    public boolean touch(View v, MotionEvent event)
    {
    	//Log.i("pantalla", String.valueOf(event.getX())+" - "+String.valueOf(event.getY()));
	    ImageView view = (ImageView) v;
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
	        } 
	        else if (mode == ZOOM) {
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
	    }//fin switch

	    view.setImageMatrix(matrix);
	    logMatrix(matrix, view);
	    return true;
    }//fin touch
    
    //******************************************************************************
    //**************** Metodos por comprobar / Metodos no usados *******************
    //******************************************************************************
    
	private float getXValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
           matrix.getValues(values);
           float globalX = values[2];

           return globalX;
    }
	private float getYValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
           matrix.getValues(values);
           float globalY = values[5];

           return globalY;
    }
	private float getWidthFromMatrix(Matrix matrix, ImageView imageview) {
        float[] values = new float[9];
           matrix.getValues(values);

           float width = values[0]* imageview.getWidth();

           return width;
    }
    private float getHeightFromMatrix(Matrix matrix, ImageView imageview) {

        float[] values = new float[9];
           matrix.getValues(values);

           float height = values[4] * imageview.getHeight();

           return height;
    }

}

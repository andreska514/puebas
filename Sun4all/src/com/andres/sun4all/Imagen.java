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
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Imagen {
	
	// Matrix para el zoom
	static Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	
	// 3 posibles estados
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	
	// cosas para el zoom
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	
	int mode = NONE;
	
	static ImageView imageView;
	float[]valores;
	
	Imagen(ImageView imView)//Constructor
	{
		// 0 4 zoom actual x y de la imagen(tamaño si lo multiplicas por width/height)
		// 2 5 posiciones x y del matrix (muy raro)
		imView.setImageResource(R.drawable.sol);
		imView.setCropToPadding(true);
		
		imageView = imView;
	}
    public boolean touch(View v, MotionEvent event)
    {
    	imageView =(ImageView) v;
		imageView.setScaleType(ScaleType.MATRIX);
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
    //pulsar 1	    
		    case MotionEvent.ACTION_DOWN:
		        savedMatrix.set(matrix);
		        start.set(event.getX(), event.getY());
		        Log.d("accion", "mode=PULSADO");
		        mode = PULSADO;
		        imageView.setImageMatrix(matrix);
		        break;
    //pulsar 2
		    case MotionEvent.ACTION_POINTER_DOWN:
		        oldDist = espacio(event);
		        Log.d("accion", "oldDist=" + oldDist);
		        if (oldDist > 10f) {
		            savedMatrix.set(matrix);
		            puntoMedio(mid, event);
		            mode = ZOOM;
		        }
		        imageView.setImageMatrix(matrix);
		        break;
	//soltar
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		        mode = NONE;
		        Log.d("accion", "mode=NONE");
		        imageView.setImageMatrix(matrix);
		        break;
	//mover dedos
		    case MotionEvent.ACTION_MOVE:
		        if (mode == PULSADO) {
		        	log("ACTION_MOVE -> pulsado");
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x, 
		            		event.getY() - start.y);
		            compruebaValores();
		        } 
		        else if (mode == ZOOM) {
		        	log("ACTION_MOVE -> zoom");
		            float newDist = espacio(event);
	                matrix.set(savedMatrix);
	                float scale = newDist / oldDist;
	                matrix.postScale(scale, scale, mid.x, mid.y);  
	                compruebaValores();
		        }
		        imageView.setImageMatrix(matrix);
		        break;
	    }//fin switch
	    return true;
    }//fin touch
    //comprueba el zoom para concretar los límites
    public void compruebaValores(){
    	//log("compruebaValores");
    	valores = new float[9];
    	matrix.getValues(valores);
    	
        if (valores[0]<=0.9f){
        	valores[0]=0.9f;
        	valores[4]=0.9f;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	imageView.setImageMatrix(matrix);
        }else if(valores[0]>0.9 && valores[0]<=1){
        	determinaMax(-86, -40);
        }else if(valores[0]>1 && valores[0]<=1.1){
        	determinaMax(-166, -123);
        }else if(valores[0]>1.1 && valores[0]<=1.2){
        	determinaMax(-246, -201);
        }else if(valores[0]>1.2 && valores[0]<=1.3){
        	determinaMax(-316, -280);
        }else if(valores[0]>1.3 && valores[0]<=1.4){
        	determinaMax(-409, -359);
        }else if(valores[0]>1.4 && valores[0]<=1.5){
        	determinaMax(-484, -435);
        }else if(valores[0]>1.5 && valores[0]<=1.6){
        	determinaMax(-563, -521);
        }else if(valores[0]>1.6 && valores[0]<=1.7){
        	determinaMax(-644, -600);
        }else {// (valores[0]>1.7f){
        	valores[0]=1.7f;
        	valores[4]=1.7f;
        	determinaMax(-664, -600);
        }
    }
    void determinaMax(float valorX, float valorY){
    	//log("determinando");
    	//if(x>0)
    	if(valores[2]>0){
    		valores[2]=0;
    		matrix.setValues(valores);
    	}//if(y>0)
    	if(valores[5]>0){
    		valores[5]=0;
    		matrix.setValues(valores);
    	}//if(x<valorX)
    	if(valores[2]< valorX){
    		valores[2]= valorX;
    		matrix.setValues(valores);
    	}//if(y<valorY)
    	if(valores[5]< valorY){
    		valores[5]= valorY;
    		matrix.setValues(valores);
    	}
    	imageView.setImageMatrix(matrix);
    }
    
    /** Determina el espacio entre los 2 primeros dedos*/
	private float espacio(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** Calcula el punto medio entre los 2 dedos*/
	private void puntoMedio(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
	//logs rapidos, de quita y pon
	private void log(String s){
		Log.i("",s);
	}
    
    static void logMatrix(Matrix matrix, ImageView imageView){
		float[] values = new float[9];
		Main.contador++;
		matrix.getValues(values);
		Log.i("  ",Main.contador+"-----------veces---------------- ");
		Log.i("valores",""+values[0]+"/"+values[1]+"/"
		+values[2]+"/"+values[3]+"/"+values[4]+"/"
		+values[5]+"/"+values[6]+"/"
		+values[7]+"/"+values[8]);
		
        float width = values[0]* imageView.getWidth();
        float height = values[4] * imageView.getHeight();

        Log.i("globalX[2]",""+values[2]);
        Log.i("globalY[5]",""+values[5]);
        Log.i("width[0]",""+width+"("+values[0]+" x "+imageView.getWidth()+")");
        Log.i("height[4]",""+height+"("+values[4]+" x "+imageView.getHeight()+")");
	}

}

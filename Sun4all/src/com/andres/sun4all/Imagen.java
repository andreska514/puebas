package com.andres.sun4all;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.util.FloatMath;
import android.util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Imagen{
	
//CONSTANTES
	// zoom
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 1.7f;
    static final float MIN_ZOOM = 0.9f;
		
//VARIABLES y OBJETOS
    // zoom
    int mode = NONE;
    float oldDist = 1f;
    
//OBJETOS
    static ImageView imageView;
	// zoom
	static Matrix matrix = new Matrix();
	static Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	int lastTouchX;
	int lastTouchY;
	//hasta aqui funciona
	
	//arrays para el envio de las coordenadas
	//coordenadas añadiendo -- coordenadas de cada toque -- cordenadas a enviar
	//static List<int []> listaCoordenadas ;
	static List<int[]> listaCoordenadas ;
	static int [] par = new int[2];
	static int envia[][];
	
	
	//
	Context c;
	
	
//CONSTRUCTOR --------------------------------------------------------------------------
	Imagen(ImageView imView, Context c)
	{	// 0 4 zoom actual x y de la imagen(tamaño si lo multiplicas por width/height)
		// 2 5 posiciones x y del matrix (muy raro)
		//codigo temporal(de momento coge una imagen ya guardada)
		Log.i("c", "1");
		imView.setImageResource(R.drawable.sol);
		Log.i("c", "2");
		imView.setCropToPadding(true);
		Log.i("c", "3");
		imageView = imView;
		Log.i("c", "4");
		//coordenadas
		listaCoordenadas = new ArrayList<int[]>();
		Log.i("c", "5");
		this.c = c;
	}
	void guardaCoordenadas(int x, int y){
		par[0]=x;
		par[1]=y;
		listaCoordenadas.add(par);
		//probando, las imprime en la app
		Main.txtCont.setText("X :"+x+" , "+"Y :"+y);
	}
	static void enviaCoordenadas(){
		//preparamos un array a medida para enviar las coordenadas
		int [] algo;// = new int[2];
		envia = new int[listaCoordenadas.size()][2];
		Log.i("imprimiendo coordenadas", "total de coordenadas: "+listaCoordenadas.size());
		Log.i("","-------------------------------------");
		for(int x = 0; x<listaCoordenadas.size();x++){
			algo = listaCoordenadas.get(x);
			envia[x][0]=algo[0];
			envia[x][1]=algo[1];
			algo=null;
			
			Log.i("coordenada "+(x+1)+" de "+envia.length,"X :"+envia[x][0]+" , "+"Y :"+envia[x][1]);
		}
		//enviamos el array --> (envia)
		
		//reseteamos el arraylist		
		listaCoordenadas.clear();
	}
	//modo add sunspot activado
	public boolean pinta (View v, MotionEvent event){
		switch(event.getAction()){
        // When user touches the screen
        case MotionEvent.ACTION_DOWN:
        	calculaCoordenadasImagen(event);
        	guardaCoordenadas(lastTouchX,lastTouchY);
            //matrix.
          //hasta aqui funciona
		}
		
		return true;
	}
	//modo move image activado
    public boolean touch(View v, MotionEvent event)
    {
    	imageView =(ImageView) v;
		imageView.setScaleType(ScaleType.MATRIX);
		
		//codigo probando
		
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
    //pulsar 1	    
		    case MotionEvent.ACTION_DOWN:
		        savedMatrix.set(matrix);
		        start.set(event.getX(), event.getY());
		        //Log.d("accion", "mode=PULSADO");
		        mode = PULSADO;
		        imageView.setImageMatrix(matrix);
		        //compruebaValores();
		        break;
    //pulsar 2
		    case MotionEvent.ACTION_POINTER_DOWN:
		        oldDist = espacio(event);
		        //Log.d("accion", "oldDist=" + oldDist);
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
		        //Log.d("accion", "mode=NONE");
		        imageView.setImageMatrix(matrix);
		        break;
	//mover dedos
		    case MotionEvent.ACTION_MOVE:
		        if (mode == PULSADO) {
		        	//log("ACTION_MOVE -> pulsado");
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x, 
		            		event.getY() - start.y);
		            compruebaZoom();
		        } 
		        else if (mode == ZOOM) {
		        	//log("ACTION_MOVE -> zoom");
		            float newDist = espacio(event);
	                matrix.set(savedMatrix);
	                float scale = newDist / oldDist;
	                matrix.postScale(scale, scale, mid.x, mid.y);  
	                compruebaZoom();
		        }
		        imageView.setImageMatrix(matrix);
		        compruebaZoom();
		        break;
	    }//fin switch
	    return true;
    }//fin touch
    
    //comprueba el zoom para concretar los límites
    //limita el max y min zoom y ejecuta compruebaValores()
    public void compruebaZoom(){
    	float[] values = new float[9];
        matrix.getValues(values);
        
        //compruebo el zoom
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        if(scaleX > MAX_ZOOM) {
    	scaleX = MAX_ZOOM;
        } else if(scaleX < MIN_ZOOM) {
    	scaleX = MIN_ZOOM;
        }

        if(scaleY > MAX_ZOOM) {
    	scaleY = MAX_ZOOM;
        } else if(scaleY < MIN_ZOOM) {
    	scaleY = MIN_ZOOM;
        }

        values[Matrix.MSCALE_X] = scaleX;
        values[Matrix.MSCALE_Y] = scaleY; 
        matrix.setValues(values);
        compruebaValores();
        //limitDrag(matrix);
    }
    
    //comprueba el zoom actual y envia los bordes de la pantalla a limitaBordes()
	public void compruebaValores(){
    	//log("compruebaValores");
    	valores = new float[9];
    	matrix.getValues(valores);
    	
        /*
         * if (valores[0]<=0.9f){
        	valores[0]=0.9f;
        	valores[4]=0.9f;
        	valores[2]=0;
        	valores[5]=0;
         */
    	if (valores[0]<=MIN_ZOOM){
        	valores[0]=MIN_ZOOM;
        	valores[4]=MIN_ZOOM;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	imageView.setImageMatrix(matrix);
        }else if(valores[0]>0.9 && valores[0]<=1){
        	limitaBordes(-65, -40);
        }else if(valores[0]>1 && valores[0]<=1.1){
        	limitaBordes(-166, -123);
        }else if(valores[0]>1.1 && valores[0]<=1.2){
        	limitaBordes(-246, -201);
        }else if(valores[0]>1.2 && valores[0]<=1.3){
        	limitaBordes(-316, -280);
        }else if(valores[0]>1.3 && valores[0]<=1.4){
        	limitaBordes(-409, -359);
        }else if(valores[0]>1.4 && valores[0]<=1.5){
        	limitaBordes(-484, -435);
        }else if(valores[0]>1.5 && valores[0]<=1.6){
        	limitaBordes(-563, -521);
        }else if(valores[0]>1.6 && valores[0]<=1.7){
        	limitaBordes(-644, -600);
        }else {// (valores[0]>1.7f){
        	//valores[0]=1.7f;
        	//valores[4]=1.7f;
        	limitaBordes(-664, -600);
        }
    }
    //establece los limites del matrix segun lo recibido de compruebaValores()
	void limitaBordes(float valorX, float valorY){
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
	//calcula las coordenadas de la pantalla
	void calculaCoordPantalla(MotionEvent e){
		// Getting X coordinate
        float mX = e.getX();
        // Getting Y Coordinate
        float mY = e.getY();
        
        Main.txtCont.setText("X :" + mX + " , " + "Y :" + mY);
	}
	//calcula las coordenadas absolutas de la imagen
	void calculaCoordenadasImagen(MotionEvent e){
		float []m = new float[9];
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X] * -1;
		float transY = m[Matrix.MTRANS_Y] * -1;
		float scaleX = m[Matrix.MSCALE_X];
		float scaleY = m[Matrix.MSCALE_Y];
		lastTouchX = (int) ((e.getX() + transX) / scaleX);
		lastTouchY = (int) ((e.getY() + transY) / scaleY);
		lastTouchX = Math.abs(lastTouchX);
		lastTouchY = Math.abs(lastTouchY);
		
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
//no funciona
/*private void limitDrag(Matrix m) {
String TAG = "touch";
float[] values = new float[9];
m.getValues(values);
float transX = values[Matrix.MTRANS_X];
float transY = values[Matrix.MTRANS_Y];
float scaleX = values[Matrix.MSCALE_X];
float scaleY = values[Matrix.MSCALE_Y];
//ImageView iv = (ImageView)findViewById(R.id.image);
Rect bounds = imageView.getDrawable().getBounds();
Main main= null;
int viewWidth = c.getResources().getDisplayMetrics().widthPixels;
int viewHeight = c.getResources().getDisplayMetrics().widthPixels;
Log.i("vw-vh",viewWidth+"-"+viewHeight);

int width = bounds.right - bounds.left;
int height = bounds.bottom - bounds.top;
int offsetX = 20;
int offsetY = 80;
float minX = (-width + 20) * scaleX;
float minY = (-height + 20) * scaleY;
float maxX = minX+viewWidth+offsetX;
float maxY = minY+viewHeight-offsetY;
Log.d(TAG, "minX:"+minX);
Log.d(TAG, "maxX:"+maxX);
Log.d(TAG, "minY:"+minY);
Log.d(TAG, "maxY:"+maxY);
if(transX > (maxX)) {
	//transX = viewWidth - 20;
	Log.d(TAG, "transX >");
	transX = maxX;
} else if(transX < minX) {
	Log.d(TAG, "transX <");
	transX = minX;
}
if(transY > (maxY)) {
	// transY = viewHeight - 80;
	Log.d(TAG, "transY >");
	transY = maxY;
} else if(transY < minY) {
	transY = minY;
	Log.d(TAG, "transY <");
}
values[Matrix.MTRANS_X] = transX;
values[Matrix.MTRANS_Y] = transY;
m.setValues(values);
}*/

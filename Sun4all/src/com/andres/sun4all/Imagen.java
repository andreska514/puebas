package com.andres.sun4all;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;

public class Imagen extends ImageView {
	
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 1.7f;
    static final float MIN_ZOOM = 0.9f;
    int mode = NONE;
    float oldDist = 1f;
    
	static Matrix matrix = new Matrix();
	static Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	
	int lastTouchX;
	int lastTouchY;
	int viewWidth = this.getWidth();
	int viewHeight = this.getHeight();
	
	static boolean pinta = false;
	static boolean borra = false;
	/** absolute and relative coordinates*/
	static ArrayList <Marking> listaPtos = new ArrayList<Marking>(); 
	static ArrayList <Mark> listaMarcas = new ArrayList<Mark>();
	
	MotionEvent lastEvent;
	Context context;
	Bitmap inicial = BitmapFactory.decodeResource(getResources(), R.drawable.sol);
	Bitmap bitmap = inicial;
	Bitmap negativo;
	Bitmap cruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz);
	Paint paintFondo,paintPuntos;
	Canvas canvas;
	Path path;
	
	ProgressDialog pDialog;
	
	public Imagen(Context c, AttributeSet attr) {
		super(c, attr);
		this.setFocusable(true);
		context=c;
		setWillNotDraw (false);
		setImageResource(R.drawable.sol);
		setCropToPadding(true);
		path=new Path();
		paintFondo = new Paint(Paint.DITHER_FLAG);
		setOnTouchListener(clickImagen);
	}
	public Imagen(Context c) {
		super(c);
		this.setFocusable(true);
		context=c;
		setWillNotDraw (false);
		setImageResource(R.drawable.sol);
		setCropToPadding(true);
		path=new Path();
		paintFondo = new Paint(Paint.DITHER_FLAG);
		setOnTouchListener(clickImagen);
	}
	@Override
	public void onDraw(Canvas c){
		setWillNotDraw (false);
		String s = getResources().getString(R.string.sunspot);
		c.drawBitmap(bitmap, matrix, paintFondo);
		Main.txtCont.setText(s+listaMarcas.size());
		if(pinta){
			this.setOnTouchListener(clickPinta);
		}
		else{
			this.setOnTouchListener(clickImagen);
		}
		if(listaPtos!=null){
			for(Mark mark:listaMarcas){	
				c.drawBitmap(cruz, mark.x, mark.y, new Paint());
			}
		}
		((Main) context).runOnUiThread(new Runnable() {
		       @Override
		       public void run() {
		           Imagen.this.invalidate();
		       }
		 });
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	}
	/** Save absolute coordinates in listaPtos*/
	void guardaCoordenadas(int x, int y){
		Log.i("guardaCoordenadas","de int");
		Marking m = new Marking(x,y);
		listaPtos.add(m);
		Log.i("listaPtos0",""+m.x+"-"+m.y);
		//Main.txtCont.setText(x+"-"+y);
	}
	/** Save relative coordinates in listaMarcas*/
	void saveCoordinates(float x, float y){
		Log.i("guardaCoordenadas","de float");
		Mark m = new Mark(x,y);
		listaMarcas.add(m);
		Log.i("marca",""+m.x+"-"+m.y);
		Main.txtCont.setText(x+"-"+y);
		guardaCoordenadas(lastTouchX,lastTouchY);
	}
	/** Delete coordinates near touch event(less than 50px)*/
	void borraCoordenadas(View v, MotionEvent event){
		for (int i=listaPtos.size()-1; i>=0; i--){
			if(Math.sqrt(
			Math.pow((event.getX()-listaMarcas.get(i).x), 2)
			+Math.pow((event.getY()-listaMarcas.get(i).y), 2)) < 50){
				listaMarcas.remove(i);
				listaPtos.remove(i);
			}
		}
	}
	
	View.OnTouchListener clickImagen = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			lastEvent = event;
			setScaleType(ScaleType.MATRIX);
		    switch (event.getAction() & MotionEvent.ACTION_MASK) {
			    case MotionEvent.ACTION_DOWN:
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        mode = PULSADO;
			        break;
			    case MotionEvent.ACTION_POINTER_DOWN:
			        oldDist = espacio(event);
			        if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            puntoMedio(mid, event);
			            mode = ZOOM;
			        }
			        break;
			    case MotionEvent.ACTION_UP:
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        break;
			    case MotionEvent.ACTION_MOVE:
			        if (mode == PULSADO) {
			            matrix.set(savedMatrix);
			            float matX = event.getX()-start.x;
			            float matY = event.getY()-start.y;
			            matrix.postTranslate(matX, matY);
			        } 
			        else if (mode == ZOOM) {
			            float newDist = espacio(event);
		                matrix.set(savedMatrix);
		                float scale = newDist / oldDist;
		                matrix.postScale(scale, scale, mid.x, mid.y);  
			        }
			        checkZoom();
			        break;
		    }//end switch
		    mueveCoordenadas(event);
		    setImageMatrix(matrix);
		    invalidate();
		    return true;
		}//end OnTouch
	};//end touchListener
	
	View.OnTouchListener clickPinta = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_UP){
				calculaCoordenadasImagen(event);
				if(!borra){
					saveCoordinates(event.getX(),event.getY());
					Main.txtCont.setText("getX"+event.getX() +" getX"+event.getY());
				}
				else{
					borraCoordenadas(v, event);
				}
			}
			path = new Path();
			invalidate();
			return true;
		}//end onTouch
	};//end onTouchListener
	void setZoom(float zoom){
    	float[] values = new float[9];
        matrix.getValues(values);
    	values[Matrix.MSCALE_X] = zoom;
        values[Matrix.MSCALE_Y] = zoom; 
        matrix.setValues(values);
    }
   	/** Chech the zoom for narrow limits
	 * limit zoom and send the edges of the screen to limitCorners ()*/
    public void checkZoom(){
    	float[] values = new float[9];
        matrix.getValues(values);
        float scaleX = values[Matrix.MSCALE_X];
        if(scaleX > MAX_ZOOM) {
        	setZoom(MAX_ZOOM);
        } 
        else if(scaleX < MIN_ZOOM) {
        	setZoom(MIN_ZOOM);
        }

        valores = new float[9];
    	matrix.getValues(valores);
    	if (valores[0]<=MIN_ZOOM){
        	valores[0]=MIN_ZOOM;
        	valores[4]=MIN_ZOOM;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        }else if(valores[0]>0.9 && valores[0]<=1){
        	limitCorners(-65, -40);
        }else if(valores[0]>1 && valores[0]<=1.1){
        	limitCorners(-166, -123);
        }else if(valores[0]>1.1 && valores[0]<=1.2){
        	limitCorners(-246, -201);
        }else if(valores[0]>1.2 && valores[0]<=1.3){
        	limitCorners(-316, -280);
        }else if(valores[0]>1.3 && valores[0]<=1.4){
        	limitCorners(-409, -359);
        }else if(valores[0]>1.4 && valores[0]<=1.5){
        	limitCorners(-484, -435);
        }else if(valores[0]>1.5 && valores[0]<=1.6){
        	limitCorners(-563, -521);
        }else {
        	limitCorners(-664, -600);
        }
    }
   //metodo a mejorar
	void limitCorners(float valorX, float valorY){
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
    	setImageMatrix(matrix);
    	
    }
	/** Determine the space between the 2 fingers*/
	@SuppressLint("FloatMath")
	private float espacio(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** calculates the midpoint between the 2 fingers*/
	private void puntoMedio(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
	/** Calculate the absolute coordinates of the image*/
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
	/** Move the relative coordinates when the image was moved*/
	void mueveCoordenadas(MotionEvent event){
		for (int i=0; i<listaPtos.size();i++){
			Marking pto = listaPtos.get(i);
			Mark mark = listaMarcas.get(i);
			float[]coor = new float[2];
			coor[0]=pto.x;
			coor[1]=pto.y;
			matrix.mapPoints(coor);
			mark.setX(coor[0]);
			mark.setY(coor[1]);
			listaMarcas.set(i, mark);
		}
	}

	void changeBitmap(Bitmap b){
		bitmap = b;
	}
	void changeBitmap(String[] s){
		//new LoadImage().execute(s);
		new LoadImage().execute("https://pybossa.socientize.eu/sun4all/sunimages/k1v_01_08_03_09h_30_E_C.jpg");
	}
	
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading Image ....");
			pDialog.show();
		}
		protected Bitmap doInBackground(String... args) {
			//1 url
			if(args.length == 1){
				Log.i("doInBack","length = 1 ");
				try {
					bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//2 url
			else{
				Log.i("doInBack","length = 2 ");
				try {
					bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
					negativo = BitmapFactory.decodeStream((InputStream)new URL(args[1]).getContent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return bitmap;
		}
		protected void onPostExecute(Bitmap image) {
			if(image != null){
				bitmap=image;
				pDialog.dismiss();
			}else{
				pDialog.dismiss();
				Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
			}
		}
	}
	//clase que guarda un objeto con coordenadas
	class Marking{
		int x;
		int y;
		Marking(int x, int y){
			this.x = x;
			this.y = y;
		}
		Marking(){}
		int getX(){
			return x;
		}
		int getY(){
			return y;
		}
		void setX(int x){
			this.x = x;
		}
		void setY(int y){
			this.y = y;
		}
	}
	class Mark{
		float x;
		float y;
		Mark(float x, float y){
			this.x = x;
			this.y = y;
		}
		Mark(){}
		float getX(){
			return x;
		}
		float getY(){
			return y;
		}
		void setX(float x){
			this.x = x;
		}
		void setY(float y){
			this.y = y;
		}
	}
}

	
//PROBAR ESTO!!!
//https://github.com/MikeOrtiz/TouchImageView/blob/master/src/com/ortiz/touch/TouchImageView.java
//**********************************************************************
/*
private void fixTrans() {
	viewWidth = this.getWidth();
	viewHeight = this.getHeight();
	float []m = new float[9];
	matrix.getValues(m);
	float transX = m[Matrix.MTRANS_X];
	float transY = m[Matrix.MTRANS_Y];
	float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
	float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());
	if (fixTransX != 0 || fixTransY != 0) {
		matrix.postTranslate(fixTransX, fixTransY);
	}
}

private void fixScaleTrans() {
	float []m = new float[9];
	matrix.getValues(m);
	fixTrans();
	matrix.getValues(m);
	if (getImageWidth() < viewWidth) {
		m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
	}
	if (getImageHeight() < viewHeight) {
		m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
	}
	matrix.setValues(m);
}
private float getFixTrans(float trans, float viewSize, float contentSize) {
	float minTrans, maxTrans;
	if (contentSize <= viewSize) {
		minTrans = 0;
		maxTrans = viewSize - contentSize;
	} else {
		minTrans = viewSize - contentSize;
		maxTrans = 0;
	}
	if (trans < minTrans)
		return -trans + minTrans;
	if (trans > maxTrans)
		return -trans + maxTrans;
	return 0;
}
private float getFixDragTrans(float delta, float viewSize, float contentSize) {
	if (contentSize <= viewSize) {
		return 0;
	}
	return delta;
}
*/

//Methods used but not necesary now
/*
 * 
 *  int getBitmapWidth(Bitmap b){
    	return b.getWidth();
    }
    int getBitmapHeight(Bitmap b){
    	return b.getHeight();
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
	
	*/
/*
 * void calculaCoordPantalla(MotionEvent e){
        float[] values = new float[9];
        matrix.getValues(values);
        
        float scaleX=values[0];
        float scaleY=values[4];
        
        float relativeX = ((e.getX() - values[2])*scaleX) / values[0];
        float relativeY = ((e.getY() - values[5])*scaleY) / values[4];
	}
 */


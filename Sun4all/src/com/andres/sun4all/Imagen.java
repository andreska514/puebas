package com.andres.sun4all;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
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
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;

public class Imagen extends ImageView {
	
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 2f;
    static float MIN_ZOOM = 0.9f;
    int mode = NONE;
    float oldDist = 1f;
    
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	
	//abs
	int lastTouchX;
	int lastTouchY;
	
	int viewWidth, viewHeight;//drag
	
	boolean inverted = false;
	boolean pinta = false;
	boolean borra = false;
	/** array of absolute and relative coordinates*/
	ArrayList <Marking> listaPtos = new ArrayList<Marking>(); 
	ArrayList <Mark> listaMarcas = new ArrayList<Mark>();
	
	MotionEvent lastEvent;
	Context context;
	Bitmap inicial;
	Bitmap bitmap;
	Bitmap positivo;
	Bitmap negativo;
	Bitmap cruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz);
	
	ProgressDialog pDialog;
	JSONObject finalJson;
	
	void setMinZoom(){
		float imageW = getImageWidth();
		float imageH = getImageHeight();
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();
		float initialZool = imageW/viewWidth;
		
	}
	public void init(){
		inicial = BitmapFactory.decodeResource(getResources(), R.drawable.sol);
		bitmap= inicial;
		Log.d("entrada","1");
		setOnTouchListener(clickImagen);
	}
	/** Constructor 1*/
	public Imagen(Context c, AttributeSet attr) {
		super(c, attr);
		context=c;
		init();
	}
	/** Constructor 2*/
	public Imagen(Context c) {
		super(c);
		context=c;
		init();
	}
	/** Draw the imageView*/
	@Override
	public void onDraw(Canvas c){
		String s = getResources().getString(R.string.sunspot);
		limitCorners();
		c.drawBitmap(bitmap, matrix, new Paint());
		Main.txtCont.setText(s+listaMarcas.size());
		if(pinta){
			this.setOnTouchListener(clickPinta);
		}
		else{
			this.setOnTouchListener(clickImagen);
		}
		if(listaPtos!=null){
			for(Mark mark:listaMarcas){	
				float laX = mark.x-(cruz.getWidth()/2);
				float laY = mark.y-(cruz.getHeight()/2);
				c.drawBitmap(cruz, laX, laY, new Paint());
				//c.drawBitmap(cruz, mark.x, mark.y, new Paint());
			}
		}
	}
	/** Save absolute coordinates in listaPtos*/
	void guardaCoordenadas(int x, int y){
		Marking m = new Marking(x,y);
		listaPtos.add(m);
	}
	/** Save relative coordinates in listaMarcas*/
	void saveCoordinates(float x, float y){
		Mark m = new Mark(x,y);
		listaMarcas.add(m);
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
	/** Enable move mode when touch the imageView*/
	View.OnTouchListener clickImagen = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			lastEvent = event;
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
			        limitCorners();
			        break;
		    }//end switch
		    mueveCoordenadas(event);
		    invalidate();
		    return true;
		}//end OnTouch
	};//end touchListener
	/** Enable paint mode when touch the imageview*/
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
			invalidate();
			return true;
		}//end onTouch
	};//end onTouchListener
	/** Set the new zoom of matrix*/
	void setZoom(float zoom){
    	float[] values = new float[9];
        matrix.getValues(values);
    	values[Matrix.MSCALE_X] = zoom;
        values[Matrix.MSCALE_Y] = zoom; 
        matrix.setValues(values);
        limitCorners();
    }
	/**Chech the zoom for narrow limits, it starts limitCorners ()*/
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
    }
    /** Limit the corners, it holds the image inside the imageView*/
    private void limitCorners() {
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
		matrix.getValues(m);
		if (getImageWidth() < viewWidth) {
			m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
		}
		if (getImageHeight() < viewHeight) {
			m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
		}
		matrix.setValues(m);
	}
	/** Used within limitCorners*/
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
	/** Get the width of image (bitmapWidth*Zoom)*/
	float getImageWidth(){
		float []m = new float[9];
		matrix.getValues(m);
		return m[Matrix.MSCALE_X]*bitmap.getWidth();
	}
	/** Get the Height of image (bitmapHeight*Zoom)*/
	float getImageHeight(){
		float []m = new float[9];
		matrix.getValues(m);
		return m[Matrix.MSCALE_X]*bitmap.getHeight();
	}/** return the space between the 2 fingers*/
	@SuppressLint("FloatMath")
	private float espacio(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** return the midpoint between the 2 fingers*/
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
		lastTouchX = Math.abs((int) ((e.getX() + transX) / scaleX));
		lastTouchY = Math.abs((int) ((e.getY() + transY) / scaleY));
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
	/** Change the bitmap(take the source)*/
	void changeBitmap(Bitmap b){
		bitmap = b;
		invalidate();
	}
	/** Execute new LoadImage with the String passed */
	void preparaDescarga(String [] s){
		new LoadImage().execute(s);
		//new LoadImage().execute("https://pybossa.socientize.eu/sun4all/sunimages/k1v_01_08_03_09h_30_E_C.jpg");
	}
	/** Invert the image(black-white)*/
	void invertBitmap(){
		if(inverted){
			bitmap=positivo;
			inverted=false;
		}else{
			bitmap=negativo;
			inverted=true;
		}
		invalidate();
	}
	/** Download de 2 images passed*/
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog((Main)context);
			pDialog.setMessage(getResources().getString(R.string.progressBar));
			//pDialog.setMessage("Loading Image ....");
			pDialog.show();
		}
		protected Bitmap doInBackground(String... args) {
			try {
				positivo = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
				negativo = BitmapFactory.decodeStream((InputStream)new URL(args[1]).getContent());
				setZoom(MIN_ZOOM);
				postInvalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return positivo;
		}
		protected void onPostExecute(Bitmap image) {
			if(image != null){
				bitmap = image;
				
				invalidate();
				
				pDialog.dismiss();
				//Toast.makeText(context, "Image Downloaded correctly", Toast.LENGTH_SHORT).show();
				Toast.makeText(context, getResources().getString(R.string.downloadOK), Toast.LENGTH_SHORT).show();
			}else{
				pDialog.dismiss();
				//Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
				Toast.makeText(context, getResources().getString(R.string.downloadError), Toast.LENGTH_SHORT).show();
			}
		}
	}
	void prepareJson(){
		JSONArray coorJson= new JSONArray();
		for(int x=0; x<listaPtos.size();x++){
			Marking ptos = listaPtos.get(x);
			JSONObject tempJson = new JSONObject();
			try{
				tempJson.put("x", ptos.getX());
				tempJson.put("y", ptos.getY());
			}catch(JSONException jEx){
				jEx.printStackTrace();
			}
			coorJson.put(tempJson);
		}
		
		finalJson = new JSONObject();
		try{
			finalJson.put("description",Main.cadUrl);
			finalJson.put("points", coorJson);
		}catch(JSONException jEx){
			jEx.printStackTrace();
		}
		
	}
	
	/** this clases save coordinates(relatives and absolutes)*/
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


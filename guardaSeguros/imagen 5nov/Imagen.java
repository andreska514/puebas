package com.andres.sun4all;

import java.util.ArrayList;
import java.util.List;

import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
	
	int lastTouchX;//from image
	int lastTouchY;//from image
	int viewWidth = this.getWidth();
	int viewHeight = this.getHeight();
	
	static boolean pinta = false;
	static boolean borra = false;
	//nueva lista de coordenadas con objetos
	static ArrayList <Marking> listaPtos = new ArrayList<Marking>(); 
	static ArrayList <Mark> listaMarcas = new ArrayList<Mark>();
	
	//Canvas canvas= new Canvas();
	Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.sol);
	Bitmap cruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz);
	Paint paintFondo,paintPuntos;
	Canvas canvas;
	Path path;
	
//CONSTRUCTOR --------------------------------------------------------------------------
	public Imagen(Context c, AttributeSet attr) {
		super(c, attr);
		setImageResource(R.drawable.sol);
		setCropToPadding(true);
		path=new Path();
		paintFondo = new Paint(Paint.DITHER_FLAG);
		setOnTouchListener(clickImagen);
		refresh();
	}
	public Imagen(Context c) {
		super(c);
		setImageResource(R.drawable.sol);
		setCropToPadding(true);
		path=new Path();
		paintFondo = new Paint(Paint.DITHER_FLAG);
		setOnTouchListener(clickImagen);
		refresh();
	}
//onDraw ***********************************************************************
	@Override
	public void onDraw(Canvas c){
		Log.d("onDraw","pinta="+pinta);
		c.drawBitmap(bitmap, matrix, paintFondo);
		c.drawPath(path,new Paint());
		if(pinta){
			Log.d("activando","clickPinta");
			//this.setOnTouchListener(null);
			this.setOnTouchListener(clickPinta);
		}
		else{
			Log.d("activando","clickImagen");
			//this.setOnTouchListener(null);
			this.setOnTouchListener(clickImagen);
		}
		
		if(listaPtos!=null){
			Log.i("pintando",listaPtos.size()+" puntos");
			/*for(Marking mark:listaPtos){				
				c.drawBitmap(cruz, mark.x, mark.y, paintPuntos);
				//c.drawCircle(mark.x, mark.y, 20, new Paint());
			}*/
			for(Mark mark:listaMarcas){	
				//c.drawBitmap(cruz, mark.x, mark.y, paintPuntos);
				c.drawBitmap(cruz, mark.x, mark.y, new Paint());
			}
		}
	}//fin ondraw() ******************************************************
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	}
	/** Guarda las coordenadas imagen pasadas en listaPtos*/
	void guardaCoordenadas(int x, int y){
		Log.i("guardaCoordenadas","de int");
		Marking m = new Marking(x,y);
		listaPtos.add(m);
		Log.i("listaPtos0",""+m.x+"-"+m.y);
		//Main.txtCont.setText(x+"-"+y);
	}
	//guarda coordenadas matrix
	void saveCoordinates(float x, float y){
		Log.i("guardaCoordenadas","de float");
		Mark m = new Mark(x,y);
		listaMarcas.add(m);
		Log.i("marca",""+m.x+"-"+m.y);
		Main.txtCont.setText(x+"-"+y);
		guardaCoordenadas(lastTouchX,lastTouchY);
	}
	/** Borra las coordenadas cercanas al evento touch(a menos de 50px)*/
	void borraCoordenadas(View v, MotionEvent event){
		for (int i=listaPtos.size()-1; i>=0; i--){
			/*if (Math.sqrt(
			Math.pow((event.getX()-listaPtos.get(i).x), 2)
			+Math.pow((event.getY()-listaPtos.get(i).y), 2)) < 50){
				listaPtos.remove(i);
			*/
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
			
			//imageView =(ImageView) v;
			setScaleType(ScaleType.MATRIX);
			//codigo probando
			
		    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    //pulsar 1	    
			    case MotionEvent.ACTION_DOWN:
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        mode = PULSADO;
			        break;
	    //pulsar 2
			    case MotionEvent.ACTION_POINTER_DOWN:
			        oldDist = espacio(event);
			        if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            puntoMedio(mid, event);
			            mode = ZOOM;
			        }
			        break;
		//soltar
			    case MotionEvent.ACTION_UP:
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        break;
		//1 dedo-mover / 2 dedos-zoom
			    case MotionEvent.ACTION_MOVE:
			        if (mode == PULSADO) {
			            matrix.set(savedMatrix);
			            float matX = event.getX()-start.x;
			            float matY = event.getY()-start.y;
			            /*matrix.postTranslate(event.getX() - start.x, 
			            		event.getY() - start.y);*/
			            matrix.postTranslate(matX, matY);
			            //mueveCoordenadas(matX,matY);
			            mueveCoordenadas(event);
			        } 
			        else if (mode == ZOOM) {
			            float newDist = espacio(event);
		                matrix.set(savedMatrix);
		                float scale = newDist / oldDist;
		                matrix.postScale(scale, scale, mid.x, mid.y);  
		                //cambiar el zoom de las marcas
		                //mueveCoordenadas();
			        }
			        compruebaZoom();
			        break;
		    }//fin switch
		    setImageMatrix(matrix);
		    invalidate();
		    return true;
		}//fin OnTouch
	};//fin touchListener
	
	View.OnTouchListener clickPinta = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_UP){
				Log.i("ClickPint","ACTION_UP");
				calculaCoordenadasImagen(event);
				if(!borra){
		        	//guardaCoordenadas(lastTouchX,lastTouchY);
					saveCoordinates(event.getX(),event.getY());
					Main.txtCont.setText("getX"+event.getX() +" getX"+event.getY());
				}
				else{
					borraCoordenadas(v, event);
				}
			}
			invalidate();
			return true;
		}//fin onTouch
	};//fin onTouchListener
   
	void setZoom(float zoom){
    	float[] values = new float[9];
        matrix.getValues(values);
    	values[Matrix.MSCALE_X] = zoom;
        values[Matrix.MSCALE_Y] = zoom; 
        matrix.setValues(values);
    }
    //comprueba el zoom para concretar los límites
    //limita zoom y envia los bordes de la pantalla a limitCorners()
    public void compruebaZoom(){
    	float[] values = new float[9];
        matrix.getValues(values);
        //compruebo el zoom
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        if(scaleX > MAX_ZOOM) {
        	setZoom(MAX_ZOOM);
        } 
        else if(scaleX < MIN_ZOOM) {
        	setZoom(MIN_ZOOM);
        }
      //Segunda parte: segun el zoom envia unos valores u otros
		//a limitaBordes(float, float)
        valores = new float[9];
    	matrix.getValues(valores);
    	if (valores[0]<=MIN_ZOOM){
        	valores[0]=MIN_ZOOM;
        	valores[4]=MIN_ZOOM;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	//imageView.setImageMatrix(matrix);
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
        }else {// (valores[0]>1.6f)
        	limitCorners(-664, -600);
        }
    }
   //metodo a mejorar
	void limitCorners(float valorX, float valorY){
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
    	setImageMatrix(matrix);
    	
    }
	/** Determina el espacio entre los 2 primeros dedos*/
	@SuppressLint("FloatMath")
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
	/** Calcula las coordenadas de la pantalla*/
	void calculaCoordPantalla(MotionEvent e){
        float[] values = new float[9];
        matrix.getValues(values);
        
        float scaleX=values[0];
        float scaleY=values[4];
        
        float relativeX = ((e.getX() - values[2])*scaleX) / values[0];
        float relativeY = ((e.getY() - values[5])*scaleY) / values[4];
	}
	/** Calcula las coordenadas absolutas de la imagen*/
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
	/**Cambiara las coordenadas cuando el matrix cambie*/
	void mueveCoordenadas(MotionEvent event){
		if (mode==PULSADO){
			//probando
			float[] values = new float[9];
			matrix.getValues(values);
			
			float scaleX=values[0];
			float scaleY=values[4];
			
			float relativeX = ((event.getX() - values[2])*scaleX) / values[0];
			float relativeY = ((event.getY() - values[5])*scaleY) / values[4];
			//fin prueba
			Log.i("listaMarcas","listaMarcas");
			for (int i=0; i<listaMarcas.size();i++){
				//cambia la x-y segun el matrix
				Mark mark = listaMarcas.get(i);
				float[]coor = new float[2];
				coor[0]=mark.x;
				coor[1]=mark.y;
				matrix.mapPoints(coor);
				mark.setX(coor[0]);
				mark.setY(coor[1]);
				listaMarcas.set(i, mark);
			}
		}
		if (mode==ZOOM){
			
		}
		
	}
	
	void log(String s){
		Log.i("log",s);
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
    int getBitmapWidth(Bitmap b){
    	return b.getWidth();
    }
    int getBitmapHeight(Bitmap b){
    	return b.getHeight();
    }
    public void refresh(){

        new Thread(new Runnable(){
            @Override
                public void run() {
                invalidate();
            }
        }).start();
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

 

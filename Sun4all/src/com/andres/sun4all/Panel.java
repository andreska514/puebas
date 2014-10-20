package com.andres.sun4all;
//pequeña prueba con activity_segunda
import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;


public class Panel extends View{
	
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 1.7f;
    static final float MIN_ZOOM = 0.9f;
    int mode = NONE;
    
    float oldDist = 1f;
    static ImageView imageView;
	static Matrix matrix = new Matrix();
	static Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	int lastTouchX;
	int lastTouchY;
	
	boolean clickZoom;
	
	//probando
	private Bitmap bitmap;
	private Bitmap bitmap2;
	private Canvas canvas;
	private Path path;
	private Paint paint;
	private Context context;

	
	
	//Bitmap bitmap = new BitmapFactory().decodeResource(getResources(), R.drawable.sol);;
	
	//Constructor
	public Panel(Context context) {
		super(context);
		//imageView.setOnTouchListener(clickImagen);
		//this.setOnTouchListener(clickImagen);
		String s = "http://i.imgur.com/CQzlM.jpg";
		bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.canguro);
		Descargar d = new Descargar();
		bitmap = d.getBitmap();
		//probando
		/*path= new Path();
		paint = new Paint(Paint.DITHER_FLAG);*/
		
		
	}
//onDraw
	@Override
	public void onDraw(Canvas canvas){
		//super.draw(canvas);
		//canvas.save();
		if(bitmap!=null){
			Log.i("hola","hola hola");
			canvas.drawBitmap(bitmap, matrix, new Paint());
			//canvas.drawPath(path, paint);
			this.canvas = canvas;
			super.onDraw(canvas);
		}
		else{
			Log.i("","El bitmap es null");
			canvas.drawBitmap(bitmap2, matrix, new Paint());
		}
	}//fin onDraw
	
	View.OnTouchListener clickImagen= new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			clickZoom=true;
	    	//imageView =(ImageView) v;
			//imageView.setScaleType(ScaleType.MATRIX);
			//codigo probando
			Log.i("preparado?","voy a petar");
			//canvas.drawBitmap(bitmap, matrix, new Paint());
			//canvas.getMatrix();
			canvas.concat(matrix);
			Log.i("?","ya he petado?");
			
		    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    //pulsar 1	    
			    case MotionEvent.ACTION_DOWN:
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        //Log.d("accion", "mode=PULSADO");
			        mode = PULSADO;
			        //imageView.setImageMatrix(matrix);
			        //canvas.drawBitmap(bitmap, matrix, new Paint());
			        canvas.concat(matrix);
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
			        //imageView.setImageMatrix(matrix);
			        //canvas.drawBitmap(bitmap, matrix, new Paint());
			        canvas.concat(matrix);
			        break;
		//soltar
			    case MotionEvent.ACTION_UP:
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        //Log.d("accion", "mode=NONE");
			        //imageView.setImageMatrix(matrix);
			        //canvas.drawBitmap(bitmap, matrix, new Paint());
			        canvas.concat(matrix);
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
			        canvas.concat(matrix);
			        //canvas.drawBitmap(bitmap, matrix, new Paint());
			        //imageView.setImageMatrix(matrix);
			        compruebaZoom();
			        break;
		    }//fin switch
		    return true;
		}
	};
	
	View.OnTouchListener clickPinta = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			clickZoom=false;
			switch(event.getAction()){
	        // When user touches the screen
	        case MotionEvent.ACTION_DOWN:
	        	calculaCoordenadasImagen(event);
	        	//guardaCoordenadas(lastTouchX,lastTouchY);
	            //matrix.
	          //hasta aqui funciona
			}
			
			return true;
		}
		
	};
	
	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	// * * * * * * * * * * *No tocar a partir de  * * * * * * * * * * * * *
	// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	
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
        //compruebaValores();
        //limitDrag(matrix);
        valores = new float[9];
    	matrix.getValues(valores);
    	
    	if (valores[0]<=MIN_ZOOM){
        	valores[0]=MIN_ZOOM;
        	valores[4]=MIN_ZOOM;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	//imageView.setImageMatrix(matrix);
        	//canvas.drawBitmap(bitmap, matrix, new Paint());
        	canvas.concat(matrix);
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
        	//imageView.setImageMatrix(matrix);
        	//canvas.drawBitmap(bitmap, matrix, new Paint());
        	canvas.concat(matrix);
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
    	//imageView.setImageMatrix(matrix);
    	//canvas.drawBitmap(bitmap, matrix, new Paint());
    	canvas.concat(matrix);
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
	

}
//************************************************************

//cosas del onDraw, pruebas
/*paint.setColor(Color.RED);
canvas.drawCircle(20, 50, 25, paint);

paint.setAntiAlias(true);
paint.setColor(Color.BLUE);
canvas.drawCircle(60, 50, 25, paint);

paint.setStyle(Paint.Style.FILL_AND_STROKE);
paint.setStrokeWidth(2);
paint.setColor(Color.GREEN);
Path path = new Path();
path.moveTo(4, -10);
path.lineTo(20, 0);
path.lineTo(-9, 0);
path.close();
path.offset(60, 40);
canvas.drawPath(path, paint);
path.offset(90, 100);
canvas.drawPath(path, paint);
path.offset(80, 150);
canvas.drawPath(path, paint);

paint.setStyle(Paint.Style.FILL);
paint.setAntiAlias(true);
paint.setTextSize(20);
canvas.drawText("Hello Android! Fill...", 50, 230,paint);

int x = 75;
int y = 185;
paint.setColor(Color.GRAY);
paint.setTextSize(25);
String rotatedtext = "Rotated helloandroid :)";

Rect rect = new Rect();
paint.getTextBounds(rotatedtext, 0, rotatedtext.length(), rect);
canvas.translate(x, y);
paint.setStyle(Paint.Style.FILL);
 
canvas.drawText("Rotated helloandroid :)", 0, 0, paint);
paint.setStyle(Paint.Style.STROKE);
canvas.drawRect(rect, paint);
 
canvas.translate(-x, -y);
 
                       
paint.setColor(Color.RED);
canvas.rotate(-45, x + rect.exactCenterX(),y + rect.exactCenterY());
paint.setStyle(Paint.Style.FILL);
canvas.drawText(rotatedtext, x, y, paint);

DashPathEffect dashPath = new DashPathEffect(new float[]{10,40}, 1);
paint.setPathEffect(dashPath);
paint.setStrokeWidth(8);
canvas.drawLine(0, 60 , 320, 300, paint);*/

// otros


/*void guardaCoordenadas(int x, int y){
	par[0]=x;
	par[1]=y;
	listaCoordenadas.add(par);
	//probando, las imprime en la app
	Main.txtCont.setText("X :"+x+" , "+"Y :"+y);
}*/
/*static void enviaCoordenadas(){
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
}*/

//logs rapidos, de quita y pon
	/*private void log(String s){
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
	}*/

//how to use external jars 
//http://stackoverflow.com/questions/1334802/how-can-i-use-external-jars-in-an-android-project

package com.andres.sun4all;


import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class Main extends Activity {

	static public int contador = 0;
	
	//variables
	Imagen imagen;
	ImageView img;
	static TextView txtCont;
	Button btnRmv, btnInv, btnFin, btnRes;
	ToggleButton btnAdd;
	
	LinearLayout layout1;
	
	//fuente boton add/move
	String cadena = "Move image/Add Sunspot";
	Editable strMove = Editable.Factory.getInstance().newEditable(cadena);
	Editable strAdd = Editable.Factory.getInstance().newEditable(cadena);
	
	//hasta aqui funciona
	static WindowManager mWinMgr;
	static int width;
	static int height;
	static Drawable d;
	static int viewWidth;
    static int viewHeight;
    
    static boolean check;
	
	//public int viewWidth ;
	//public int viewHeight;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		img = (ImageView) findViewById(R.id.ImgFoto);
		
		//*************
		//img.setOnTouchListener(clickImagen);
		//imagen = new Imagen(img, getApplicationContext());
		imagen = new Imagen(this);
		
		//el contador
		txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		
		//Botones
		btnAdd =(ToggleButton)findViewById(R.id.btnAdd);
		btnRmv =(Button)findViewById(R.id.btnRmv);
		btnFin =(Button)findViewById(R.id.btnFin);
		btnInv =(Button)findViewById(R.id.btnInv);
		btnRes =(Button)findViewById(R.id.btnRes);
		
		btnAdd.setOnClickListener(clickAdd);
		btnRmv.setOnClickListener(clickBoton);
		btnFin.setOnClickListener(clickBoton);
		btnInv.setOnClickListener(clickBoton);
		btnRes.setOnClickListener(clickBoton);
		
		layout1 = (LinearLayout)findViewById(R.id.layout1);
		layout1.setBackgroundColor(Color.WHITE);
		
		//fuente boton add/move
		strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),11,22,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		btnAdd.setText(strMove);
	}
	
	//Clicks en botones
	View.OnClickListener clickAdd = (new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			imagen.borra=false;
			if(btnAdd.isChecked())//add sunspot
			{
				btnAdd.setText(strAdd);
				imagen.pinta = true;
				Log.d("btnAdd.isChecked()",""+Imagen.pinta);
				//img.setOnTouchListener(clickPinta)
			}
			else//move
			{
				btnAdd.setText(strMove);
				imagen.pinta = false;
				Log.d("btnAdd.isChecked()",""+Imagen.pinta);
				//img.setOnTouchListener(clickImagen);
			}
			
		}
	});
	View.OnClickListener clickBoton = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			/*case R.id.btnAdd:
				//comprueba si esta en modo mover o en modo añadir
				
				//activa el modo pintar
				//dibuja un sunspot donde tocas(modo escribir)
				//guardar coordenada de cada punto
				break;*/
			case R.id.btnRmv:
				//desactiva modo pintar
				Imagen.borra=true;
				//borra un sunspot donde tocas
				
				//prueba contador(BORRAR) ----------------------------
				contador++;
				txtCont.setText(String.valueOf(contador));
				//pongo el boton add en modo mover
				
				
				
				break;
			case R.id.btnFin://envia coordenadas de los sunspot y cambia de imagen
				//borrar
				Intent i = new Intent(Main.this, Segunda.class);
				startActivity(i);
				//envia
				//descarga nueva imagen
				//borra imagen anterior del dispositivo
				break;
			case R.id.btnInv://coger el negativo de esa imagen(finish the task)
				break;
			case R.id.btnRes://reinicia la misma imagen sin sunspots(start over)
				//Imagen.logMatrix(Imagen.matrix, Imagen.imageView);
				//Imagen.imprimeCoordenadas();
				Imagen.listaPtos.clear();
				//refresco imagen
				//imagen.invalidate();
				//imagen.onDraw(new Canvas());
				btnAdd.setChecked(false);
				btnAdd.setText(strMove);
				imagen.pinta = false;
				//imagen.setZoom(1);
				//imagen.
				imagen.invalidate();
				//envio cordenadas
				break;
			}
		}
	});
	
}

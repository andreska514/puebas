//how to use external jars 
//http://stackoverflow.com/questions/1334802/how-can-i-use-external-jars-in-an-android-project

package com.andres.sun4all;


import java.util.Locale;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
//de imagen
import java.util.ArrayList;
import java.util.List;

import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

public class Main extends FragmentActivity {

	static public int contador = 0;
	static TextView txtCont;
	//variables
	Imagen imagen;
	ImageView img;
	Button btnInv, btnFin, btnRes;
	ToggleButton btnAdd, btnRmv;

	LinearLayout layout1;
	String cadena;
	Editable strMove;
	Editable strAdd;

	//hasta aqui funciona
	static int width;
	static int height;
	static Drawable d;
	static int viewWidth;
	static int viewHeight;

	static boolean check;
	static boolean finish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		img = (ImageView) findViewById(R.id.ImgFoto);

		imagen = new Imagen(this);

		//el contador
		txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));

		//Botones
		btnAdd =(ToggleButton)findViewById(R.id.btnAdd);
		btnRmv =(ToggleButton)findViewById(R.id.btnRmv);
		btnFin =(Button)findViewById(R.id.btnFin);
		btnInv =(Button)findViewById(R.id.btnInv);
		btnRes =(Button)findViewById(R.id.btnRes);

		btnAdd.setOnClickListener(clickAdd);
		btnRmv.setOnClickListener(clickAdd);
		btnFin.setOnClickListener(clickBoton);
		btnInv.setOnClickListener(clickBoton);
		btnRes.setOnClickListener(clickBoton);

		layout1 = (LinearLayout)findViewById(R.id.layout1);
		layout1.setBackgroundColor(Color.WHITE);
		/** Change the button add/move depending the language*/
		idiomas();
	}
	//Clicks en botones
	View.OnClickListener clickAdd = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnAdd:
				imagen.borra=false;
				if(btnAdd.isChecked())//add sunspot
				{
					btnAdd.setText(strAdd);
					imagen.pinta = true;
					Log.d("btnAdd.isChecked()",""+imagen.pinta);
				}
				else//move
				{
					btnAdd.setText(strMove);
					imagen.pinta = false;
					Log.d("btnAdd.isChecked()",""+imagen.pinta);
				}
				break;//fin case btnAdd

			case R.id.btnRmv:
				if(btnRmv.isChecked())
				{
					btnRmv.setText("OK");
					activaBotones(false);
					imagen.borra=true;
					imagen.pinta = true;
				}
				else
				{
					btnRmv.setText("Remove Sunspot");
					activaBotones(true);
					imagen.borra=false;
					if(btnAdd.isChecked())
						imagen.pinta=true;
					else
						imagen.pinta=false;
				}
				break;
			}
		}
	});
	View.OnClickListener clickBoton = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnFin:
				//muestra mensaje de confirmacion

				Dialogo dialogo = new Dialogo();
				dialogo.show(getSupportFragmentManager(), "tagAlerta");
				//acepta y envia coordenadas y descarga nueva imagen
				if(dialogo.getBoton()){
					//envia coordenadas de los sunspot
					//descarga nueva imagen
					//borra imagen anterior del dispositivo
				}
				else{//cancela el envio, continua con el task actual
					//vacio?
				}
				
				break;
			case R.id.btnInv://coger el negativo de esa imagen(finish the task)
				break;
			case R.id.btnRes://reinicia la misma imagen sin sunspots(start over)
				imagen.listaPtos.clear();//vacio listaPtos
				imagen.listaMarcas.clear();
				cambiaAdd(false);//paso el boton add a move
				//refresco imagen(NO VA)
				//imagen.postInvalidate();
				//imagen.postInvalidate(0, 0, 1000, 1000);
				//imagen.refreshDrawableState();
				//imagen.onDraw(new Canvas());
				//toast("all the sunspots deleted",2000);
				//imagen.onTouchEvent(null);
				//ok();
				imagen.onTouchEvent(null);
				imagen.onDraw(new Canvas());
				
				break;
			}
		}
	});
	void cambiaAdd(boolean b){
		btnAdd.setChecked(b);
		imagen.pinta=b;
		if(b)
			btnAdd.setText(strAdd);
		else
			btnAdd.setText(strMove);
		Log.d("btnAdd.isChecked()",""+imagen.pinta);
	}
	void activaBotones(boolean b){
		btnAdd.setEnabled(b);
		btnInv.setEnabled(b);
		btnFin.setEnabled(b);
		btnRes.setEnabled(b);
	}
	void toast(String s, int ms){
		Toast.makeText(getApplicationContext(), s, ms).show();
	}
	void ok(){
		DialogoOk ok = new DialogoOk();
		ok.show(getSupportFragmentManager(), "tagAlerta");
	}
	/** Set the String and font of btnAdd depending user language*/
	void idiomas(){

		Locale current = getResources().getConfiguration().locale;
		Log.i("getLanguage","-"+current.getLanguage());
		/** español*/
		if (current.getLanguage().equals("es")){
			cadena = "Mover imagen/añadir mancha";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,12,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),13,26,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** italiano*/
		else if(current.getLanguage().equals("it")){
			cadena = "Spostare l'immagine/Aggiungi macchia solare";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),20,43,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** français*/
		else if(current.getLanguage().equals("fr")){
			cadena = "Déplacez l'image/Ajouter taches solaires";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,16,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),17,39,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** english(default)*/
		else{
			cadena = "Move image/Add Sunspot";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),11,22,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		btnAdd.setText(strMove);

	}

}

//Intent i = new Intent(Main.this, Segunda.class);
//startActivity(i);


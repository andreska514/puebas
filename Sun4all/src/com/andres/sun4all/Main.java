//how to use external jars 
//http://stackoverflow.com/questions/1334802/how-can-i-use-external-jars-in-an-android-project

package com.andres.sun4all;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class Main extends FragmentActivity {

	static public int contador = 0;
	static TextView txtCont;
	//variables
	Imagen imagen;
	ImageView img;
	Button btnInv, btnFin, btnRes;
	ToggleButton btnAdd, btnRmv;
	
	LinearLayout layout1;
	
	//fuente boton add/move
	
	//String cadena = "Move image/Add Sunspot";
	String cadena = getResources().getString(R.string.btnAdd);
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
		
		//Textos boton add/move
		
		strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),11,22,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		btnAdd.setText(strMove);
		
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
					Log.d("btnAdd.isChecked()",""+Imagen.pinta);
				}
				else//move
				{
					btnAdd.setText(strMove);
					imagen.pinta = false;
					Log.d("btnAdd.isChecked()",""+Imagen.pinta);
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
				//probando cosas, borrar
				//Intent i = new Intent(Main.this, Segunda.class);
				//startActivity(i);
				break;
			case R.id.btnInv://coger el negativo de esa imagen(finish the task)
				break;
			case R.id.btnRes://reinicia la misma imagen sin sunspots(start over)
				imagen.listaPtos.clear();//vacio listaPtos
				imagen.listaMarcas.clear();
				cambiaAdd(false);//paso el boton add a move
				//refresco imagen(NO VA)
				//imagen.postInvalidate();
				//imagen.refreshDrawableState();
				//imagen.onDraw(new Canvas());
				//toast("all the sunspots deleted",2000);
				//ok();
				imagen.onTouchEvent(null);
				
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
		Log.d("btnAdd.isChecked()",""+Imagen.pinta);
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
	
}

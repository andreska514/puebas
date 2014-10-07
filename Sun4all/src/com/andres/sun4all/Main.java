package com.andres.sun4all;

import java.io.IOException;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class Main extends Activity {

	static public int contador = 0;
	
	//variables
	Imagen imagen;
	ImageView img;
	TextView txtCont;
	Button btnAdd, btnRmv, btnInv, btnFin, btnRes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//imagen
		img = (ImageView) findViewById(R.id.ImgFoto);
		img.setOnTouchListener(clickImagen);
		imagen = new Imagen(img);
		
		//el contador
		txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		
		//Botones
		btnAdd =(Button)findViewById(R.id.btnAdd);
		btnRmv =(Button)findViewById(R.id.btnRmv);
		btnAdd =(Button)findViewById(R.id.btnAdd);
		btnInv =(Button)findViewById(R.id.btnInv);
		btnRes =(Button)findViewById(R.id.btnRes);
		
		btnAdd.setOnClickListener(clickBoton);
		btnRmv.setOnClickListener(clickBoton);
		btnAdd.setOnClickListener(clickBoton);
		btnInv.setOnClickListener(clickBoton);
		btnRes.setOnClickListener(clickBoton);
	}
	
	//Clicks en botones
	View.OnClickListener clickBoton = (new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnAdd://dibuja un sunspot donde tocas(modo escribir)
				
				//prueba new activity(BORRAR) ------------------------
				//Intent i = new Intent(Main.this, Segunda.class);
				//startActivity(i);
				//----------------------------------------------------
				//0 y 4 zoom
				//2 y 5 posicion x y matrix
				
				//prueba zoom con botones 1/2
				/*float[]valores = new float[9];
				Imagen.matrix.getValues(valores);
				valores[0]+=0.1;
				valores[4]+=0.1;
				Imagen.matrix.setValues(valores);
				imagen.compruebaValores();*/
				break;
			case R.id.btnRmv://borra un sunspot donde tocas(modo borrar)
				
				//prueba contador(BORRAR) ----------------------------
				contador++;
				txtCont.setText(String.valueOf(contador));
				
				//prueba zoom con botones 2/2
				/*valores = new float[9];
				Imagen.matrix.getValues(valores);
				valores[0]-=0.1;
				valores[4]-=0.1;
				Imagen.matrix.setValues(valores);
				imagen.compruebaValores();*/
				//----------------------------------------------------
				break;
			case R.id.btnFin://envia coordenadas de los sunspot y cambia de imagen
				//envia
				//descarga nueva imagen
				//borra imagen anterior del dispositivo
				break;
			case R.id.btnInv://coger el negativo de esa imagen(finish the task)
				break;
			case R.id.btnRes://reinicia la misma imagen sin sunspots(start over)
				Imagen.logMatrix(Imagen.matrix, Imagen.imageView);
				break;
			}
		}
	});
		
	
	//Clicks en imagen
	View.OnTouchListener clickImagen = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return imagen.touch(v, event);
			
		}//fin onTouch
	};//fin ontouchListener
	

	

	
}

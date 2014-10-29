package com.andres.sun4all;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class Dialogo extends DialogFragment {
	private boolean boton=false;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Did you finish this task and want start another?")
		.setTitle("Finish the task")
		.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				Log.i("Confirmacion","Aceptada");
				Main.txtCont.setText("finish ok");
				setBoton(true);
				dialog.cancel();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				Log.i("Confirmacion","Rechazada");
				Main.txtCont.setText("NO finish");
				setBoton(false);
				dialog.cancel();	
				
			}
		});
		return builder.create();
	}
	public boolean getBoton() {
		return boton;
	}
	public void setBoton(boolean boton) {
		this.boton = boton;
	}
}

class DialogoOk extends DialogFragment {
	private boolean boton=false;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Done")
		.setTitle("Accept")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				
				dialog.cancel();
			}
		});
		return builder.create();
	}
	public boolean getBoton() {
		return boton;
	}
	public void setBoton(boolean boton) {
		this.boton = boton;
	}
}
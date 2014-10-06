package com.andres.sun4all;

import android.graphics.Bitmap;

public class ProcessingImage {
	private Bitmap imagen;
	private int idBitmap;

	public ProcessingImage() {
		
		
	}
	public int getIdBitmap() {
		return idBitmap;
	}

	public void setIdBitmap(int idBitmap) {
		this.idBitmap = idBitmap;
	}

	public Bitmap getImagen() {
		return imagen;
	}

	public void setImagen(Bitmap defaultImg) {
		this.imagen = defaultImg;
	}

	

	public Bitmap processingI(Bitmap myBitmap) {
		return myBitmap;
	}

	public Bitmap TintThePicture(int deg, Bitmap defaultBitmap) {

		int w = defaultBitmap.getWidth();
		int h = defaultBitmap.getHeight();

		int[] pix = new int[w * h];
		defaultBitmap.getPixels(pix, 0, w, 0, 0, w, h);

		double angle = (3.14159d * (double) deg) / 180.0d;
		int S = (int) (256.0d * Math.sin(angle));
		int C = (int) (256.0d * Math.cos(angle));

		int r, g, b, index;
		int RY, BY, RYY, GYY, BYY, R, G, B, Y;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				index = y * w + x;
				r = (pix[index] >> 16) & 0xff;
				g = (pix[index] >> 8) & 0xff;
				b = pix[index] & 0xff;
				RY = (70 * r - 59 * g - 11 * b) / 100;
				BY = (-30 * r - 59 * g + 89 * b) / 100;
				Y = (30 * r + 59 * g + 11 * b) / 100;
				RYY = (S * BY + C * RY) / 256;
				BYY = (C * BY - S * RY) / 256;
				GYY = (-51 * RYY - 19 * BYY) / 100;
				R = Y + RYY;
				R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
				G = Y + GYY;
				G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
				B = Y + BYY;
				B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
				pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
			}
		}

		Bitmap bm = Bitmap.createBitmap(w, h, defaultBitmap.getConfig());
		bm.setPixels(pix, 0, w, 0, 0, w, h);

		pix = null;
		return bm;
	}
}
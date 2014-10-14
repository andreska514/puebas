package com.andres.sun4all;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class EnvironmentView extends ImageView {
	Matrix matrix;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;

	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	public static boolean EDIT_RISKYZONE = false;
	static boolean SAVE = false;
	ScaleGestureDetector mScaleDetector;

	Context context;

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		Paint paint = new Paint();

		paint.setColor(Color.RED);
		paint.setStrokeWidth(3);
		paint.setAlpha(80);

		float left = 0;
		float top = 0;
		float right = getWidth();
		float down = getHeight();
		RectF origRect = new RectF(left, top, right, down);

		matrix.mapRect(origRect);
		canvas.drawRect(origRect, paint);



	}

	public EnvironmentView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public EnvironmentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		matrix = new Matrix();
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		//
		// Rescales image on rotation
		//
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1) {
			//Fit to screen.
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);

			// Center the image
			float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
			float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);

			origWidth = viewWidth - 2 * redundantXSpace;
			origHeight = viewHeight - 2 * redundantYSpace;
			setImageMatrix(matrix);
		}
	}
}

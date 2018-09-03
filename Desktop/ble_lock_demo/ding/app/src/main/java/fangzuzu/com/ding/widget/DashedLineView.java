package fangzuzu.com.ding.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import fangzuzu.com.ding.R;


@SuppressLint("DrawAllocation")
public class DashedLineView extends View{

	public DashedLineView(Context context) {
		super(context);
	}
	public DashedLineView(Context context, AttributeSet attrs) { 
		super(context, attrs); 

	} 

	@SuppressLint("DrawAllocation") 
	@Override 
	protected void onDraw(Canvas canvas) { 
		super.onDraw(canvas); 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE); 
		paint.setColor(ContextCompat.getColor(getContext(), R.color.blue));

		Path path = new Path();
		path.moveTo(0, 5); 
		path.lineTo(this.getWidth(), 5);

		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1); 
		paint.setPathEffect(effects); 
		canvas.drawPath(path, paint); 
	} 

}

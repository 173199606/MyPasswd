package com.example.mypasswd.lockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mypasswd.R;

public class MyLockPoint extends View {
	// viewgounp øÌ∂»£¨ ”…MyLockView…Ë÷√
	static int viewWidth = 0;
	int mRadius = 0;
	Paint mPaint_bg = new Paint();
	Paint mPaint_bigCircle = new Paint();
	Paint mPaint_smallCircle = new Paint();
	boolean isChoosed = false;

	public boolean isChoosed() {
		return isChoosed;
	}

	public void setChoosed(boolean isChoosed) {
		this.isChoosed = isChoosed;
	}

	public int getmRadius() {
		return mRadius;
	}

	enum Mode {
		STATUS_NO_FINGER, STATUS_FINGER_ON, STATUS_FINGER_UP_RIGHT, STATUS_FINGER_UP_WRONG;
	}

	private Mode mCurrentStatus = Mode.STATUS_NO_FINGER;

	public Mode getmCurrentStatus() {
		return mCurrentStatus;
	}

	public void setmCurrentStatus(Mode mCurrentStatus) {
		this.mCurrentStatus = mCurrentStatus;
		initPaint();
		invalidate();
	}

	public MyLockPoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRadius = viewWidth / (MyLockView.mCount * 4);
	}

	public MyLockPoint(Context context) {
		super(context);
		mRadius = viewWidth / (MyLockView.mCount * 4);
		initPaint();

	}

	
	private void initPaint() {
		mPaint_bg.setAntiAlias(true);
		mPaint_bg.setColor(Color.BLACK);
		mPaint_bigCircle.setAntiAlias(true);
		mPaint_smallCircle.setAntiAlias(true);
		
		switch (mCurrentStatus) {
		case STATUS_NO_FINGER:
			mPaint_bigCircle.setColor(Color.WHITE);
			mPaint_smallCircle.setColor(Color.WHITE);
			break;
		case STATUS_FINGER_ON:
			mPaint_bigCircle.setColor(getResources().getColor(R.color.big_right_circle));
			mPaint_smallCircle.setColor(getResources().getColor(R.color.small_right_circle));
			break;
		case STATUS_FINGER_UP_WRONG:
			mPaint_bigCircle.setColor(getResources().getColor(R.color.big_wrong_circle));
			mPaint_smallCircle.setColor(getResources().getColor(R.color.small_wrong_circle));
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		
		canvas.drawCircle(mRadius, mRadius, mRadius, mPaint_bg);

		canvas.drawCircle(mRadius, mRadius, mRadius - 5, mPaint_bigCircle);

		canvas.drawCircle(mRadius, mRadius, mRadius / 3, mPaint_smallCircle);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(2 * mRadius, 2 * mRadius);
	}

}

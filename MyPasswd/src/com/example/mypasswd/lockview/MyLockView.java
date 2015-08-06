package com.example.mypasswd.lockview;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mypasswd.R;
import com.example.mypasswd.lockview.MyLockPoint.Mode;
import com.example.mypasswd.lockview.MyLockView.OnGestureLockListener;

public class MyLockView extends LinearLayout {
	Context mContext;
	Paint mPaint;
	Path mPath;
	ArrayList<MyLockPoint> lockPoints = new ArrayList<MyLockPoint>();
	int mTryTime = -1;
	static int mCount;
	// viewgroup�Ŀ��
	int mViewWidth = 0;
	int padding = 0;
	int marginLeft = 0;
	int marginRight = 0;
	// ����һ�ε����Pointλ����Ч�ĵ��ڣ�Ϊtrue������Ϊtrue
	private boolean isValidMove = false;
	private MyLockPoint mLastLockPoint;
	// ��ָ�ƶ�����λ��
	private int mLastPathX;
	private int mLastPathY;
	int expected_answer[] = new int[] { 5, 3, 7, 6 };
	ArrayList<Integer> real_answer = new ArrayList<Integer>();
	private OnGestureLockListener mGestureLockListener;

	public final static int UNLOCKED = 1;
	public final static int UNLOCK_FAILED = 2;

	public MyLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mPath = new Path();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(10);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.LockPointStyle);
		mCount = a.getInt(R.styleable.LockPointStyle_ncount, 3);
		mTryTime = a.getInt(R.styleable.LockPointStyle_trytime, 5);
		a.recycle();
	}

	private void addViews() {
		for (int i = 0; i < mCount * mCount; i++) {
			MyLockPoint myLockPoint = new MyLockPoint(mContext);
			lockPoints.add(myLockPoint);
			addView(myLockPoint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		LayoutParams lp = (LayoutParams) getLayoutParams();
		// viewgroup�ߴ�=��Ļ�ߴ�-����margin
		mViewWidth = Utils.getInstance(mContext).getScreenWidth()
				- lp.leftMargin - lp.rightMargin;

		// padding=ÿ����Ԫ���1/4�����Գ������ߵ�padding��ʣ�µĲ��ָպ�ΪԲ��ֱ������֪��padding==radius��
		padding = (mViewWidth / mCount) / 4;

		// ֪ͨMyLockPoint,��MyLockPoint����Radius
		MyLockPoint.viewWidth = mViewWidth;

		addViews();
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		// ����viewgroup�ĳߴ�
		setMeasuredDimension(mViewWidth, mViewWidth);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View childView = getChildAt(i);
			int cWidth = childView.getMeasuredWidth();
			int cHeight = childView.getMeasuredHeight();
			int cleft = (i % mCount) * (mViewWidth / mCount) + padding;
			int ctop = (i / mCount) * (mViewWidth / mCount) + padding;
			childView.layout(cleft, ctop, cleft + cWidth, ctop + cHeight);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTryTime > 0) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				reset();
				isValidMove = isPointInChild((int) event.getX(),
						(int) event.getY());
				if (mLastLockPoint != null) {
					// ����mLastPathX��mLastPathY����ACTION_DOWNʱ���Ͳ��ửָ������
					mLastPathX = (int) (mLastLockPoint.getX() + mLastLockPoint
							.getmRadius());
					mLastPathY = (int) (mLastLockPoint.getY() + mLastLockPoint
							.getmRadius());
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isValidMove) {
					// �������ʧ�ܣ��͸ı�����ѡ�е��������ɫ��������ɫ����
					if (!isUnLocked()) {
						for (MyLockPoint myLockPoint : lockPoints) {
							if (myLockPoint.isChoosed) {
								myLockPoint
										.setmCurrentStatus(Mode.STATUS_FINGER_UP_WRONG);
							}
							// ��mPaint_smallCircle����ɫһ��
							mPaint.setColor(getResources().getColor(
									R.color.small_wrong_circle));
						}
						mGestureLockListener.onLockEvent(UNLOCK_FAILED,
								--mTryTime >= 0 ? mTryTime : 0);
						Log.i("xxj", "UNLOCK_FAILED");
					} else {
						Log.i("xxj", "UNLOCKED");
						mGestureLockListener.onLockEvent(UNLOCKED, mTryTime);
					}
					if (mLastLockPoint != null) {
						// ����mLastPathX��mLastPathY����ACTION_UPʱ���Ͳ��ửָ������
						mLastPathX = (int) (mLastLockPoint.getX() + mLastLockPoint
								.getmRadius());
						mLastPathY = (int) (mLastLockPoint.getY() + mLastLockPoint
								.getmRadius());
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (isValidMove) {
					// Log.i("xxj","move");
					// ��mPaint_smallCircle����ɫһ��
					mPaint.setColor(getResources().getColor(
							R.color.small_right_circle));
					mPath.moveTo(
							mLastLockPoint.getX() + mLastLockPoint.getmRadius(),
							mLastLockPoint.getY() + mLastLockPoint.getmRadius());
					isPointInChild((int) event.getX(), (int) event.getY());
					mPath.lineTo(
							mLastLockPoint.getX() + mLastLockPoint.getmRadius(),
							mLastLockPoint.getY() + mLastLockPoint.getmRadius());
					mLastPathX = (int) event.getX();
					mLastPathY = (int) event.getY();
				}
				break;

			default:
				break;
			}
		} 
		invalidate();
		return true;
	}

	private void reset() {
		// mChoose.clear();
		real_answer.clear();
		mPath.reset();
		for (MyLockPoint lockPoint : lockPoints) {
			lockPoint.setChoosed(false);
			lockPoint.setmCurrentStatus(Mode.STATUS_NO_FINGER);
		}
	}
	
	private boolean isPointInChild(float x, float y) {
		for (int i = 0; i < mCount * mCount; i++) {
			MyLockPoint myLockPoint = lockPoints.get(i);
			float rx = (myLockPoint.getX() + myLockPoint.getmRadius());
			float ry = (myLockPoint.getY() + myLockPoint.getmRadius());
			double distance = Math.sqrt(Math.abs((rx - x) * (rx - x) + (ry - y)
					* (ry - y)));
			if (distance <= myLockPoint.getmRadius() && !myLockPoint.isChoosed) {
				mLastLockPoint = myLockPoint;
				myLockPoint.setChoosed(true);
				myLockPoint.setmCurrentStatus(Mode.STATUS_FINGER_ON);
				if (!real_answer.contains(Integer.valueOf(i))) {
					real_answer.add(Integer.valueOf(i));
				}
				return true;
			}

		}
		return false;
	}

	private void disableLockView(Canvas canvas) {
		reset();
		mPaint.setColor(Color.GRAY);
		mPaint.setAlpha(150);
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(marginLeft, marginLeft, marginLeft+mViewWidth, marginLeft+mViewWidth, mPaint);
		
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// ����֮�����
		if (mPath != null) {
			canvas.drawPath(mPath, mPaint);
		}

		if (mLastLockPoint != null) {
			canvas.drawLine(
					mLastLockPoint.getX() + mLastLockPoint.getmRadius(),
					mLastLockPoint.getY() + mLastLockPoint.getmRadius(),
					mLastPathX, mLastPathY, mPaint);
		}
		
		if (mTryTime<=0) {
			disableLockView(canvas);
		}

	}

	private boolean isUnLocked() {
		if (expected_answer.length != real_answer.size()) {
			return false;
		}
		for (int i = 0; i < expected_answer.length; i++) {
			if (expected_answer[i] != (int) real_answer.get(i)) {
				return false;
			}
		}
		return true;
	}

	public void setOnGestureLockListener(OnGestureLockListener l) {
		mGestureLockListener = l;
	}

	public interface OnGestureLockListener {
		/**
		 * 
		 * @param lockEvent
		 *            : UNLOCKED, UNLOCK_FAILED
		 * @param times
		 *            : ʣ�����
		 */
		public void onLockEvent(int lockEvent, int times);
	}

}

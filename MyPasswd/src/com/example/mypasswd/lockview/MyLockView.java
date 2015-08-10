package com.example.mypasswd.lockview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mypasswd.MyApplication;
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
	int mLockType;
	// viewgroup的宽度
	int mViewWidth = 0;
	int padding = 0;
	int marginLeft = 0;
	int marginRight = 0;
	// 当第一次点击的Point位于有效的点内，为true，否则为true
	private boolean isValidMove = false;
	private MyLockPoint mLastLockPoint;
	// 手指移动到的位置
	private int mLastPathX;
	private int mLastPathY;
	
	ArrayList<Integer> real_answer = new ArrayList<Integer>();
	private MyHandler myHandler = new MyHandler();
	private OnGestureLockListener mGestureLockListener;
	private OnSetPasswdListener mPasswdListener;
	
	ArrayList<Integer> first_passwd_setted = new ArrayList<Integer>();
	
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
		
		mLockType = a.getInt(R.styleable.LockPointStyle_type, 0);
		if (mLockType == 0) {
			mTryTime = 2;//if set passwd, just need two times
		}

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
		// viewgroup尺寸=屏幕尺寸-左右margin
		mViewWidth = Utils.getInstance(mContext).getScreenWidth()
				- lp.leftMargin - lp.rightMargin;

		// padding=每个单元格的1/4，所以除了两边的padding，剩下的部分刚好为圆的直径（可知：padding==radius）
		padding = (mViewWidth / mCount) / 4;

		// 通知MyLockPoint,让MyLockPoint计算Radius
		MyLockPoint.viewWidth = mViewWidth;

		addViews();
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		// 设置viewgroup的尺寸
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
					// 设置mLastPathX、mLastPathY，当ACTION_DOWN时，就不会画指引线了
					mLastPathX = (int) (mLastLockPoint.getX() + mLastLockPoint
							.getmRadius());
					mLastPathY = (int) (mLastLockPoint.getY() + mLastLockPoint
							.getmRadius());
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isValidMove) {
					if (mLockType == 1) {
						// 如果解锁失败，就改变所有选中点和连线颜色，否则颜色不变
						if (!isUnLocked()) {
							for (MyLockPoint myLockPoint : lockPoints) {
								if (myLockPoint.isChoosed) {
									myLockPoint
											.setmCurrentStatus(Mode.STATUS_FINGER_UP_WRONG);
								}
								// 和mPaint_smallCircle的颜色一样
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
					} else {
						//设置password模式
						Utils.getInstance(mContext).setPasswd(real_answer);
						System.out.println(Utils.getInstance(mContext).getPasswd());
						if (mTryTime-- == 2) {
							first_passwd_setted = Utils.getInstance(mContext).getPasswd();
						} else {
							boolean bSuccess = false;
							if (Utils.getInstance(mContext).getPasswd().equals(first_passwd_setted)) {
								bSuccess = true;
							} else {
								 bSuccess = false;
								 // set the color(red) to show the two password is not the same
								 for (MyLockPoint myLockPoint : lockPoints) {
										if (myLockPoint.isChoosed) {
											myLockPoint
													.setmCurrentStatus(Mode.STATUS_FINGER_UP_WRONG);
										}
										// 和mPaint_smallCircle的颜色一样
										mPaint.setColor(getResources().getColor(
												R.color.small_wrong_circle));
								}
							}
							if (mPasswdListener != null) {
								mPasswdListener.onPasswdEvent(bSuccess);
							}
						}
						if (mTryTime == 0) {
							//should give another chance to set password again
							mTryTime = 2;
						}
						
					}
					
					if (mLastLockPoint != null) {
						// 设置mLastPathX、mLastPathY，当ACTION_UP时，就不会画指引线了
						mLastPathX = (int) (mLastLockPoint.getX() + mLastLockPoint
								.getmRadius());
						mLastPathY = (int) (mLastLockPoint.getY() + mLastLockPoint
								.getmRadius());
					}
				}
				
				
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						myHandler.sendEmptyMessage(0);
					}
				}, 800);
				
				break;
			case MotionEvent.ACTION_MOVE:
				if (isValidMove) {
					// 和mPaint_smallCircle的颜色一样
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
		// 画点之间的线
		if (mPath != null) {
			canvas.drawPath(mPath, mPaint);
		}

		if (mLastLockPoint != null) {
			canvas.drawLine(
					mLastLockPoint.getX() + mLastLockPoint.getmRadius(),
					mLastLockPoint.getY() + mLastLockPoint.getmRadius(),
					mLastPathX, mLastPathY, mPaint);
		}
		
		if (mTryTime<=0 && mLockType != 0) {
			disableLockView(canvas);
		}

	}

	private boolean isUnLocked() {
		ArrayList<Integer> expected_answer = Utils.getInstance(mContext).getPasswd();
		if (expected_answer.equals(real_answer)) {
			return true;
		}
//		if (expected_answer.size() != real_answer.size()) {
//			return false;
//		}
//		for (int i = 0; i < expected_answer.length; i++) {
//			if (expected_answer[i] != (int) real_answer.get(i)) {
//				return false;
//			}
//		}
		return false;
	}
	
	
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			reset();
			invalidate();
		}
		
	}

	public void setOnGestureLockListener(OnGestureLockListener l) {
		mGestureLockListener = l;
	}
	
	public void setOnSetPasswdListener(OnSetPasswdListener l) {
		mPasswdListener = l;
	}

	public interface OnGestureLockListener {
		/**
		 * 
		 * @param lockEvent
		 *            : UNLOCKED, UNLOCK_FAILED
		 * @param times
		 *            : 剩余次数
		 */
		public void onLockEvent(int lockEvent, int times);
	}
	
	public interface OnSetPasswdListener {
		/**
		 * 
		 * @param bSuccess
		 *            : the password set successfully
		 */
		public void onPasswdEvent(boolean bSuccess);
	}

}

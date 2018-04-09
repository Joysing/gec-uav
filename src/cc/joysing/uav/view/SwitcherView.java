package cc.joysing.uav.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SwitcherView extends View {

	private Bitmap backgroundBitmap;
	private Bitmap slideBitmap;

	private boolean switcherState = false;
	private Paint paint;
	/*
	 * 在XML布局文件中使用该控件时，并且该控件使用了style属性时，被调用,
	 * 
	 * */
	public SwitcherView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		String namespace = "http://schemas.android.com/apk/res/cc.joysing.uav";
		//根据控件的属性，在布局文件中进行属性设置
		  /**
	     * Return the value of 'attribute' as a resource identifier.
	     * 
	     * <p>Note that this is different than {@link #getAttributeNameResource}
	     * in that it returns the value contained in this attribute as a
	     * resource identifier (i.e., a value originally of the form
	     * "@package:type/resource"); the other method returns a resource
	     * identifier that identifies the name of the attribute.
	     * 
	     * @param namespace Namespace of attribute to retrieve.
	     * @param attribute The attribute to retrieve.
	     * @param defaultValue What to return if the attribute isn't found.
	     * 
	     * @return Resulting value.
	     */
		//得到开关背景的图片的ID
		int switchbackgroundResourceValue = attrs.getAttributeResourceValue(namespace , "switch_background", -1);
		setBackgroundSwitchResource(switchbackgroundResourceValue);
		
		int slide_buttonResourceValue = attrs.getAttributeResourceValue(namespace, "slide_button", -1);
		setSlideResource(slide_buttonResourceValue);
		
		switcherState = attrs.getAttributeBooleanValue(namespace, "switch_state", false);
		init();
	}

	 /*
	  * 初始化工作
	  * 
	  * */
	private void init() {
		
		paint = new Paint();
	}
      /*
       * 在XML布局文件中使用该控件时，被调用
       * 参数一：Context context  上下文
       * 参数二:AttributeSet  属性集
       * */
	public SwitcherView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		//init();
	}
    
	/*
	 * 在代码中通过new关键字创建该类对象时，才被调用
	 * 
	 * */
	public SwitcherView(Context context) {
		this(context,null);
		//init();
	}

	/*
	 * 设置开关背景图片
	 * 把原材料生产成一张图片对象(BitMap)
	 * */
	public void setBackgroundSwitchResource(int switchBackground) {
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	/*
	 * 
	 * 设置开关滑片
	 * */
	public void setSlideResource(int slideButton) {
		slideBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}
  
	/*
	 * 设置开关状态
	 * 
	 * */
	public void setSwitcherState(boolean switcherState) {
		// TODO Auto-generated method stub
		this.switcherState = switcherState;
	}
	
	//重写测量方法
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
	}
	
	boolean isTouchMode = false; //是否触摸
	float padding=0.0f;
	float currentX = 0.0f;
	//绘制控件
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		padding=(backgroundBitmap.getHeight()-slideBitmap.getHeight())/2.0f;		
		
		//[1] 绘制背景图片
		canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
		
		//[2] 绘制开关的滑片
		
		//当手在操作滑片时，要动态的移动滑片，是否在触摸
		if(isTouchMode){ //true
			float leftx = currentX-slideBitmap.getWidth()/2.0f;
			//进行实时的滑片移动
			if(leftx>backgroundBitmap.getWidth()-slideBitmap.getWidth())
				leftx=backgroundBitmap.getWidth()-slideBitmap.getWidth();
			if(leftx<0)leftx=0;
			canvas.drawBitmap(slideBitmap, leftx, padding, paint);
			
		}else{ //当用户不再执行滑动时，来确定滑片是否还在进行移动
			
			//根据开关本身的状态信息，来确定，滑片所停止的位置
			if(switcherState){//true
				//开
				canvas.drawBitmap(slideBitmap, backgroundBitmap.getWidth()-slideBitmap.getWidth()-padding, padding, paint);  //left  top
			}else{
				//关
				canvas.drawBitmap(slideBitmap, padding,padding, paint);
			}
		}

	}
	
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//来确定currentX
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isTouchMode = true;
				currentX = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				isTouchMode = true;
				currentX = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				isTouchMode = false;
				currentX = event.getX();
				boolean state = currentX > backgroundBitmap.getWidth()/2.0f;
				if(state != switcherState && onSwitchStateListener != null){ //当前后两次状态不同时，则把实时的状态，传递给用户
					onSwitchStateListener.onSwitchStateUpdate(state);
				}
				switcherState = state; //保存开关上一次的状态
			default:
				break;
		}
		//保存及时重新调用onDraw方法
		invalidate();
		return true;
	}

	//OnSwitchStateListener get 方法
    public OnSwitchStateListener getOnSwitchStateListener() {
		return onSwitchStateListener;
	}

    //OnSwitchStateListener set 方法
	public void setOnSwitchStateListener(OnSwitchStateListener onSwitchStateListener) {
		this.onSwitchStateListener = onSwitchStateListener;
	}
	
	private OnSwitchStateListener onSwitchStateListener;
	
	//目的，就是为了让用户可以根据开关的状态来执行任务
	 /**
     * Interface definition for a callback to be invoked when a switch is swtichstate.
     */
    public interface OnSwitchStateListener {
    	
    	/**
         * Called when a switch has been changed.
         *
         * @param swtichstate The switch that was Update.
         */
        void onSwitchStateUpdate(boolean swtichstate);
    	
    }
	
     
}

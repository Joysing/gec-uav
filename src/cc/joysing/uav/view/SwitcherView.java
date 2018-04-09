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
	 * ��XML�����ļ���ʹ�øÿؼ�ʱ�����Ҹÿؼ�ʹ����style����ʱ��������,
	 * 
	 * */
	public SwitcherView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		String namespace = "http://schemas.android.com/apk/res/cc.joysing.uav";
		//���ݿؼ������ԣ��ڲ����ļ��н�����������
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
		//�õ����ر�����ͼƬ��ID
		int switchbackgroundResourceValue = attrs.getAttributeResourceValue(namespace , "switch_background", -1);
		setBackgroundSwitchResource(switchbackgroundResourceValue);
		
		int slide_buttonResourceValue = attrs.getAttributeResourceValue(namespace, "slide_button", -1);
		setSlideResource(slide_buttonResourceValue);
		
		switcherState = attrs.getAttributeBooleanValue(namespace, "switch_state", false);
		init();
	}

	 /*
	  * ��ʼ������
	  * 
	  * */
	private void init() {
		
		paint = new Paint();
	}
      /*
       * ��XML�����ļ���ʹ�øÿؼ�ʱ��������
       * ����һ��Context context  ������
       * ������:AttributeSet  ���Լ�
       * */
	public SwitcherView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		//init();
	}
    
	/*
	 * �ڴ�����ͨ��new�ؼ��ִ����������ʱ���ű�����
	 * 
	 * */
	public SwitcherView(Context context) {
		this(context,null);
		//init();
	}

	/*
	 * ���ÿ��ر���ͼƬ
	 * ��ԭ����������һ��ͼƬ����(BitMap)
	 * */
	public void setBackgroundSwitchResource(int switchBackground) {
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	/*
	 * 
	 * ���ÿ��ػ�Ƭ
	 * */
	public void setSlideResource(int slideButton) {
		slideBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}
  
	/*
	 * ���ÿ���״̬
	 * 
	 * */
	public void setSwitcherState(boolean switcherState) {
		// TODO Auto-generated method stub
		this.switcherState = switcherState;
	}
	
	//��д��������
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
	}
	
	boolean isTouchMode = false; //�Ƿ���
	float padding=0.0f;
	float currentX = 0.0f;
	//���ƿؼ�
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		padding=(backgroundBitmap.getHeight()-slideBitmap.getHeight())/2.0f;		
		
		//[1] ���Ʊ���ͼƬ
		canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
		
		//[2] ���ƿ��صĻ�Ƭ
		
		//�����ڲ�����Ƭʱ��Ҫ��̬���ƶ���Ƭ���Ƿ��ڴ���
		if(isTouchMode){ //true
			float leftx = currentX-slideBitmap.getWidth()/2.0f;
			//����ʵʱ�Ļ�Ƭ�ƶ�
			if(leftx>backgroundBitmap.getWidth()-slideBitmap.getWidth())
				leftx=backgroundBitmap.getWidth()-slideBitmap.getWidth();
			if(leftx<0)leftx=0;
			canvas.drawBitmap(slideBitmap, leftx, padding, paint);
			
		}else{ //���û�����ִ�л���ʱ����ȷ����Ƭ�Ƿ��ڽ����ƶ�
			
			//���ݿ��ر����״̬��Ϣ����ȷ������Ƭ��ֹͣ��λ��
			if(switcherState){//true
				//��
				canvas.drawBitmap(slideBitmap, backgroundBitmap.getWidth()-slideBitmap.getWidth()-padding, padding, paint);  //left  top
			}else{
				//��
				canvas.drawBitmap(slideBitmap, padding,padding, paint);
			}
		}

	}
	
	//�����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//��ȷ��currentX
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
				if(state != switcherState && onSwitchStateListener != null){ //��ǰ������״̬��ͬʱ�����ʵʱ��״̬�����ݸ��û�
					onSwitchStateListener.onSwitchStateUpdate(state);
				}
				switcherState = state; //���濪����һ�ε�״̬
			default:
				break;
		}
		//���漰ʱ���µ���onDraw����
		invalidate();
		return true;
	}

	//OnSwitchStateListener get ����
    public OnSwitchStateListener getOnSwitchStateListener() {
		return onSwitchStateListener;
	}

    //OnSwitchStateListener set ����
	public void setOnSwitchStateListener(OnSwitchStateListener onSwitchStateListener) {
		this.onSwitchStateListener = onSwitchStateListener;
	}
	
	private OnSwitchStateListener onSwitchStateListener;
	
	//Ŀ�ģ�����Ϊ�����û����Ը��ݿ��ص�״̬��ִ������
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

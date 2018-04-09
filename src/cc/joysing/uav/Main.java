package cc.joysing.uav;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import cc.joysing.uav.view.RockerView;
import cc.joysing.uav.view.SwitcherView;
import cc.joysing.uav.view.SwitcherView.OnSwitchStateListener;
import cc.joysing.uav.view.VerticalSeekBar;

public class Main extends Activity {

	private SwitcherView switcherview;//开关按钮
	private RockerView rockerViewLeft;//左侧方向摇杆
	private SeekBar verticalSeekBar;//油门控制条
	private Button btn_stay;//悬停按钮
	private Button btn_connect;//连接按钮
	private Button btn_setting;//设置按钮
	private TextView textview1;
	private boolean isStay;
	private boolean isConnect;
	private Socket socket;
	private boolean isOpen=false;
	private boolean isRunning=false;
	private byte[] getBuffer = new byte[34]; 
	public FlyControl flycontrol=new FlyControl();
	private boolean isDirectionChanging;
	private int directionValue;
	private String goWhere;
	private boolean isDirectionChangedThreadStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initView();
		initEven();
    }
	

	private void initView() {
		
		textview1=(TextView)findViewById(R.id.textView1);
		switcherview = (SwitcherView) findViewById(R.id.switcherView1);
		rockerViewLeft = (RockerView) findViewById(R.id.rockerView_left);
		verticalSeekBar=(SeekBar) findViewById(R.id.vertical_Seekbar);
		btn_connect=(Button) findViewById(R.id.btn_connect);
		btn_stay=(Button) findViewById(R.id.btn_stay);
		
		isDirectionChanging=false;
		isDirectionChangedThreadStart=false;
		isStay=false;
		isConnect=false;
		isRunning=false;
		verticalSeekBar.setEnabled(false);
		btn_connect.setText("未连接");
		btn_stay.setText("悬停");
		verticalSeekBar.setEnabled(false);
		btn_stay.setEnabled(false);
		rockerViewLeft.setVisibility(View.INVISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket=new Socket("192.168.4.1", 333);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	private void initEven(){
		verticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
	
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
	
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				//油门被改变，发送油门数据给无人机
				btn_stay.setText("悬停");
				btn_stay.setBackgroundResource(R.drawable.button_bg_blue);
				isStay=false;
				
				textview1.setText("当前速度："+progress);
				flycontrol.changeAcceleration(progress);
				Log.e("progress","progress:"+progress);
				
					
				if(progress>=50){
					isOpen=true;
					if(!isRunning){
						droneUpOrDown();
					}
				}else{
					isOpen=false;
					isRunning=false;
				}
				
			}
		});
	
		
		btn_stay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isStay) {
					btn_stay.setText("悬停中");
					btn_stay.setBackgroundResource(R.drawable.button_bg_red);
					isStay=true;
					isOpen=true;
					flycontrol.changeAcceleration(500);
					if(!isRunning)
						droneUpOrDown();
				}
			}
		});
		
		//设置开关监听器
		switcherview.setOnSwitchStateListener(new OnSwitchStateListener() {
			@Override
			public void onSwitchStateUpdate(boolean swtichstate) {
				// TODO Auto-generated method stub
				//根据状态来执行用户的请求
				if(swtichstate){
					isStay=false;
					isConnect=false;
					verticalSeekBar.setEnabled(true);
					btn_stay.setEnabled(true);
					rockerViewLeft.setVisibility(View.VISIBLE);
					btn_stay.setBackgroundResource(R.drawable.button_bg_blue);
				}else{
					isOpen=false;
					isRunning=false;
					btn_stay.setText("悬停");
					verticalSeekBar.setEnabled(false);
					btn_stay.setEnabled(false);
					rockerViewLeft.setVisibility(View.INVISIBLE);
					btn_stay.setBackgroundResource(R.drawable.button_bg_gray);
				}
			}
		});
	    
	    if (rockerViewLeft != null) {
	        rockerViewLeft.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
	        rockerViewLeft.setOnShakeListener(RockerView.DirectionMode.DIRECTION_4, new RockerView.OnShakeListener() {
	            @Override
	            public void onStart() {
	            	isDirectionChanging=true;
	            	if(!isDirectionChangedThreadStart)
	            		directionChanged();
	            }
	
	            @Override
	            public void direction(RockerView.Direction direction) {
	            	switch (direction) {
	    	            case DIRECTION_LEFT:
	    	            	goWhere="left";
	    	            	 Log.i("移动位置", "移动位置 :左");
	    	                break;
	    	            case DIRECTION_RIGHT:
	    	            	goWhere="right";
	    	            	Log.i("移动位置", "移动位置 :右");
	    	                break;
	    	            case DIRECTION_UP:
	    	            	goWhere="up";
	    	            	Log.i("移动位置", "移动位置 :上");
	    	                break;
	    	            case DIRECTION_DOWN:
	    	            	goWhere="down";
	    	            	Log.i("移动位置", "移动位置 :下");
	    	                break;
	    	            default:
	    	                break;
	    	        }
	            }
	
	            @Override
	            public void onFinish() {
	            	isDirectionChanging=false;
	            	isDirectionChangedThreadStart=false;
	            	flycontrol.changeDirection("onFinish", 1500);
	            }
	        });
	    }
		btn_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isConnect) {
					btn_connect.setText("连接成功");
					btn_connect.setBackgroundResource(R.drawable.button_bg_blue);
					isConnect=true;
					flycontrol.connect();
				}
				else {
					btn_connect.setText("未连接");
					btn_connect.setBackgroundResource(R.drawable.button_bg_red);
					isConnect=false;
					flycontrol.changeAcceleration(0);
					isOpen=false;
				}
				
				
			}
		});
	}
	private void droneUpOrDown(){
		isRunning=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    if(socket != null) {
                        while (isOpen) {
                        	OutputStream out = socket.getOutputStream();
                            Log.e("油门启动中","running"+flycontrol.getAcceleration());
                            out.write(flycontrol.getAcceleration());
                            Thread.sleep(5);
                        }
                   }
                   isRunning=false;
               } catch (IOException e) {
                   e.printStackTrace();
                   isRunning=false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isRunning=false;
                }
            }
        }).start();
    }
	private void directionChanged(){
    	isDirectionChangedThreadStart=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
	                Thread.sleep(10);
	                while (isDirectionChanging) {
	                	if("left".equals(goWhere)) {
	                		directionValue+=50;
	                		flycontrol.changeDirection("roll", directionValue);
	                	}else if("up".equals(goWhere)) {
	                		directionValue+=50;
	                		flycontrol.changeDirection("pitch", directionValue);
	                	}
	                	else if("right".equals(goWhere)) {
	                		directionValue-=50;
	                		flycontrol.changeDirection("roll", directionValue);
	                	}else if("down".equals(goWhere)) {
	                		directionValue-=50;
	                		flycontrol.changeDirection("pitch", directionValue);
	                	}
	                    Log.e("方向改变中","running");
	                    Thread.sleep(200);
	                }
	                isDirectionChangedThreadStart=false;
               } catch (InterruptedException e) {
                    e.printStackTrace();
                    isDirectionChangedThreadStart=false;
                }
            }
        }).start();
    }
}
package cc.joysing.uav;

import cc.joysing.uav.adapter.Myadapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



public class MainActivity extends Activity {

	private ViewPager viewpager;
	private SharedPreferences preferences;
	
  //文件之间选择  ctrl + e
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		
		preferences = getSharedPreferences("runCount",MODE_PRIVATE);   
        int runCount = preferences.getInt("runCount", 0);    
           
        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面     
        if (runCount == 0) {    
        	//[1] 在布局文件中增加ViewPager控件
    		
    		//[2] 通过 findViewById方法找到xml布局文件中的控件
    		viewpager = (ViewPager) findViewById(R.id.viewpager);  //CTRL + 1
    		//[3] 给ViewPager设置适配器-----目的是为了加载数据到ViewPager显示
    		viewpager.setAdapter(new Myadapter(this));
        }else {
        	Intent intent=new Intent(MainActivity.this,Main.class);
			startActivity(intent);
			this.finish();   
        }
        Editor editor = preferences.edit();    
        //存入数据      
        editor.putInt("runCount", ++runCount);
        //提交修改      
        editor.commit();
		
		
	}



}

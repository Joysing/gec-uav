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
	
  //�ļ�֮��ѡ��  ctrl + e
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		
		preferences = getSharedPreferences("runCount",MODE_PRIVATE);   
        int runCount = preferences.getInt("runCount", 0);    
           
        //�жϳ�����ڼ������У�����ǵ�һ����������ת������ҳ��     
        if (runCount == 0) {    
        	//[1] �ڲ����ļ�������ViewPager�ؼ�
    		
    		//[2] ͨ�� findViewById�����ҵ�xml�����ļ��еĿؼ�
    		viewpager = (ViewPager) findViewById(R.id.viewpager);  //CTRL + 1
    		//[3] ��ViewPager����������-----Ŀ����Ϊ�˼������ݵ�ViewPager��ʾ
    		viewpager.setAdapter(new Myadapter(this));
        }else {
        	Intent intent=new Intent(MainActivity.this,Main.class);
			startActivity(intent);
			this.finish();   
        }
        Editor editor = preferences.edit();    
        //��������      
        editor.putInt("runCount", ++runCount);
        //�ύ�޸�      
        editor.commit();
		
		
	}



}

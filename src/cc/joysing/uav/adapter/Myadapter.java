package cc.joysing.uav.adapter;


import cc.joysing.uav.Main;
import cc.joysing.uav.MainActivity;
import cc.joysing.uav.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class Myadapter extends PagerAdapter {

	private Context context; //声明Context成员变量
	/*
	 * 构造方法
	 * 
	 * */
	public Myadapter(Context context) {
	        this.context = context;
	    }
	
	/*
	 * 
	 * viewpager显示的页数
	 * */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	/*
	 * 比较View与object是不相等
	 * 
	 * */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}

	/*
	 * 初始化条目的内容
	 *    条目的内容可以是单个控件，也可以是整个布局
	 *    
	 * */
	 @Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		 
		 /**
		     * Inflate a new view hierarchy from the specified xml resource. Throws
		     * {@link InflateException} if there is an error.
		     * 
		     * @param   一个布局文件的ID resource ID for an XML layout resource to load (e.g.,
		     *        <code>R.layout.main_page</code>)
		     * @param root Optional view to be the parent of the generated hierarchy (if
		     *        <em>attachToRoot</em> is true), or else simply an object that
		     *        provides a set of LayoutParams values for root of the returned
		     *        hierarchy (if <em>attachToRoot</em> is false.)
		     * @param attachToRoot Whether the inflated hierarchy should be attached to
		     *        the root parameter? If false, root is only used to create the
		     *        correct subclass of LayoutParams for the root view in the XML.
		     * @return The root View of the inflated hierarchy. If root was supplied and
		     *         attachToRoot is true, this is root; otherwise it is the root of
		     *         the inflated XML file.
		     *         
		     * public View inflate(int resource, ViewGroup root, boolean attachToRoot) 
		     * 返回值为View
		     * 参数一:布局文件的ID
		     * 参数二：就是instantiateItem的参数 ViewGroup，前提是，第三个参数设置为true
		     * 
		     */
		 //R文件的路径，可以自项目中的R文件，也可以是anroid系统中的R文件
		 View view = LayoutInflater.from(context).inflate(R.layout.viewpager, null, false);
	
		 Button button1 = (Button) view.findViewById(R.id.button1);
		 ImageView circle1=(ImageView) view.findViewById(R.id.circle1);
		 ImageView circle2=(ImageView) view.findViewById(R.id.circle2);
		 ImageView circle3=(ImageView) view.findViewById(R.id.circle3);
		 RelativeLayout relativeLayout=(RelativeLayout)view.findViewById(R.id.viewpager);
		 switch (position) {
		 case 0:
			 circle1.setImageResource(R.drawable.red_circle);
			 break;
		case 1:
			circle2.setImageResource(R.drawable.red_circle);
			relativeLayout.setBackgroundResource(R.drawable.slide02);
			
			break;
		case 2:
			circle3.setImageResource(R.drawable.red_circle);
			relativeLayout.setBackgroundResource(R.drawable.slide03);
			button1.setVisibility(View.VISIBLE); //设置按钮为可见
			button1.setText("开始体验");
			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context,Main.class);
					context.startActivity(intent);
					((Activity) context).finish();
				}
			});
			break;
		default:
			break;
		}

		 container.addView(view); //把生产好的条目，增加到ViewPager
		 
		 return view;
	}
	 
	 /*
		 * 删除条目的内容
		 *    
		 *    
		 * */
	 @Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		 //super.destroyItem(container, position, object); //注释掉,否则，报错
		 container.removeView((View) object); //删除ViewPager条目
		
		
	}
	

}

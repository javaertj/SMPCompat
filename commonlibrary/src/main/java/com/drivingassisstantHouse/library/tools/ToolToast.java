package com.drivingassisstantHouse.library.tools;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.drivingassisstantHouse.library.config.SysEnv;


/**
 * 自定义Toast控件
 * @author sunji
 * @version 1.1
 *
 *
 */
@TargetApi(16)
public class ToolToast {

	/**
	 * 系统自身的toast一条信息
	 * @param context 上下文
	 * @param  msg 消息文本字符串，不能为空
	 *
	 * **/

	public static void showToast(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 系统自身的toast一条信息
	 * @param context 上下文
	 * @param  msgRes 消息文本资源id
	 *
	 * **/
	public static void showToast(Context context,int msgRes){
		Toast.makeText(context, msgRes, Toast.LENGTH_SHORT).show();
	}


	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	/**
	 * 弹出较长时间提示信息
	 * @param context 上下文对象
	 * @param msg 要显示的信息
	 */
	public static void showLong(Context context, String msg){
		buildToast(context,msg,Toast.LENGTH_LONG).show();
	}

	/**
	 * 弹出较长时间提示信息
	 * @param msg 要显示的信息
	 */
	public static void showLong(String msg){
		buildToast(SysEnv.context,msg,Toast.LENGTH_LONG).show();
	}

	/**
	 * 弹出较长时间提示信息
	 * @param msgRes 要显示的信息
	 */
	public static void showLong(int msgRes){
		showLong(SysEnv.context.getString(msgRes));
	}
	/**
	 * 弹出较短时间提示信息
	 * @param context 上下文对象
	 * @param msg 要显示的信息
	 */
	public static void showShort(Context context, String msg){
		buildToast(context,msg,Toast.LENGTH_SHORT).show();
	}
	/**
	 * 弹出较短时间提示信息
	 * @param context 上下文对象
	 * @param msgRes 要显示的信息
	 */
	public static void showShort(Context context, int msgRes){
		 showShort(context,context.getString(msgRes));
	}


	/**
	 * 弹出较短时间提示信息
	 * @param msg 要显示的信息
	 */
	public static void showShort(String msg){
		buildToast(SysEnv.context,msg,Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出较短时间提示信息
	 * @param msgRes 要显示的信息
	 */
	public static void showShort(int msgRes){
		showShort(SysEnv.context.getString(msgRes));
	}

	/**
	 * 构造Toast
	 * @param context 上下文
	 * @return
	 */
	private static Toast buildToast(Context context,String msg,int duration){
		return buildToast(context,msg,duration,"#000000",16);
	}


	/**
	 * 构造Toast
	 * @param context 上下文
	 * @param msg 消息
	 * @param duration 显示时间
	 * @param bgColor 背景颜色
	 * @return
	 */
	public static Toast buildToast(Context context,String msg,int duration,String bgColor){
		return buildToast(context,msg,duration,bgColor,16);
	}


	/**
	 * 构造Toast
	 * @param context 上下文
	 * @param msg	消息
	 * @param duration 显示时间
	 * @param bgColor 背景颜色
	 * @param textSp  文字大小
	 * @return
	 */
	public static Toast buildToast(Context context,String msg,int duration,String bgColor,int textSp){
		return buildToast(context,msg,duration,bgColor,textSp,10);
	}

	/**
	 * 构造Toast
	 * @param context 上下文
	 * @param msg	消息
	 * @param duration 显示时间
	 * @param bgColor 背景颜色
	 * @param textSp  文字大小
	 * @param cornerRadius  四边圆角弧度
	 * @return
	 */
	public static Toast buildToast(Context context,String msg,int duration,String bgColor,int textSp,int cornerRadius){
		mHandler.removeCallbacks(r);

		if(null == mToast){
			//构建Toast
			mToast = Toast.makeText(context, null, duration);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			//取消toast
//			mHandler.postDelayed(r, duration);
		}

		//设置Toast文字
		TextView tv = new TextView(context);
		int dpPadding = ToolUnit.dipTopx(10);
		tv.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
		tv.setGravity(Gravity.CENTER);
		tv.setText(msg);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSp);

		//Toast文字TextView容器
		LinearLayout mLayout = new LinearLayout(context);
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(Color.parseColor(bgColor));
		shape.setCornerRadius(cornerRadius);
		shape.setStroke(1, Color.parseColor(bgColor));
		shape.setAlpha(180);
		//sdk level 16增加setBackground()方法
		if(Build.VERSION.SDK_INT>=16) {
			mLayout.setBackground(shape);
		}else{
			mLayout.setBackgroundDrawable(shape);
		}
		mLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		//设置layout_gravity
		params.gravity = Gravity.CENTER;
		mLayout.setLayoutParams(params);
		//设置gravity
		mLayout.setGravity(Gravity.CENTER);
		mLayout.addView(tv);

		//将自定义View覆盖Toast的View
		mToast.setView(mLayout);

		return mToast;
	}
}
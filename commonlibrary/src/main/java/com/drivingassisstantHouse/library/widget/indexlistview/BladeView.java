package com.drivingassisstantHouse.library.widget.indexlistview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.drivingassisstantHouse.library.R;
import com.drivingassisstantHouse.library.tools.ToolUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表右侧侧边栏
 *
 * @author
 */
public class BladeView extends View {

    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
//	public static String[] b = { "热门", "A", "B", "C", "D", "E", "F", "G", "H",
//			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
//			"V", "W", "X", "Y", "Z" };
    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public BladeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BladeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BladeView(Context context) {
        super(context);
    }

    private List<String> bladeString = new ArrayList<>();

    public List<String> getBladeString() {
        return bladeString;
    }

    public void setBladeString(List<String> bladeString) {
        this.bladeString = bladeString;
        postInvalidate();
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度

        float singleHeight = (height * 1f) / bladeString.size();// 获取每一个字母的高度
        singleHeight = (height * 1f - singleHeight / 2) / bladeString.size();
        for (int i = 0; i < bladeString.size(); i++) {
            // paint.setColor(Color.rgb(86, 86, 86));
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            int fixedHeight = ToolUnit.dipTopx(10);
            paint.setTextSize(fixedHeight);

            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(bladeString.get(i)) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(bladeString.get(i), xPos, yPos, paint);
            paint.reset();// 重置画笔
        }
        // setBackgroundDrawable(new ColorDrawable(0x13161316));
        setBackgroundResource(R.drawable.slide_bar_bg);

        // setBackgroundDrawable(new
        // ColorDrawable(Color.parseColor("#c0c2c9")));

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * bladeString.size());// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                // setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                // setBackgroundDrawable(new ColorDrawable(0x13161316));
                if (oldChoose != c) {
                    if (c >= 0 && c < bladeString.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(bladeString.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(bladeString.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}

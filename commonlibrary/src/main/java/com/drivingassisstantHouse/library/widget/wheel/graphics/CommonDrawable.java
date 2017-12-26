/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.drivingassisstantHouse.library.widget.wheel.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

import com.drivingassisstantHouse.library.R;
import com.drivingassisstantHouse.library.widget.wheel.common.WheelConstants;
import com.drivingassisstantHouse.library.widget.wheel.util.WheelUtils;
import com.drivingassisstantHouse.library.widget.wheel.widget.WheelView;


/**
 * common滚轮样式 该类已经被更改，不具有通用性
 *
 * @author venshine
 */
public class CommonDrawable extends WheelDrawable {

    private static final int[] SHADOWS_COLORS =
            {
                    0xFF111111,
                    0x00AAAAAA,
                    0x00AAAAAA
            };  // 阴影色值

    private GradientDrawable mTopShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            SHADOWS_COLORS);    // 顶部阴影

    private GradientDrawable mBottomShadow = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
            SHADOWS_COLORS);    // 底部阴影

    private Paint mCommonBgPaint, mCommonPaint, mCommonDividerPaint, mCommonBorderPaint;

    private int mWheelSize, mItemH;

    public CommonDrawable(int width, int height, WheelView.WheelViewStyle style, int wheelSize, int itemH, Context context) {
        super(width, height, style,context);
        mWheelSize = wheelSize;
        mItemH = itemH;
        init();
    }

    private void init() {
        mCommonBgPaint = new Paint();
        mCommonBgPaint.setColor(mStyle.backgroundColor != -1 ? mStyle.backgroundColor : WheelConstants
                .WHEEL_SKIN_COMMON_BG);

        mCommonPaint = new Paint();
        mCommonPaint.setColor(WheelConstants.WHEEL_SKIN_COMMON_COLOR);

        mCommonDividerPaint = new Paint();
        mCommonDividerPaint.setColor(WheelConstants.WHEEL_SKIN_COMMON_DIVIDER_COLOR);
        mCommonDividerPaint.setStrokeWidth(2);

        mCommonBorderPaint = new Paint();
        mCommonBorderPaint.setStrokeWidth(6);
        mCommonBorderPaint.setColor(WheelConstants.WHEEL_SKIN_COMMON_BORDER_COLOR);

    }

    @Override
    public void draw(Canvas canvas) {
        // draw background
//        canvas.drawRect(0, 0, mWidth, mHeight, mCommonBgPaint);
//
        // draw select border
        if (mItemH != 0) {
            int value= WheelUtils.dip2px(getmContenxt(),18);//最下面红线的高度
            Bitmap factory=BitmapFactory.decodeResource(getmContenxt().getResources(), R.drawable.selected_bg);
            Rect rect=new Rect(0, mItemH * (mWheelSize / 2)-5, mWidth, mItemH
                    * (mWheelSize / 2 + 1)-value/2-5);//5是界面显示的偏移值，多减去5，发现刚刚好！！！
            canvas.drawBitmap(factory,null,rect,mCommonPaint);

//            canvas.drawRect(0, mItemH * (mWheelSize / 2), mWidth, mItemH
//                    * (mWheelSize / 2 + 1), mCommonPaint);
//            canvas.drawLine(0, mItemH * (mWheelSize / 2), mWidth, mItemH
//                    * (mWheelSize / 2), mCommonDividerPaint);
//            canvas.drawLine(0, mItemH * (mWheelSize / 2 + 1), mWidth, mItemH
//                    * (mWheelSize / 2 + 1), mCommonDividerPaint);

            // top, bottom
            mTopShadow.setBounds(0, 0, mWidth, mItemH);
            mTopShadow.draw(canvas);

            mBottomShadow.setBounds(0, mHeight - mItemH, mWidth, mHeight);
            mBottomShadow.draw(canvas);

            // left,right
            canvas.drawLine(0, 0, 0, mHeight, mCommonBorderPaint);
            canvas.drawLine(mWidth, 0, mWidth, mHeight, mCommonBorderPaint);
        }
    }
}

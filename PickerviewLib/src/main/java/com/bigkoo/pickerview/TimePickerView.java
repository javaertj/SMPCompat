package com.bigkoo.pickerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sai on 15/11/22.
 */
public class TimePickerView extends BasePickerView implements View.OnClickListener {
    public enum Type {
        ALL, YEAR_MONTH_DAY, HOURS_MINS, MONTH_DAY_HOUR_MIN, YEAR_MONTH
    }// 四种选择模式，年月日时分，年月日，时分，月日时分

    WheelTime wheelTime;
    private Button btnSubmit, btnCancel;
    private TextView tvTitle;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private static final String TAG_TITLE = "title";
    private OnTimeSelectListener timeSelectListener;

    public TimePickerView(Context context, Type type,PickerControllerPosition position) {
        super(context);
        int layoutId = position == PickerControllerPosition.TOP ? R.layout.pickerview_time_top :
                R.layout.pickerview_time_bottom;
        LayoutInflater.from(context).inflate(layoutId, contentContainer);
        // -----确定和取消按钮
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //顶部标题
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTag(TAG_TITLE);
        // ----时间转轮
        final View timepickerview = findViewById(R.id.timepicker);
        wheelTime = new WheelTime(timepickerview, type);
    }

    public TimePickerView create(){
        //默认选中当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
        return this;
    }

    public TimePickerView setEnableYearLable(boolean enableYearLable) {
        wheelTime.setEnableYearLable(enableYearLable);
        return  this;
    }

    public TimePickerView setEnableMonthLable(boolean enableMonthLable) {
      wheelTime.setEnableMonthLable(enableMonthLable);
        return  this;
    }

    public TimePickerView setEnableDayLable(boolean enableDayLable) {
       wheelTime.setEnableDayLable(enableDayLable);
        return  this;
    }

    public TimePickerView setEnableHourLable(boolean enableHourLable) {
        wheelTime.setEnableHourLable(enableHourLable);
        return  this;
    }

    public TimePickerView setEnableMinuteLable(boolean enableMinuteLable) {
       wheelTime.setEnableMinuteLable(enableMinuteLable);
        return  this;
    }

    /**
     * 设置可以选择的时间范围
     *
     * @param startYear
     * @param endYear
     */
    public TimePickerView setRange(int startYear, int endYear) {
        wheelTime.setStartYear(startYear);
        wheelTime.setEndYear(endYear);
        return this;
    }

    /**
     * 设置选中时间
     *
     * @param date
     */
    public TimePickerView setTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
        return this;
    }

//    /**
//     * 指定选中的时间，显示选择器
//     *
//     * @param date
//     */
//    public void show(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        if (date == null)
//            calendar.setTimeInMillis(System.currentTimeMillis());
//        else
//            calendar.setTime(date);
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hours = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        wheelTime.setPicker(year, month, day, hours, minute);
//        show();
//    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public TimePickerView setCyclic(boolean cyclic) {
        wheelTime.setCyclic(cyclic);
        return this;
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
        } else {
            if (timeSelectListener != null) {
                try {
                    Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                    timeSelectListener.onTimeSelect(v, date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            dismiss();
        }
    }

    public interface OnTimeSelectListener {
        public void onTimeSelect(View view, Date date);
    }

    public TimePickerView setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
        return this;
    }

    public TimePickerView setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public TimePickerView setCancelText(String title) {
        btnCancel.setText(title);
        return this;
    }

    public TimePickerView setConfirmText(String title) {
        btnSubmit.setText(title);
        return this;
    }

    public TimePickerView setEnableTitleClick(boolean enableTitleClick) {
        if (enableTitleClick) {
            tvTitle.setOnClickListener(this);
        }else {
            tvTitle.setOnClickListener(null);
        }
        return this;
    }
}

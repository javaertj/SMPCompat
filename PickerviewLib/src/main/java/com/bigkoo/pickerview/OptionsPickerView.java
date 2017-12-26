package com.bigkoo.pickerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelOptions;

import java.util.ArrayList;

/**
 * Created by Sai on 15/11/22.
 */
public class OptionsPickerView<T> extends BasePickerView implements View.OnClickListener {
    private WheelOptions wheelOptions;
    private Button btnSubmit, btnCancel;
    private TextView tvTitle;
    private OnOptionsSelectListener optionsSelectListener;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private static final String TAG_TITLE = "title";
    private boolean enableTitleClick;

    public OptionsPickerView(Context context, PickerControllerPosition position) {
        super(context);
        int layoutId = position == PickerControllerPosition.TOP ? R.layout.pickerview_options_top :
                R.layout.pickerview_options_bottom;
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
        if (enableTitleClick) {
            tvTitle.setTag(TAG_TITLE);
            tvTitle.setOnClickListener(this);
        }
        // ----转轮
        final View optionspicker = findViewById(R.id.optionspicker);
        wheelOptions = new WheelOptions(optionspicker);
    }

    public OptionsPickerView setPicker(ArrayList<T> optionsItems) {
        wheelOptions.setPicker(optionsItems, null, null, false);
        return this;
    }

    public OptionsPickerView setPicker(ArrayList<T> options1Items,
                                       ArrayList<ArrayList<T>> options2Items, boolean linkage) {
        wheelOptions.setPicker(options1Items, options2Items, null, linkage);

        return this;
    }

    public OptionsPickerView setPicker(ArrayList<T> options1Items,
                                       ArrayList<ArrayList<T>> options2Items,
                                       ArrayList<ArrayList<ArrayList<T>>> options3Items,
                                       boolean linkage) {
        wheelOptions.setPicker(options1Items, options2Items, options3Items,
                linkage);

        return this;
    }

    /**
     * 设置选中的item位置
     *
     * @param option1
     */
    public OptionsPickerView setSelectOptions(int option1) {
        wheelOptions.setCurrentItems(option1, 0, 0);
        return this;
    }

    /**
     * 设置选中的item位置
     *
     * @param option1
     * @param option2
     */
    public OptionsPickerView setSelectOptions(int option1, int option2) {
        wheelOptions.setCurrentItems(option1, option2, 0);
        return this;
    }

    /**
     * 设置选中的item位置
     *
     * @param option1
     * @param option2
     * @param option3
     */
    public OptionsPickerView setSelectOptions(int option1, int option2, int option3) {
        wheelOptions.setCurrentItems(option1, option2, option3);
        return this;
    }

    /**
     * 设置选项的单位
     *
     * @param label1
     */
    public OptionsPickerView setLabels(String label1) {
        wheelOptions.setLabels(label1, null, null);
        return this;
    }

    /**
     * 设置选项的单位
     *
     * @param label1
     * @param label2
     */
    public OptionsPickerView setLabels(String label1, String label2) {
        wheelOptions.setLabels(label1, label2, null);
        return this;
    }

    /**
     * 设置选项的单位
     *
     * @param label1
     * @param label2
     * @param label3
     */
    public OptionsPickerView setLabels(String label1, String label2, String label3) {
        wheelOptions.setLabels(label1, label2, label3);
        return this;
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public OptionsPickerView setCyclic(boolean cyclic) {
        wheelOptions.setCyclic(cyclic);
        return this;
    }

    public OptionsPickerView setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        wheelOptions.setCyclic(cyclic1, cyclic2, cyclic3);
        return this;
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
        } else {
            if (optionsSelectListener != null) {
                try {
                    int[] optionsCurrentItems = wheelOptions.getCurrentItems();
                    optionsSelectListener.onOptionsSelect(v, optionsCurrentItems[0],
                            optionsCurrentItems[1], optionsCurrentItems[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dismiss();
        }
    }

    public interface OnOptionsSelectListener {
        public void onOptionsSelect(View view, int options1, int option2, int options3);
    }

    public OptionsPickerView setOnoptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
        return this;
    }

    public OptionsPickerView setCancelText(String title) {
        btnCancel.setText(title);
        return this;
    }

    public OptionsPickerView setConfirmText(String title) {
        btnSubmit.setText(title);
        return this;
    }

    public OptionsPickerView setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }


    public OptionsPickerView<T> setEnableTitleClick(boolean enableTitleClick) {
        this.enableTitleClick = enableTitleClick;
        return this;
    }
}

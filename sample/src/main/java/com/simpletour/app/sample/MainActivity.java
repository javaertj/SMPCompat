package com.simpletour.app.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drivingassisstantHouse.library.base.BaseActivity;
import com.drivingassisstantHouse.library.widget.SlideMenu;

import butterknife.BindView;

/**
 * Description：
 * Creator：yankebin
 * CreatedAt：2018/5/31
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.sm_layout)
    SlideMenu slideMenu;

    Button toogle;

    @Override
    public int bindLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public void initView(View view) {
        slideMenu.setMode(SlideMenu.MODE_SCROLL_ALL_WITH_SCALE);
        slideMenu.setMenu(R.layout.home_include_slide_menu);
        slideMenu.setContent(R.layout.home_include_slide_content);
    }

    @Override
    public void doBusiness(Context mContext) {
        toogle=slideMenu.findViewById(R.id.toogle);
        toogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.toggle();
            }
        });
    }

    @Override
    public void resume() {

    }

    @Override
    public void destroy() {

    }
}

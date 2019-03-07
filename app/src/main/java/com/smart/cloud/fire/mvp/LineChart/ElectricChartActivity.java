package com.smart.cloud.fire.mvp.LineChart;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.mvp.LineChart.All.AllDataChartFragment;
import com.smart.cloud.fire.mvp.LineChart.Seven.SevenDataFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class ElectricChartActivity extends MvpActivity<LineChartPresenter > {

    private Context mContext;

    @Bind(R.id.rg_data)
    RadioGroup rg_data;
    @Bind(R.id.rb_all)
    RadioButton rb_all;
    @Bind(R.id.rb_days)
    RadioButton rb_days;

    AllDataChartFragment allDataChartFragment;
    SevenDataFragment sevenDataFragment;
    FragmentTransaction ft;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_chart);

        ButterKnife.bind(this);
        mContext = this;


        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();



        rg_data.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                // TODO Auto-generated method stub
                ft = fragmentManager.beginTransaction();

                if(checkedId == rb_all.getId()){
                    if(allDataChartFragment==null){
                        allDataChartFragment=new AllDataChartFragment();
                        ft.add(R.id.fragment_content,allDataChartFragment);
                    }
                    hideFragment(ft);
                    ft.show(allDataChartFragment);
                }else if(checkedId == rb_days.getId()){
                    if(sevenDataFragment==null){
                        sevenDataFragment=new SevenDataFragment();
                        ft.add(R.id.fragment_content,sevenDataFragment);
                    }
                    hideFragment(ft);
                    ft.show(sevenDataFragment);
                }
                ft.commit();
            }
        });
        rb_all.setChecked(true);
    }

    public void hideFragment(FragmentTransaction ft) {
        //如果不为空，就先隐藏起来
        if (allDataChartFragment != null) {
            ft.hide(allDataChartFragment);
        }
        if(sevenDataFragment!=null){
            ft.hide(sevenDataFragment);
        }
    }

    @Override
    protected LineChartPresenter createPresenter() {
        return null;
    }
}

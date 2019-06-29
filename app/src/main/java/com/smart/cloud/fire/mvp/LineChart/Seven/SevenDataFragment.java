package com.smart.cloud.fire.mvp.LineChart.Seven;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.TemperatureTime;
import com.smart.cloud.fire.mvp.LineChart.LineChartPresenter;
import com.smart.cloud.fire.mvp.LineChart.LineChartView;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.view.BarChart03View;
import com.smart.cloud.fire.view.LineChart01View;

import org.xclcharts.renderer.XEnum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;


public class SevenDataFragment extends MvpFragment<LineChartPresenter> implements LineChartView {

    @Bind(R.id.linechart)
    LineChart01View mLineChartView;//线性图表控件
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    private LineChartPresenter lineChartPresenter;

    private Context mContext;
    private String userID;
    private int privilege;
    private String electricMac;
    private String electricType;
    private String electricNum;
    private int page = 1;

    private String titleName="";
    private String axisYName="";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_seven_data, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        electricMac = getActivity().getIntent().getExtras().getString("electricMac");
        electricType = getActivity().getIntent().getExtras().getInt("electricType") + "";
        electricNum = getActivity().getIntent().getExtras().getInt("electricNum") + "";
        lineChartPresenter.getElectricHistoryInfo(userID, privilege + "", electricMac, electricType, electricNum, false);
        switch (electricType) {
            case "6":
                axisYName="电压值(V)";
                titleName="电压折线图";
                break;
            case "7":
                axisYName="电流值(A)";
                titleName="电流折线图";
                break;
            case "8":
                axisYName="漏电流(mA)";
                titleName="漏电流线图";
                break;
            case "9":
                axisYName="温度值(℃)";
                titleName="温度折线图";
                break;
            default:
                break;
        }
    }

    @Override
    public String getFragmentName() {
        return "AllDataChartFragment";
    }

    @Override
    public void getDataSuccess(List<TemperatureTime.ElectricBean> temperatureTimes) {
        int len = temperatureTimes.size();
        if (len == 6) {
            drawChart(temperatureTimes);
        } else if (len < 6) {
            drawChart(temperatureTimes);
        }else if(len ==0){
            T.showShort(mContext,"无数据");
        }
    }

    private void drawChart(List<TemperatureTime.ElectricBean> temperatureTimes) {
        LinkedList<String> l=new LinkedList<>();
        LinkedList<Double> c=new LinkedList<>();
        for(int i=0;i<temperatureTimes.size();i++){
            l.add(temperatureTimes.get(i).getElectricTime());
            c.add(Double.parseDouble(temperatureTimes.get(i).getElectricValue()));
        }
        mLineChartView.initView(titleName,l,c, XEnum.LabelLineFeed.EVEN_ODD);
    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
        mLineChartView.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected LineChartPresenter createPresenter() {
        lineChartPresenter = new LineChartPresenter(this);
        return lineChartPresenter;
    }


}

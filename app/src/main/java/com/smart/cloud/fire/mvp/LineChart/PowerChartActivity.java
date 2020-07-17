package com.smart.cloud.fire.mvp.LineChart;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.TemperatureTime;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
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

public class PowerChartActivity extends MvpActivity<LineChartPresenter> implements LineChartView {
    /*=========== 控件相关 ==========*/
    @Bind(R.id.linechart)
    LineChart01View mLineChartView;//线性图表控件
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.btn_next)
    ImageView btnNext;
    @Bind(R.id.btn_before)
    ImageView btnBefore;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.btn_new)
    ImageView btnNew;
    @Bind(R.id.page_text)
    TextView page_text;
    private LineChartPresenter lineChartPresenter;

    private Context context;
    private String userID;
    private int privilege;
    private String electricMac;
    private String electricType;
    private String electricNum;
    private int page = 1;
    private boolean haveDataed = true;
    private Map<Integer, String> data = new HashMap<>();

    private String titleName="";
    private String axisYName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_chart);

        ButterKnife.bind(this);
        context = this;
        userID = SharedPreferencesManager.getInstance().getData(context,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        electricMac = getIntent().getExtras().getString("electricMac");
        electricType = getIntent().getExtras().getInt("electricType") + "";
        electricNum = getIntent().getExtras().getInt("electricNum") + "";

        axisYName="用电量(度)";
        titleName="每日用电量折线图";

        mvpPresenter.getElectricTypeInfoPower(userID, privilege + "", electricMac, electricType, electricNum, page + "","", false);
    }


    /**
     * 重点方法，计算绘制图表
     */

    @Override
    public void getDataSuccess(List<TemperatureTime.ElectricBean> temperatureTimes) {
        int len = temperatureTimes.size();
        page_text.setText("第"+page+"页");
        if (len == 6) {
            drawChart(temperatureTimes);
        } else if (len < 6) {
            drawChart(temperatureTimes);
        }else if(len ==0){
            T.showShort(mActivity,"无数据");
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
        T.showShort(context, msg);
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

    @OnClick({R.id.btn_next, R.id.btn_before,R.id.btn_new})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                page = page + 1;
                mvpPresenter.getElectricTypeInfoPower(userID, privilege + "", electricMac, electricType, electricNum, page + "","", false);
                break;
            case R.id.btn_before:
                if (page > 1) {
                    page = page - 1;
                    mvpPresenter.getElectricTypeInfoPower(userID, privilege + "", electricMac, electricType, electricNum, page + "","", false);
                }
                break;
            case R.id.btn_new:
                page = 1;
                mvpPresenter.getElectricTypeInfoPower(userID, privilege + "", electricMac, electricType, electricNum, page + "","", false);
                break;
        }
    }
}


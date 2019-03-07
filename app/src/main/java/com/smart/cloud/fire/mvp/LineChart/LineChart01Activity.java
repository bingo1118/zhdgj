package com.smart.cloud.fire.mvp.LineChart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.TemperatureTime;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.LineChart01View;

import org.json.JSONException;
import org.json.JSONObject;
import org.xclcharts.renderer.XEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by Administrator on 2016/11/1.
 */
public class LineChart01Activity extends MvpActivity<LineChartPresenter> implements LineChartView {
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
        setContentView(R.layout.activity_line_chart01);

        ButterKnife.bind(this);
        context = this;
        userID = SharedPreferencesManager.getInstance().getData(context,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        electricMac = getIntent().getExtras().getString("electricMac");
        electricType = getIntent().getExtras().getInt("electricType") + "";
        electricNum = getIntent().getExtras().getInt("electricNum") + "";
        mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false);
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

    private String getTime(String str) {
        String strings = str.substring(5, str.length());
        return strings;
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
                mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false);
                break;
            case R.id.btn_before:
                if (page > 1) {
                    page = page - 1;
                    mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false);
                }
                break;
            case R.id.btn_new:
                page = 1;
                mvpPresenter.getElectricTypeInfo(userID, privilege + "", electricMac, electricType, electricNum, page + "", false);
                break;
        }
    }
}

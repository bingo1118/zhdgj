package com.smart.cloud.fire.mvp.electric;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.activity.AccountManage.AllAreaEntity;
import com.smart.cloud.fire.activity.AccountManage.AreaListAdapter;
import com.smart.cloud.fire.activity.AlarmHistory.OneDeviceAlarmHistoryActivity;
import com.smart.cloud.fire.adapter.ElectricActivityAdapterTest;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.base.ui.BaseActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.ElectricDXDetailEntity;
import com.smart.cloud.fire.global.ElectricValue;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.ElectrTimerTask.ElectrTimerTaskActivity;
import com.smart.cloud.fire.mvp.LineChart.ElectricChartActivity;
import com.smart.cloud.fire.mvp.LineChart.PowerChartActivity;
import com.smart.cloud.fire.mvp.electricChangeHistory.ElectricChangeHistoryActivity;
import com.smart.cloud.fire.mvp.fragment.CollectFragment.AlarmMessageModel;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimePickerDialog;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;

public class AlarmValueInfoActivity extends BaseActivity {

    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;

    @Bind(R.id.title_tv)
    TextView title_tv;
    @Bind(R.id.time_tv)
    TextView time_tv;

    @Bind(R.id.dy_a)
    TextView dy_a;
    @Bind(R.id.dy_b)
    TextView dy_b;
    @Bind(R.id.dy_c)
    TextView dy_c;
    @Bind(R.id.dl_a)
    TextView dl_a;
    @Bind(R.id.dl_b)
    TextView dl_b;
    @Bind(R.id.dl_c)
    TextView dl_c;
    @Bind(R.id.ldl_a)
    TextView ldl_a;
    @Bind(R.id.wd_a)
    TextView wd_a;
    @Bind(R.id.wd_b)
    TextView wd_b;
    @Bind(R.id.wd_c)
    TextView wd_c;
    @Bind(R.id.wd_n)
    TextView wd_n;

    AlarmMessageModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_value_info);

        mContext=this;
        data=(AlarmMessageModel) getIntent().getSerializableExtra("data");

        refreshListView();
        title_tv.setText(data.getName());
        time_tv.setText(data.getAlarmTime());
        getData(data.getAlarmDetailID());
    }

    private void refreshListView() {
        //设置刷新时动画的颜色，可以设置4个
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
    }

    private void getData(String id) {
        mProgressBar.setVisibility(View.VISIBLE);
        Observable mObservable = BasePresenter.apiStores1.getOneElectricAlarmInfo(id);

        BasePresenter.addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ElectricDXDetailEntity>() {
            @Override
            public void onSuccess(ElectricDXDetailEntity model) {
                int resultCode = model.getErrorCode();
                if(resultCode==0){
                    setDataToView(model);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                T.showShort(mContext,"无数据");
            }

            @Override
            public void onCompleted() {
                mProgressBar.setVisibility(View.GONE);
            }
        }));
    }


    private void setDataToView(ElectricDXDetailEntity entity) {

        dy_a.setText(Float.parseFloat(entity.getVoltage())+"");
        dy_b.setText(Float.parseFloat(entity.getVoltageB())+"");
        dy_c.setText(Float.parseFloat(entity.getVoltageC())+"");

        dl_a.setText(Float.parseFloat(entity.getCurrent())+"");
        dl_b.setText(Float.parseFloat(entity.getCurrentB())+"");
        dl_c.setText(Float.parseFloat(entity.getCurrentC())+"");

        ldl_a.setText(Float.parseFloat(entity.getLeakage())+"");

        wd_a.setText(Float.parseFloat(entity.getTempFire())+"");
        wd_b.setText(Float.parseFloat(entity.getTempFireB())+"");
        wd_c.setText(Float.parseFloat(entity.getTempFireC())+"");
        wd_n.setText(Float.parseFloat(entity.getTempZero())+"");

    }

}

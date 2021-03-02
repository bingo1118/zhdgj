package com.smart.cloud.fire.mvp.main;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smart.cloud.fire.activity.AddDev.AddDevActivity;
import com.smart.cloud.fire.activity.AlarmHistory.AlarmHistoryActivity;
import com.smart.cloud.fire.activity.Camera.CameraDevActivity;
import com.smart.cloud.fire.activity.Electric.ElectricDevActivity;
import com.smart.cloud.fire.activity.Map.MapActivity;
import com.smart.cloud.fire.activity.SelectDevPlaceType.SelectDevPlaceTypeActivity;
import com.smart.cloud.fire.activity.Setting.MyZoomActivity;
import com.smart.cloud.fire.mvp.electric.ElectricActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2018/7/23.
 */
public class HomeFragment extends Fragment{

    @Bind(R.id.more_item)
    ImageView more_item;

    @Bind(R.id.add_dev)
    ImageView add_dev;
    @Bind(R.id.electr)
    ImageView electr;
    @Bind(R.id.map)
    ImageView map;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.add_dev,R.id.electr,R.id.map,R.id.more_item,R.id.alarm_msg})
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.add_dev:
                intent=new Intent(getActivity(), AddDevActivity.class);
                startActivity(intent);
                break;
            case R.id.electr:
                intent=new Intent(getActivity(), SelectDevPlaceTypeActivity.class);
                startActivity(intent);
                break;
//            case R.id.camera:
//                intent=new Intent(getActivity(), CameraDevActivity.class);
//                startActivity(intent);
//                break;
            case R.id.map:
                intent=new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.alarm_msg:
                intent=new Intent(getActivity(), AlarmHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.more_item:
//                DrawerLayout drawerLayout= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
//                drawerLayout.openDrawer(GravityCompat.START);
                intent=new Intent(getActivity(), MyZoomActivity.class);
                startActivity(intent);
                break;
        }
    }
}

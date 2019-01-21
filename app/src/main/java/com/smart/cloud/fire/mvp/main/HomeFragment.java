package com.smart.cloud.fire.mvp.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smart.cloud.fire.activity.AddDev.AddDevActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2018/7/23.
 */
public class HomeFragment extends Fragment{

    @Bind(R.id.more_item)
    ImageView more_item;

    @Bind(R.id.add_dev)
    ImageView add_dev;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        ButterKnife.bind(this, view);
        more_item.setVisibility(View.VISIBLE);
        more_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout= (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        add_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddDevActivity.class);
                startActivity(intent);
            }
        });
        return view;

    }
}

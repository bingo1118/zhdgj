package com.smart.cloud.fire.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentPresenter;
import com.smart.cloud.fire.ui.CallManagerDialogActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class ElectricFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    public static final int PULLUP_LOAD_MORE = 0;//上拉加载更多
    public static final int LOADING_MORE = 1;//正在加载中
    public static final int NO_MORE_DATA = 2;//正在加载中
    public static final int NO_DATA = 3;//无数据
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Electric> listNormalSmoke;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(Electric)v.getTag());
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Electric data);
    }

    public ElectricFragmentAdapter(Context mContext, List<Electric> listNormalSmoke) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.listNormalSmoke = listNormalSmoke;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    /**
     * item显示类型
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.shop_info_zhdgj_adapter, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            ItemViewHolder viewHolder = new ItemViewHolder(view);
            view.setOnClickListener(this);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    public static int[] colorArray={R.color.blue,R.color.login_btn,R.color.fuxk_settle_black_mark,R.color.yg_lx_bg};

    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {

            final Electric normalSmoke = listNormalSmoke.get(position);
            ((ItemViewHolder) holder).category_group_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onItemClick(v,normalSmoke);
                    }
                }
            });
            ((ItemViewHolder) holder).category_group_lin.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showNormalDialog(normalSmoke.getMac(),normalSmoke.getDeviceType(),position);
                    return false;
                }
            });
            ((ItemViewHolder) holder).mac_tv.setText(normalSmoke.getMac());//@@
//            ((ItemViewHolder) holder).repeater_tv.setText(normalSmoke.getRepeater());
//            ((ItemViewHolder) holder).type_tv.setText(normalSmoke.getPlaceType());
//            ((ItemViewHolder) holder).area_tv.setText(normalSmoke.getAreaName());
            ((ItemViewHolder) holder).manager_img.setOnClickListener(new View.OnClickListener() {//拨打电话提示框。。
                @Override
                public void onClick(View v) {
//                    String phoneOne = normalSmoke.getPrincipal1Phone();
//                    mShopInfoFragmentPresenter.telPhoneAction(mContext,phoneOne);
                if(normalSmoke.getPrincipal1().length()>0||normalSmoke.getPrincipal2().length()>0){
                    Intent intent=new Intent(mContext, CallManagerDialogActivity.class);
                    intent.putExtra("people1",normalSmoke.getPrincipal1());
                    intent.putExtra("people2",normalSmoke.getPrincipal2());
                    intent.putExtra("phone1",normalSmoke.getPrincipal1Phone());
                    intent.putExtra("phone2",normalSmoke.getPrincipal2Phone());
                    mContext.startActivity(intent);
                }else{
                    T.showShort(mContext,"无联系人信息");
                }

                }
            });
            final int state = normalSmoke.getNetState();
            final int privilege = MyApp.app.getPrivilege();
            ((ItemViewHolder) holder).dev_name.setText(normalSmoke.getName());

            if (state == 0) {//设备不在线。。
                ((ItemViewHolder) holder).state_text.setText("离线");
                ((ItemViewHolder) holder).alarm_state_text.setVisibility(View.GONE);
                ((ItemViewHolder) holder).state_iv.setImageResource(R.drawable.state_offline);
            } else {//设备在线。。
                ((ItemViewHolder) holder).state_text.setText("在线");
                ((ItemViewHolder) holder).alarm_state_text.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).state_iv.setImageResource(R.drawable.state_online);
            }
            final int alarmState = normalSmoke.getAlarmState();
            if (alarmState == 2) {//设备报警
                ((ItemViewHolder) holder).alarm_state_text.setText("报警");
            } else {//设备正常
                ((ItemViewHolder) holder).alarm_state_text.setText("正常");
            }
            final int eleState = normalSmoke.getEleState();
            //if(privilege==3){//@@8.28权限3有切换电源功能
            switch (eleState){
                case 1:
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).power_button.setImageResource(R.drawable.electr_on);
                    break;
                case 2:
                    ((ItemViewHolder) holder).power_button.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).power_button.setImageResource(R.drawable.electr_off);
                    break;
                default:
                    ((ItemViewHolder) holder).power_button.setVisibility(View.GONE);
                    break;
            }
            ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(MyApp.app.entity.getCut_electr()!=1){
                        Toast.makeText(MyApp.app,"您没有该权限",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(state==0){
                        Toast.makeText(MyApp.app,"设备不在线",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(eleState!=1){
                        changepower(2,normalSmoke);
                    }else{
                        changepower(1,normalSmoke);
                    }
                }
            });
            holder.itemView.setTag(normalSmoke);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.footViewItemTv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.footViewItemTv.setText("正在加载更多数据...");
                    break;
                case NO_MORE_DATA:
                    T.showShort(mContext, "没有更多数据");
                    footViewHolder.footer.setVisibility(View.GONE);
                    break;
                case NO_DATA:
                    footViewHolder.footer.setVisibility(View.GONE);
                    break;
            }
        }

    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    @Override
    public int getItemCount() {
        return listNormalSmoke.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.smoke_name_text)
//        TextView smoke_name_text;
        @Bind(R.id.mac_tv)
        TextView mac_tv;
//        @Bind(R.id.repeater_tv)
//        TextView repeater_tv;
//        @Bind(R.id.area_tv)
//        TextView area_tv;
//        @Bind(R.id.type_tv)
//        TextView type_tv;
//        @Bind(R.id.address_tv)
//        TextView address_tv;
        @Bind(R.id.state_text)
        TextView state_text;
        @Bind(R.id.alarm_state_text)
        TextView alarm_state_text;
        @Bind(R.id.manager_img)
        ImageView manager_img;
        @Bind(R.id.dev_name)
        TextView dev_name;
        @Bind(R.id.category_group_lin)
        LinearLayout category_group_lin;
        @Bind(R.id.power_button)
        ImageView power_button;//@@切换电源按钮
        @Bind(R.id.state_iv)
        ImageView state_iv;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.foot_view_item_tv)
        TextView footViewItemTv;
        @Bind(R.id.footer)
        LinearLayout footer;

        public FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //添加数据
    public void addItem(List<Electric> smokeList) {
        //mTitles.add(position, data);
        //notifyItemInserted(position);
        smokeList.addAll(listNormalSmoke);
        listNormalSmoke.removeAll(listNormalSmoke);
        listNormalSmoke.addAll(smokeList);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Electric> smokeList) {
        listNormalSmoke.addAll(smokeList);
        notifyDataSetChanged();
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    public void  changepower(final int eleState, final Electric normalSmoke){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_cut,null);
        TextView msg_tv = (TextView)dialogView.findViewById(R.id.msg_tv) ;
        if(eleState==1){
            msg_tv.setText("是否执行分闸命令？");
        }else{
            msg_tv.setText("是否执行合闸命令？");
        }
        builder.setView(dialogView);
        final Dialog dialog=builder.create();
        Button commit_btn = (Button) dialogView.findViewById(R.id.commit_btn);
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = SharedPreferencesManager.getInstance().getData(mContext,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.KEY_RECENTNAME);
                RequestQueue mQueue = Volley.newRequestQueue(mContext);
                String url="";
                if(normalSmoke.getDeviceType()==53){
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"EasyIot_Switch_control?devSerial="+normalSmoke.getMac()+"&eleState=2&appId=1&userId="+userID;
                    }else{
                        url=ConstantValues.SERVER_IP_NEW+"EasyIot_Switch_control?devSerial="+normalSmoke.getMac()+"&eleState=1&appId=1&userId="+userID;
                    }
                }else if(normalSmoke.getDeviceType()==75||normalSmoke.getDeviceType()==77){
                    String userid= SharedPreferencesManager.getInstance().getData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTNAME);
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?imei="+normalSmoke.getMac()+"&deviceType="+normalSmoke.getDeviceType()+"&devCmd=12&userid="+userid;
                    }else{
                        url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?imei="+normalSmoke.getMac()+"&deviceType="+normalSmoke.getDeviceType()+"&devCmd=13&userid="+userid;
                    }
                }else if(normalSmoke.getDeviceType()==6){
                    String userid= SharedPreferencesManager.getInstance().getData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTNAME);
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchDX?mac="+normalSmoke.getMac()+"&eleState=2&userId="+userid;
                    }else{
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchDX?mac="+normalSmoke.getMac()+"&eleState=1&userId="+userid;
                    }
                }else if(normalSmoke.getDeviceType()==7){
                    String userid= SharedPreferencesManager.getInstance().getData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTNAME);
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchSX?mac="+normalSmoke.getMac()+"&eleState=2&userId="+userid;
                    }else{
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchSX?mac="+normalSmoke.getMac()+"&eleState=1&userId="+userid;
                    }
                }else if(normalSmoke.getDeviceType()==8){
                    String userid= SharedPreferencesManager.getInstance().getData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTNAME);
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchBig?mac="+normalSmoke.getMac()+"&eleState=2&userId="+userid;
                    }else{
                        url= ConstantValues.SERVER_IP_NEW+"ackControlSwitchBig?mac="+normalSmoke.getMac()+"&eleState=1&userId="+userid;
                    }
                }else{
                    if(eleState==1){
                        url= ConstantValues.SERVER_IP_NEW+"ackControl?smokeMac="+normalSmoke.getMac()+"&eleState=2&userId="+userID;
                    }else{
                        url=ConstantValues.SERVER_IP_NEW+"ackControl?smokeMac="+normalSmoke.getMac()+"&eleState=1&userId="+userID;
                    }
                }
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                View dialogView = LayoutInflater.from(mContext)
                        .inflate(R.layout.dialog_cut_wait,null);
                TextView cut_tv = (TextView)dialogView.findViewById(R.id.cut_tv);
                ImageView cut_iv = (ImageView)dialogView.findViewById(R.id.cut_iv);
                if(eleState==1){
                    cut_tv.setText("分闸进行中");
                    cut_iv.setBackgroundResource(R.drawable.cut_off);
                }else{
                    cut_tv.setText("合闸进行中");
                    cut_iv.setBackgroundResource(R.drawable.cut_on);
                }
                builder1.setView(dialogView);
                final Dialog dialog1=builder1.create();

                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
//                Toast.makeText(mContext,"设置中，请稍候",Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject=new JSONObject(response);
                                    int errorCode=jsonObject.getInt("errorCode");
                                    if(errorCode==0){
                                        switch (eleState){
                                            case 2:
                                                normalSmoke.setEleState(1);
                                                break;
                                            case 1:
                                                normalSmoke.setEleState(2);
                                                break;
                                        }
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext,jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext,jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog1.dismiss();
                        Toast.makeText(mContext,"设置超时",Toast.LENGTH_SHORT).show();
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
//                        0,
//                        0.0f));
                mQueue.add(stringRequest);
                dialog.dismiss();
            }
        });

        Button cancel_btn = (Button) dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showNormalDialog(final String mac, final int deviceType, final int position){
        final android.app.AlertDialog.Builder normalDialog =
                new android.app.AlertDialog.Builder(mContext);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确认删除该设备?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url="";
                        String userid= SharedPreferencesManager.getInstance().getData(mContext,
                                SharedPreferencesManager.SP_FILE_GWELL,
                                SharedPreferencesManager.KEY_RECENTNAME);
                        switch (deviceType){
                            case 58:
                                url= ConstantValues.SERVER_IP_NEW+"deleteOneNetDevice?imei="+mac+"&userid="+userid;
                                break;
                            default:
                                url= ConstantValues.SERVER_IP_NEW+"deleteDeviceById?imei="+mac+"&userid="+userid;
                                break;
                        }
                        VolleyHelper.getInstance(mContext).getStringResponse(url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject=new JSONObject(response);
                                            int errorCode=jsonObject.getInt("errorCode");
                                            if(errorCode==0){
                                                listNormalSmoke.remove(position);
                                                notifyDataSetChanged();
                                                T.showShort(mContext,"删除成功");
                                            }else{
                                                T.showShort(mContext,"删除失败");
                                            }
                                            T.showShort(mContext,jsonObject.getString("error"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            T.showShort(mContext,"删除失败");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("TAG", error.getMessage(), error);
                                        T.showShort(mContext,"删除失败");
                                    }
                                });
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        Dialog dialog=normalDialog.create();
        dialog.show();
    }

}

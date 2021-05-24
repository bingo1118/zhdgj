package com.smart.cloud.fire.activity.AccountManage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class UserOfAreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int PULLUP_LOAD_MORE = 0;//上拉加载更多
    public static final int LOADING_MORE = 1;//正在加载中
    public static final int NO_MORE_DATA = 2;//正在加载中
    public static final int NO_DATA = 3;//无数据
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private Context mContext;
    private Area mArea;
    private List<AccountEntity> listNormalSmoke;

    public UserOfAreaAdapter(Context mContext, List<AccountEntity> listNormalSmoke,Area area) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.listNormalSmoke = listNormalSmoke;
        this.mArea=area;
    }

    public void changeDatas(List<AccountEntity> listNormalSmoke){
        this.listNormalSmoke = listNormalSmoke;
        notifyDataSetChanged();
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
            final View view = mInflater.inflate(R.layout.account_of_area_adapter_item, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            ItemViewHolder viewHolder = new ItemViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final AccountEntity normalSmoke = listNormalSmoke.get(position);

            ((ItemViewHolder) holder).name_tv.setText(normalSmoke.getUserName());
            ((ItemViewHolder) holder).phone_tv.setText("账号:" + normalSmoke.getUserId());//@@
            String gradeString = "普通用户";
            switch (normalSmoke.getGradeOfArea()) {
                case 1:
                    gradeString = "二级管理员";
                    break;
                case 2:
                    gradeString = "普通用户";
                    break;
            }
            ((ItemViewHolder) holder).grade_tv.setText("角色:" + gradeString);
            ((ItemViewHolder) holder).cut_tv.setText("切电:" + (normalSmoke.getCut_electr() == 0 ? "关闭" : "开启"));
            ((ItemViewHolder) holder).add_tv.setText("充电:" + (normalSmoke.getAdd_electr() == 0 ? "关闭" : "开启"));
            ((ItemViewHolder) holder).txt_tv.setText("短信:" + (normalSmoke.getIstxt() == 0 ? "关闭" : "开启"));


            ((ItemViewHolder) holder).more_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v, normalSmoke);
                }
            });
            holder.itemView.setTag(position);
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
        @Bind(R.id.category_group_lin)
        LinearLayout category_group_lin;
        @Bind(R.id.name_tv)
        TextView name_tv;
        @Bind(R.id.phone_tv)
        TextView phone_tv;
        @Bind(R.id.grade_tv)
        TextView grade_tv;
        @Bind(R.id.cut_tv)
        TextView cut_tv;
        @Bind(R.id.add_tv)
        TextView add_tv;
        @Bind(R.id.txt_tv)
        TextView txt_tv;
        @Bind(R.id.more_iv)
        ImageView more_iv;

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

    private void showPopupMenu(final View view, final AccountEntity entity) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_account_of_area, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.delete:
                        unbindArea(mArea,entity);
                        break;
                    case R.id.reset:
                        resetAccount(entity);
                        break;
                    case R.id.set_role:
                        setRole(entity);
                        break;
                    case R.id.area:
                        intent=new Intent(mContext,AreaListActivity.class);
                        intent.putExtra("account",entity);
                        mContext.startActivity(intent);
                        break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    private void setRole(final AccountEntity entity) {
        if(!(MyApp.entity.getGrade()<2||isManegerOfArea(MyApp.entity.getUserId()))){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.account_set_role_view, null);
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg);
        final RadioButton manager_rb = (RadioButton) view.findViewById(R.id.manager_rb);
        final RadioButton normol_rb = (RadioButton) view.findViewById(R.id.normol_rb);
        if (entity.getGradeOfArea() == 1) {
            rg.check(R.id.manager_rb);
        }else{
            rg.check(R.id.normol_rb);
        }

        Button commit_btn = (Button) view.findViewById(R.id.commit);


        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = ConstantValues.SERVER_IP_NEW + "setAccountRole?userId=" + entity.getUserId()
                        + "&areaId=" + mArea.getAreaId()
                        + "&grade=" + (manager_rb.isChecked()?"1":"2");

                VolleyHelper helper = VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int code = response.getInt("errorCode");
                                    if (code == 0) {
                                        T.showShort(mContext, "成功");
                                        entity.setGradeOfArea(manager_rb.isChecked()?1:2);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, "失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext, "网络错误");
                        dialog.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });
    }

    private void unbindArea(final Area entity,final AccountEntity mAccount) {
        if(!(MyApp.entity.getGrade()<2||isManegerOfArea(MyApp.entity.getUserId()))){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定将该账号移出该区域?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = ConstantValues.SERVER_IP_NEW + "unbindArea?userId=" + mAccount.getUserId()
                        + "&areaId=" + entity.getAreaId();

                VolleyHelper helper = VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int code = response.getInt("errorCode");
                                    if (code == 0) {
                                        T.showShort(mContext, "成功");
                                        listNormalSmoke.remove(mAccount);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, "失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext, "网络错误");
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });
        builder.show();
    }

    private boolean isManegerOfArea(String userId) {
        boolean result = false;
        for(int i=0;i<listNormalSmoke.size();i++){
            if(listNormalSmoke.get(i).getUserId()==userId){
                if(listNormalSmoke.get(i).getGradeOfArea()==1){
                    result=true;
                    break;
                }
            }
        }
        return result;
    }

    private void resetAccount(final AccountEntity entity) {
        if(!(MyApp.entity.getGrade()<2||isManegerOfArea(MyApp.entity.getUserId()))){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.account_reset_view, null);
        final EditText name_et = (EditText) view.findViewById(R.id.name_et);
        name_et.setText(entity.getUserName());
        final EditText phone_et = (EditText) view.findViewById(R.id.phone_et);
        phone_et.setText(entity.getUserId());
        EditText grade_et = (EditText) view.findViewById(R.id.grade_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);
        int grade = entity.getGrade();
        String gradeString = "个人账号";
        switch (grade) {
            case 0:
                gradeString = "超级账号";
                break;
            case 1:
                gradeString = "一级账号";
                break;
            case 2:
                gradeString = "二级账号";
                break;
            case 3:
                gradeString = "三级账号";
                break;
            case 4:
                gradeString = "个人账号";
                break;


        }
        grade_et.setText(gradeString);
        final Switch add_enable = (Switch) view.findViewById(R.id.add_enable);
        add_enable.setChecked(entity.getAdd_electr() == 1);

        final Switch cut_enable = (Switch) view.findViewById(R.id.cut_enable);
        cut_enable.setChecked(entity.getCut_electr() == 1);

        final Switch txt_enable = (Switch) view.findViewById(R.id.txt_enable);
        txt_enable.setChecked(entity.getIstxt() == 1);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int cut = cut_enable.isChecked() ? 1 : 0;
                final int add = add_enable.isChecked() ? 1 : 0;
                final int txt = txt_enable.isChecked() ? 1 : 0;

                String url = ConstantValues.SERVER_IP_NEW + "resetSubAccount?userId=" + phone_et.getText().toString()
                        + "&name=" + name_et.getText().toString()
                        + "&grade=&cut=" + cut
                        + "&add=" + add
                        + "&txt=" + txt;

                VolleyHelper helper = VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int code = response.getInt("errorCode");
                                    if (code == 0) {
                                        T.showShort(mContext, "成功");
                                        entity.setUserName(name_et.getText().toString());
                                        entity.setCut_electr(cut);
                                        entity.setAdd_electr(add);
                                        entity.setIstxt(txt);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, "失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext, "网络错误");
                        dialog.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });
    }
}



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
import android.widget.RelativeLayout;
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
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.dataSelector.GetParentTeamDataSelectorView;
import com.smart.cloud.fire.view.dataSelector.GetTeamDataSelectorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class OwnAreaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int PULLUP_LOAD_MORE = 0;//上拉加载更多
    public static final int LOADING_MORE = 1;//正在加载中
    public static final int NO_MORE_DATA = 2;//正在加载中
    public static final int NO_DATA = 3;//无数据
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Area> listNormalSmoke;
    private AccountEntity mAccount;

    public OwnAreaListAdapter(Context mContext, List<Area> listNormalSmoke,AccountEntity mAccount) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.listNormalSmoke = listNormalSmoke;
        this.mAccount=mAccount;
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
            final View view = mInflater.inflate(R.layout.arealist_adapter_item, parent, false);
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
            final Area mArea = listNormalSmoke.get(position);
            ((ItemViewHolder) holder).name_tv.setText(mArea.getAreaName());
            ((ItemViewHolder) holder).more_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v, mArea);
                }
            });
            ((ItemViewHolder) holder).rela.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext,UserOfAreaActivity.class);
                    intent.putExtra("area",mArea);
                    mContext.startActivity(intent);
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
        @Bind(R.id.name_tv)
        TextView name_tv;
        @Bind(R.id.rb)
        RadioButton rb;
        @Bind(R.id.more_iv)
        ImageView more_iv;
        @Bind(R.id.rela)
        LinearLayout rela;

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

    private void showPopupMenu(final View view, final Area entity) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_own_area, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.user:
                        if(MyApp.entity.getGrade()>1){
                            T.showShort(mContext,"您没有该权限");
                            break;
                        }
                        intent=new Intent(mContext,UserOfAreaActivity.class);
                        intent.putExtra("area",entity);
                        mContext.startActivity(intent);
                        break;
                    case R.id.delete:
                        deleteArea(entity);
                        break;
                    case R.id.rename:
                        renameArea(entity);
                        break;
                    case R.id.move:
                        moveArea(entity);
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

    private void moveArea(final Area entity) {
        if(MyApp.entity.getGrade()>1){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.move_area_view, null);
        final GetParentTeamDataSelectorView selectorView = (GetParentTeamDataSelectorView) view.findViewById(R.id.selector);
        Button commit_btn = (Button) view.findViewById(R.id.commit);

        final Dialog dialog = builder.setView(view).create();
        selectorView.setRootView(dialog.getWindow().getDecorView());
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utils.isNullString(selectorView.getCheckedModel().getModelName())){
                    T.showShort(mContext,"请先选择");
                    return;
                }
                final String p_areaid = selectorView.getCheckedModel().getModelId();

                String url = ConstantValues.SERVER_IP_NEW + "moveArea?userId=" + mAccount.getUserId()
                        + "&parentAreaId=" + p_areaid
                        +"&areaId=" + entity.getAreaId();


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
                                        dialog.dismiss();
                                        listNormalSmoke.remove(entity);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, response.getString("error"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    private void renameArea(final Area entity) {
        if(MyApp.entity.getGrade()>1){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.rename_area_view, null);
        final EditText name_et = (EditText) view.findViewById(R.id.name_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name=name_et.getText().toString();
                if(Utils.isNullString(name)){
                    T.showShort(mContext,"请先完善信息");
                    return;
                }

                String url = ConstantValues.SERVER_IP_NEW + "renameArea?userId=" + mAccount.getUserId()
                        + "&areaName=" + name
                        +"&areaId=" + entity.getAreaId();


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
                                        dialog.dismiss();
                                        entity.setAreaName(name);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, response.getString("error"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    private void deleteArea(final Area entity) {
        if(MyApp.entity.getGrade()>1){
            T.showShort(mContext,"您没有该权限");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定删除该区域?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = ConstantValues.SERVER_IP_NEW + "deleteArea?userId=" + mAccount.getUserId()
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
                                        T.showShort(mContext, "删除成功");
                                        listNormalSmoke.remove(entity);
                                        notifyDataSetChanged();
                                    } else {
                                        T.showShort(mContext, "删除失败");
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

}



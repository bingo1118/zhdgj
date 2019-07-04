package com.smart.cloud.fire.mvp.ElectrTimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2019/6/29.
 */
public class ElectrTimerTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<TimerTaskEntity> taskList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private ElectrTimerTaskPresenter mPresenter;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(TimerTaskEntity)v.getTag());
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , TimerTaskEntity data);
    }

    public void setTaskList(List<TimerTaskEntity> list){
        this.taskList=list;
        notifyDataSetChanged();
    }

    public ElectrTimerTaskAdapter(Context mContext, List<TimerTaskEntity> list,ElectrTimerTaskPresenter presenter) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.taskList = list;
        this.mPresenter=presenter;
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
            View view = mInflater.inflate(R.layout.timer_task_item, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            ItemViewHolder viewHolder = new ItemViewHolder(view);
            view.setOnClickListener(this);
            return viewHolder;
    }


    /**
     * 数据的绑定显示
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final TimerTaskEntity timerTask = taskList.get(position);
        String cycle=timerTask.getIsCycle();
        String cycle_text = "";
        if(cycle.equals("8")){
            cycle_text="每天";
        }else if(cycle.equals("0")){
            cycle_text="单次";
        }else{
            for (int i=0;i<cycle.length();i++){
                char week=cycle.charAt(i);
                switch (week){
                    case '1':
                        cycle_text+="周一 ";
                        break;
                    case '2':
                        cycle_text+="周二 ";
                        break;
                    case '3':
                        cycle_text+="周三 ";
                        break;
                    case '4':
                        cycle_text+="周四 ";
                        break;
                    case '5':
                        cycle_text+="周五 ";
                        break;
                    case '6':
                        cycle_text+="周六 ";
                        break;
                    case '7':
                        cycle_text+="周日 ";
                        break;
                }
            }
        }
        ((ItemViewHolder) holder).time_tv.setText(timerTask.getDate());
        ((ItemViewHolder) holder).cycle_tv.setText(cycle_text);
        ((ItemViewHolder) holder).state_tv.setText(timerTask.getState()==1?"合闸":"切断");
        ((ItemViewHolder) holder).power_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
                builder.setTitle("提示" ) ;
                builder.setMessage("是否确认删除该定时任务？" ) ;
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.removeElectrTimer(timerTask.getId(),position);
                    }
                });
                builder.setNegativeButton("否", null);
                builder.show();
            }
        });
        holder.itemView.setTag(timerTask.getId());
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time)
        TextView time_tv;
        @Bind(R.id.cycle)
        TextView cycle_tv;
        @Bind(R.id.state)
        TextView state_tv;

        @Bind(R.id.power_button)
        RelativeLayout power_button;


        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}

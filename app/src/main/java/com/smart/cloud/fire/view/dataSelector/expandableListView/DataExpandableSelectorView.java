package com.smart.cloud.fire.view.dataSelector.expandableListView;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.view.dataSelector.BingoViewModel;

import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

public abstract class DataExpandableSelectorView extends LinearLayout {

    public TextView editText;
    private ImageView imageView;
    private PopupWindow popupWindow = null;
    public ArrayList<Area> dataList;
    private Context mContext;
    ProgressBar loading_prg_monitor;
    private ImageView clear_choice;

    private View rootView;//根布局

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    enum Status{
        INIT,
        LOADDATA,
        LOADCOMPELE,
        CHECKED,
        CLEARED
    }
    Status state= Status.INIT;

    BingoViewModel checkedModel;

    public BingoViewModel getCheckedModel() {
        return checkedModel;
    }


    Activity a;

    public void setActivity(Activity a) {
        this.a = a;
    }

    public DataExpandableSelectorView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public DataExpandableSelectorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public DataExpandableSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    public void setHint(String s){
        editText.setHint(s);
    }


    public void initView(Context context) {
        mContext = context;
        if(mContext instanceof AppCompatActivity){
            a=(Activity)mContext;
        }
        LayoutInflater.from(mContext).inflate(R.layout.expandable_data_selector_view, this, true);
        loading_prg_monitor = (ProgressBar) findViewById(R.id.loading_prg_monitor);
        editText = (TextView) findViewById(R.id.text);
        imageView = (ImageView) findViewById(R.id.btn);
        editText.setHint("请选择");
        clear_choice = (ImageView) findViewById(R.id.clear_choice);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList == null) {
                    if(state== Status.INIT){
                        getdata();
                    }
                } else {
                    showPopWindow();
                }
            }
        });
        clear_choice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(Status.CLEARED,null);
            }
        });
        changeStatus(Status.LOADDATA,null);
        getdata();
    }

    public abstract void getdata();

    public void getDataSuccesss(){
        changeStatus(Status.LOADCOMPELE,null);
    }

    public void getDataFail(){
        changeStatus(Status.INIT,null);
    }



    /**
     * 打开下拉列表弹窗
     */
    public void showPopWindow() {
        if(dataList==null||dataList.size()==0){
            T.showShort(mContext,"无选项数据");
            return;
        }
        backgroundAlpha(0.5f);
        // 加载popupWindow的布局文件
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.expandable_data_selector_popwin, null, false);
        TextView title_tv = contentView.findViewById(R.id.title_tv);
        ExpandableListView listView = (ExpandableListView) contentView.findViewById(R.id.listView);
        MyExpandableAdapter mAdapter = new MyExpandableAdapter(getContext(), dataList,this);
        listView.setGroupIndicator(null);

        listView.setAdapter(mAdapter);
        if(rootView!=null){
            popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            title_tv.setVisibility(GONE);
        }else{
            popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, 1000);
            title_tv.setVisibility(VISIBLE);
        }


        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if(rootView!=null){
            popupWindow.showAsDropDown(this);
        }else{
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    /**
     * 关闭下拉列表弹窗
     */
    public void closePopWindow() {
        backgroundAlpha(1f);
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
        popupWindow = null;
    }

    //@@12.20
    public void backgroundAlpha(float bgAlpha) {
//        if (a == null) {
//            return;
//        }
//        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
//        lp.alpha = bgAlpha; //0.0-1.0
//        a.getWindow().setAttributes(lp);
    }

    /**
     * 数据适配器
     *
     * @author caizhiming
     */
    class ListAdapter extends BaseAdapter {

        Context mContext;
        List<BingoViewModel> mData;
        LayoutInflater inflater;

        public ListAdapter(Context ctx, List<BingoViewModel> data) {
            mContext = ctx;
            mData = data;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = inflater.inflate(R.layout.dropdown_list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.tv);
            final BingoViewModel object = mData.get(position);
            tv.setText(object.getModelName());
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    changeStatus(Status.CHECKED,object);
                }
            });
            return convertView;
        }

    }

    public void changeStatus(Status status,BingoViewModel model){
        state=status;
        checkedModel=model;
        switch (status){
            case INIT:
                clear_choice.setVisibility(GONE);
                loading_prg_monitor.setVisibility(GONE);
                imageView.setVisibility(GONE);
                editText.setText("");
                break;
            case LOADDATA:
                clear_choice.setVisibility(GONE);
                loading_prg_monitor.setVisibility(VISIBLE);
                imageView.setVisibility(GONE);
                editText.setText("");
                break;
            case LOADCOMPELE:
                clear_choice.setVisibility(GONE);
                loading_prg_monitor.setVisibility(GONE);
                imageView.setVisibility(VISIBLE);
                editText.setText("");
                break;
            case CHECKED:
                clear_choice.setVisibility(VISIBLE);
                loading_prg_monitor.setVisibility(GONE);
                imageView.setVisibility(GONE);
                editText.setText(model.getModelName());
                closePopWindow();
                break;
            case CLEARED:
                clear_choice.setVisibility(GONE);
                loading_prg_monitor.setVisibility(GONE);
                imageView.setVisibility(VISIBLE);
                editText.setText("");
                break;
        }
    };
}

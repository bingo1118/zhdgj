package com.smart.cloud.fire.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.cloud.fire.activity.AddNFC.NFCDeviceType;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ShopType;

import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class XCDropDownListView extends LinearLayout {

    private BasePresenter basePresenter;
    private TextView editText;
    private ImageView imageView;
    private PopupWindow popupWindow = null;
    private List<Object> dataList =  new ArrayList<>();
    private View mView;
    private Context mContext;
    ProgressBar loading_prg_monitor;
    private RelativeLayout clear_choice;

    public XCDropDownListView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    public XCDropDownListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }
    public XCDropDownListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView();
    }

    public void initView(){
        mContext = getContext();
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater =  (LayoutInflater) getContext().getSystemService(infServie);
        View view  = layoutInflater.inflate(R.layout.dropdownlist_view, this,true);
        loading_prg_monitor = (ProgressBar) findViewById(R.id.loading_prg_monitor);
        editText= (TextView)findViewById(R.id.text);
        imageView = (ImageView)findViewById(R.id.btn);
        clear_choice = (RelativeLayout) findViewById(R.id.clear_choice);
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popupWindow == null) {
                    showPopWindow();
                } else {
                    closePopWindow();
                }
            }
        });
        clear_choice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList.size() > 0) {
                    Object object = dataList.get(0);
                    if (object instanceof Area) {
                        basePresenter.getArea(new Area());
                    }
                    if (object instanceof ShopType) {
                        basePresenter.getShop(new ShopType());
                    }
                    imageView.setVisibility(View.VISIBLE);
                    clear_choice.setVisibility(View.GONE);
                    editText.setText("");
                }
            }
        });
    }

    public void showLoading(){
        imageView.setVisibility(View.GONE);
        loading_prg_monitor.setVisibility(View.VISIBLE);
    }

    public void closeLoading(){
        loading_prg_monitor.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }
    /**
     * 打开下拉列表弹窗
     */
    public void showPopWindow() {
        // 加载popupWindow的布局文件
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater =  (LayoutInflater) getContext().getSystemService(infServie);
        View contentView  = layoutInflater.inflate(R.layout.dropdownlist_popupwindow, null,false);
        ListView listView = (ListView)contentView.findViewById(R.id.listView);

        listView.setAdapter(new XCDropDownListAdapter(getContext(), dataList));
        popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this);
    }
    /**
     * 关闭下拉列表弹窗
     */
    public void closePopWindow(){
        popupWindow.dismiss();
        popupWindow = null;
    }

    public boolean ifShow(){
        if (popupWindow == null) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 设置数据
     * @param list
     */
    public void setItemsData(List<Object> list, BasePresenter basePresenter){
        dataList = list;
        editText.setText("");
        this.basePresenter = basePresenter;
    }

    public void setEditTextData(String editTextData){
        editText.setText(editTextData);
    }
    /**
     * 数据适配器
     * @author caizhiming
     *
     */
    class XCDropDownListAdapter extends BaseAdapter {

        Context mContext;
        List<Object> mData;
        LayoutInflater inflater;
        public XCDropDownListAdapter(Context ctx,List<Object> data){
            mContext  = ctx;
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
            // 自定义视图
            ListItemView listItemView = null;
            if (convertView == null) {
                // 获取list_item布局文件的视图
                convertView = inflater.inflate(R.layout.dropdown_list_item, null);

                listItemView = new ListItemView();
                // 获取控件对象
                listItemView.tv = (TextView) convertView
                        .findViewById(R.id.tv);
                listItemView.layout = (LinearLayout) convertView.findViewById(R.id.layout_container);
                // 设置控件集到convertView
                convertView.setTag(listItemView);
            } else {
                listItemView = (ListItemView) convertView.getTag();
            }
            Object object = mData.get(position);
            if(object instanceof Area){
                final Area mArea = (Area)object;
                // 设置数据
                listItemView.tv.setText(mArea.getAreaName());
                final String text = mArea.getAreaName();
                listItemView.layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        editText.setText(text);
                        imageView.setVisibility(View.GONE);
                        clear_choice.setVisibility(View.VISIBLE);
                        basePresenter.getArea(mArea);
                        closePopWindow();
                    }
                });
            }else if(object instanceof NFCDeviceType){
                final NFCDeviceType nfcDeviceType = (NFCDeviceType)object;
                // 设置数据
                listItemView.tv.setText(nfcDeviceType.getPlaceTypeName());
                final String text = nfcDeviceType.getPlaceTypeName();
                listItemView.layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        editText.setText(text);
                        imageView.setVisibility(View.GONE);
                        clear_choice.setVisibility(View.VISIBLE);
                        basePresenter.getNFCDeviceType(nfcDeviceType);
                        closePopWindow();
                    }
                });
            }else{
                final ShopType mShopType = (ShopType)object;
                // 设置数据
                listItemView.tv.setText(mShopType.getPlaceTypeName());
                final String text = mShopType.getPlaceTypeName();
                listItemView.layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        editText.setText(text);
                        imageView.setVisibility(View.GONE);
                        clear_choice.setVisibility(View.VISIBLE);
                        basePresenter.getShop(mShopType);
                        closePopWindow();
                    }
                });
            }
            return convertView;
        }

    }
    private static class ListItemView{
        TextView tv;
        LinearLayout layout;
    }

    public void addFinish(){
        imageView.setVisibility(View.VISIBLE);
        clear_choice.setVisibility(View.GONE);
    }

    public void setEditTextHint(String textStr){
        editText.setHint(textStr);
    }

}

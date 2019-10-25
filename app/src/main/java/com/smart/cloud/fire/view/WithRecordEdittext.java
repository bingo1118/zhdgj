package com.smart.cloud.fire.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smart.cloud.fire.utils.ListDataSave;

import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2019/10/12.
 */
public class WithRecordEdittext extends EditText{

    private PopupWindow popWnd;
    private Context context;
    private ListView listview;
    private TextView clear_history;
    List<String> data = new ArrayList<>();

    public WithRecordEdittext(Context context) {
        super(context);
        initView(context);
    }

    public WithRecordEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public WithRecordEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WithRecordEdittext(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context c) {
        this.context=c;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(context);
            }
        });
    }


    private void showPopupWindow(final Context context) {
        data=getData(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwin_with_record_edittext, null);
        if(popWnd==null){
            popWnd = new PopupWindow(context);
        }
        if(popWnd.isShowing()){
            return;
        }
        popWnd.setContentView(contentView);
        popWnd.setWidth(500);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setBackgroundDrawable(null);
        popWnd.setOutsideTouchable(true);
//        popWnd.setAnimationStyle(R.style.contextMenuAnim);
        clear_history=(TextView) contentView.findViewById(R.id.clear_history);
        clear_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               ListDataSave.getInstance(context).setDataList("list",new ArrayList<String>());
                popWnd.dismiss();
            }
        });
        listview=(ListView) contentView.findViewById(R.id.listView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setText(data.get(position));
                setSelection(data.get(position).length());
                popWnd.dismiss();
            }
        });
        listview.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,data));
        //相对某个控件的位置（正左下方），无偏移
        if(data.size()>0){
            popWnd.showAsDropDown(this);
        }
    }

    private List<String> getData(Context context) {
        return ListDataSave.getInstance(context).getDataList();
    }
}

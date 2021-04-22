package com.smart.cloud.fire.view.dataSelector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Spinner;

import java.util.List;

@SuppressLint("AppCompatCustomView")
public class DataSelectorSpinner extends Spinner {

    List<BingoViewModel> mList;

    public DataSelectorSpinner(Context context) {
        super(context);
        initView();
    }

    private void initView() {

    }


}

package com.smart.cloud.fire.view.dataSelector.expandableListView;

import android.content.Context;
import android.util.AttributeSet;

import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.view.dataSelector.BingoViewModel;
import com.smart.cloud.fire.view.dataSelector.DataSelectorView;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;

public class AreaDataExpandableSelectorView extends DataExpandableSelectorView {

    public AreaDataExpandableSelectorView(Context context) {
        super(context);
    }

    public AreaDataExpandableSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AreaDataExpandableSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        getPlaceTypeId();
    }

    public void setCheckedModel(BingoViewModel checkedModel) {
        this.checkedModel = checkedModel;
        changeStatus(DataExpandableSelectorView.Status.CHECKED,checkedModel);
    }


    public void getPlaceTypeId() {
        Observable mObservable = BasePresenter.apiStores1.getAreaInfo(MyApp.getUserID(),MyApp.getPrivilege()+"","").map(new Func1<HttpAreaResult,ArrayList<Area>>() {
            @Override
            public ArrayList<Area> call(HttpAreaResult o) {
                return o.getAreas();
            }
        });


        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<ArrayList<Area>>() {
            @Override
            public void onSuccess(ArrayList<Area> model) {
                if(model!=null&&model.size()!=0){
                    dataList=model;
                    getDataSuccesss();
                }else{
                    getDataFail();
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                getDataFail();
            }

            @Override
            public void onCompleted() {
            }
        }));
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("单位");
    }
}

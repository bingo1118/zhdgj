package com.smart.cloud.fire.mvp.ElectrTimerTask;

import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;

import java.util.List;

import rx.Observable;

/**
 * Created by Rain on 2019/6/29.
 */
public class ElectrTimerTaskPresenter extends BasePresenter<ElectrTimerTaskView>{

    public ElectrTimerTaskPresenter(ElectrTimerTaskView view) {
        attachView(view);
    }

    public void getAllTimerTask(String smokeMac){
        Observable mObservable = apiStores1.getAllTimerTask(smokeMac);
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                int result=model.getErrorCode();
                if(result==0){
                    List<TimerTaskEntity> smokeList = model.getTasks();
                    mvpView.getDataSuccess(smokeList,false);
                }else{
                    mvpView.getDataFail("无数据");
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                mvpView.getDataFail("网络错误");
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }

    public void removeElectrTimer(String id){
        Observable mObservable = apiStores1.removeElectrTimer(id);
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                int result=model.getErrorCode();
                mvpView.getDataFail(model.getError());

            }
            @Override
            public void onFailure(int code, String msg) {
                mvpView.getDataFail("网络错误");
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }
}

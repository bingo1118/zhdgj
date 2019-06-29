package com.smart.cloud.fire.mvp.ElectrTimerTask;

import java.util.List;

/**
 * Created by Rain on 2019/6/29.
 */
public interface ElectrTimerTaskView {
    void getDataSuccess(List<?> smokeList, boolean research);

    void getDataFail(String msg);

    void showLoading();

    void hideLoading();
}

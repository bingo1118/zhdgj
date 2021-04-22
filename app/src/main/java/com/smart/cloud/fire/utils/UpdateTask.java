package com.smart.cloud.fire.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.smart.cloud.fire.global.MainThread;

public class UpdateTask extends AsyncTask<Context, Integer, Integer> {

    @Override
    protected Integer doInBackground(Context... params) {
        // TODO Auto-generated method stub\
        Context context = params[0];
        long ll = -2;
        int result = new MainThread(context).checkUpdate(ll);
        return result;
    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
    }
}

package com.pd.a2.vkdialog;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pd.a2.vkdialog.model.MailItem;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class VKWorker {
    AppCompatActivity currentActivity;
    VKRequest request;
    MyVKRequestListener requestListener;
    public VKWorker(AppCompatActivity currentActivity){
        this.currentActivity = currentActivity;
    }

    public void refreshMailList() {
        MailItem mailItem = new MailItem();
        Queue<VKRequest> requestQueue = new LinkedList<VKRequest>();
        Queue<MyVKRequestListener> listenerQueue = new LinkedList<MyVKRequestListener>();
        Log.i("MYLOG_VKWorker", "getMessages. Starting request");
        requestListener = new MyVKRequestListener(RequestOperations.GET_ALL_DIALOGS,
                new ArrayList<MailItem>(), this);
        request = VKApi.messages().getDialogs(VKParameters.from("preview_length, 20"));
        request.executeWithListener(requestListener);
        Log.i("MYLOG_VKWorker", "getMessages. Elnd of the method");
    }


    public void setChangedData(ArrayList<MailItem> mailItemList) {
        Log.i("MYLOG_VKWorker", "setChangedData.Size: " + mailItemList.size());
        ((MainActivity)currentActivity).setMailItemsList(mailItemList);
        ((MainActivity)currentActivity).notifyDataSetChanged();
    }
    public void notifyDataSetChanged() {
        //((MainActivity)currentActivity).notifyDataSetChanged();
    }

}

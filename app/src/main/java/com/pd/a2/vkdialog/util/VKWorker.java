package com.pd.a2.vkdialog.util;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pd.a2.vkdialog.listener.MyVKRequestListener;
import com.pd.a2.vkdialog.activity.MainActivity;
import com.pd.a2.vkdialog.listener.OnFetchDataListener;
import com.pd.a2.vkdialog.model.MailItem;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class VKWorker {
    VKRequest request;
    MyVKRequestListener requestListener;
    OnFetchDataListener onFetchDataListener;

    public VKWorker(){}

    public void refreshMailList() {
        MailItem mailItem = new MailItem();
        Queue<VKRequest> requestQueue = new LinkedList<VKRequest>();
        Queue<MyVKRequestListener> listenerQueue = new LinkedList<MyVKRequestListener>();
        Log.i("MYLOG_VKWorker", "getMessages. Starting request");
        requestListener = new MyVKRequestListener(RequestOperations.GET_ALL_DIALOGS,
                new ArrayList<MailItem>(), this);
        request = VKApi.messages().getDialogs(VKParameters.from("preview_length", "15", "count", "50"));
        request.executeWithListener(requestListener);
        Log.i("MYLOG_VKWorker", "getMessages. End of the method");
    }


    public void setChangedData(ArrayList<MailItem> mailItemList) {
        Log.i("MYLOG_VKWorker", "setChangedData.Size: " + mailItemList.size());
        if(onFetchDataListener != null) {
            onFetchDataListener.onDataFetched(mailItemList);
        }
    }

    public void setOnFetchDataListener(OnFetchDataListener onFetchDataListener) {
        this.onFetchDataListener = onFetchDataListener;
    }
}

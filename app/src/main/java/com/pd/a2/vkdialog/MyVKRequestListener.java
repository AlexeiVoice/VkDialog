package com.pd.a2.vkdialog;

import android.util.Log;

import com.pd.a2.vkdialog.model.MailItem;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MyVKRequestListener extends VKRequest.VKRequestListener {

    RequestOperations operation;
    MailItem mailItem;
    ArrayList<MailItem> mailItemList;
    Queue<VKRequest> requestQueue;
    Queue<MyVKRequestListener> listenerQueue;
    VKWorker vkWorker;
    private boolean jobIsDone;

    public MyVKRequestListener(RequestOperations operation, MailItem mailItem,
                               ArrayList<MailItem> mailItemList, Queue<VKRequest> requestQueue,
                               Queue<MyVKRequestListener> listenerQueue, VKWorker vkWorker) {
        this.operation = operation;
        this.mailItem = mailItem;
        this.mailItemList = mailItemList;
        this.jobIsDone = false;
        this.requestQueue = requestQueue;
        this.listenerQueue = listenerQueue;
        this.vkWorker = vkWorker;
    }

    @Override
    public void onComplete(VKResponse response) {
        switch (operation){
            case GET_ALL_DIALOGS:
                Log.i("MYLOG_MyRequestListener", "getDialogs request's executed ");
                VKApiGetDialogResponse dialogResponse = (VKApiGetDialogResponse)response.parsedModel;
                VKList<VKApiDialog> dialogs = dialogResponse.items;
                VKRequest request_next;
                VKApiDialog singleDialog;
                VKApiMessage lastMessFromDial;
                MyVKRequestListener listener_next;
                for(int i = 0; i < dialogs.size(); i++) {
                    singleDialog = dialogs.get(i);
                    lastMessFromDial = singleDialog.message;
                    //Now let's set info that we have: time and message body
                    MailItem nextMailItem = new MailItem();
                    nextMailItem.setDateTime(lastMessFromDial.date);
                    nextMailItem.setMessageBody(lastMessFromDial.body);
                    //Next we need to retrieve user in order to get userPic addr and userName
                    request_next = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                            lastMessFromDial.user_id));
                    //Add request and corresponding listener to the queue
                    requestQueue.add(request_next);
                    listener_next = new MyVKRequestListener(RequestOperations.GET_USER,
                            nextMailItem, mailItemList, requestQueue, listenerQueue, vkWorker);
                    listenerQueue.add(listener_next);
                    //request_next.executeWithListener(listener_next);
                }
                //Start the emptying the queue
                if(!requestQueue.isEmpty() && !listenerQueue.isEmpty()){
                    requestQueue.poll().executeWithListener(listenerQueue.poll());
                }
                break;
            case GET_USER:
                Log.i("MYLOG_MyRequestListener", "getUser request's executed ");
                VKList<VKApiUser> usersList = (VKList<VKApiUser>)response.parsedModel;
                VKApiUser usr = usersList.get(0);
                //Now we can set what information we missed until now
                mailItem.setUserName(usr.first_name);
                mailItem.setUserPicURL(usr.photo_50);
                addMessage(mailItem);
                 if(!requestQueue.isEmpty() && !listenerQueue.isEmpty()){
                     Log.i("MYLOG_MyRequestListener", "requests left: " + requestQueue.size() +
                     "listeners left: " + listenerQueue.size());
                    requestQueue.poll().executeWithListener(listenerQueue.poll());
                } else{
                    //All requests were executed. Now we need to inform our View
                     Log.i("MYLOG_MyRequestListener", "all getUser requests are executed ");
                    vkWorker.setChangedData(mailItemList);
                 }
                break;
            default:
                super.onComplete(response);
                break;
        }
    }

    @Override
    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        super.attemptFailed(request, attemptNumber, totalAttempts);
        switch (operation){
            case GET_ALL_DIALOGS:
               break;
            case GET_USER:
                Log.i("MYLOG_MyRequestListener", "getUser request's attemptFailed ");
                if(!requestQueue.isEmpty() && !listenerQueue.isEmpty()){
                    Log.i("MYLOG_MyRequestListener", "requests left: " + requestQueue.size() +
                            "listeners left: " + listenerQueue.size());
                    requestQueue.poll().executeWithListener(listenerQueue.poll());
                } else{
                    //All requests were executed. Now we need to inform our View
                    Log.i("MYLOG_MyRequestListener", "all getUser requests are executed ");
                    vkWorker.setChangedData(mailItemList);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(VKError error) {
        super.onError(error);
        switch (operation){
            case GET_ALL_DIALOGS:
                break;
            case GET_USER:
                Log.i("MYLOG_MyRequestListener", "getUser request's error ");
                if(!requestQueue.isEmpty() && !listenerQueue.isEmpty()){
                    Log.i("MYLOG_MyRequestListener", "requests left: " + requestQueue.size() +
                            "listeners left: " + listenerQueue.size());
                    requestQueue.poll().executeWithListener(listenerQueue.poll());
                } else{
                    //All requests were executed. Now we need to inform our View
                    Log.i("MYLOG_MyRequestListener", "all getUser requests are executed ");
                    vkWorker.setChangedData(mailItemList);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Add mailItem to the mailItem list
     * @param mailItem
     */
    public void addMessage(MailItem mailItem) {
        Log.i("MYLOG_MyRequestListener", "addMessage(): " + mailItem.getUserName());
        mailItemList.add(mailItem);
    }
}

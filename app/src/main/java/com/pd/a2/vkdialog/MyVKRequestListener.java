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
    ArrayList<MailItem> mailItemList;
    VKWorker vkWorker;
    private boolean jobIsDone;

    public MyVKRequestListener(RequestOperations operation,
                               ArrayList<MailItem> mailItemList, VKWorker vkWorker) {
        this.operation = operation;
        this.mailItemList = mailItemList;
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
                VKApiMessage lastMessFromDial;
                MyVKRequestListener listener_next;
                StringBuilder userIds = new StringBuilder();
                MailItem nextMailItem;
                for(int i = 0; i < dialogs.size(); i++) {
                    lastMessFromDial = dialogs.get(i).message;
                    //Now let's set info that we have: time and message body
                    nextMailItem = new MailItem();
                    nextMailItem.setDateTime(lastMessFromDial.date);
                    nextMailItem.setMessageBody(lastMessFromDial.body);
                    addMessage(nextMailItem);
                    //Now let's add user id to the list
                    userIds.append(lastMessFromDial.user_id);
                    if(i!=dialogs.size()-1) { userIds.append(","); }
                }
                //Next we need to retrieve user in order to get userPic addr and userName
                request_next = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                        userIds.toString(), VKApiConst.FIELDS, "photo_100"));
                listener_next = new MyVKRequestListener(RequestOperations.GET_USERS, mailItemList,
                        vkWorker);
                request_next.executeWithListener(listener_next);
                break;
            case GET_USERS:
                Log.i("MYLOG_MyRequestListener", "getUser request's executed ");
                VKList<VKApiUser> usersList = (VKList<VKApiUser>)response.parsedModel;
                MailItem mailItem = new MailItem();
                //Now we can set what information we missed until now
                int i = 0;
                for(VKApiUser usr : usersList) {
                    //mailItem.setUserName(usr.first_name + " " + usr.last_name);
                    //mailItem.setUserPicURL(usr.photo_100);
                    //addMessage(mailItem, i);
                    mailItemList.get(i).setUserName(usr.first_name + " " + usr.last_name);
                    mailItemList.get(i).setUserPicURL(usr.photo_100);
                    i++;
                }
                //All requests were executed. Now we need to inform our View
                Log.i("MYLOG_MyRequestListener", "all getUser requests are executed ");
                vkWorker.setChangedData(mailItemList);
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
            case GET_USERS:
                Log.i("MYLOG_MyRequestListener", "getUser request's attemptFailed ");

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
            case GET_USERS:
                Log.i("MYLOG_MyRequestListener", "getUser request's error ");
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

    /**
     * Add mailItem to the mailItem list at the given position.
     * @param mailItem
     */
    public void addMessage(MailItem mailItem, int position) {
        Log.i("MYLOG_MyRequestListener", "addMessage(): " + mailItem.getUserName());
        mailItemList.add(position, mailItem);
    }
}

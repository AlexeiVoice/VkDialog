package com.pd.a2.vkdialog.listener;

import android.util.Log;

import com.pd.a2.vkdialog.model.MailItem;
import com.pd.a2.vkdialog.util.RequestOperations;
import com.pd.a2.vkdialog.util.VKWorker;
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

public class MyVKRequestListener extends VKRequest.VKRequestListener {

    RequestOperations operation;
    ArrayList<MailItem> mailItemList;
    VKWorker vkWorker;
    int[] userIdsArray;
    private boolean jobIsDone;

    public MyVKRequestListener(RequestOperations operation,
                               ArrayList<MailItem> mailItemList, VKWorker vkWorker) {
        this.operation = operation;
        this.mailItemList = mailItemList;
        this.vkWorker = vkWorker;
    }
    public MyVKRequestListener(RequestOperations operation,
                               ArrayList<MailItem> mailItemList, VKWorker vkWorker,
                               int[] usrIdsArray) {
        this.operation = operation;
        this.mailItemList = mailItemList;
        this.vkWorker = vkWorker;
        this.userIdsArray = usrIdsArray;
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
                userIdsArray = new int[dialogs.size()];
                StringBuilder userIdsList = new StringBuilder();
                MailItem nextMailItem;
                for(int i = 0; i < dialogs.size(); i++) {
                    lastMessFromDial = dialogs.get(i).message;
                    //Now let's set info that we have: time and message body
                    nextMailItem = new MailItem();
                    nextMailItem.setDateTime(lastMessFromDial.date);
                    nextMailItem.setMessageBody(lastMessFromDial.body);
                    addMessage(nextMailItem);
                    //Now let's add user id to the list
                    userIdsArray[i] = lastMessFromDial.user_id;
                    userIdsList.append(userIdsArray[i]);
                    if(i!=dialogs.size()-1) { userIdsList.append(","); }
                }
                Log.i("MYLOG_MyRequestListener", "Dialogs number: " + dialogs.size());
                //Next we need to retrieve user in order to get userPic addr and userName
                request_next = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                        userIdsList.toString(), VKApiConst.FIELDS, "photo_100"));
                listener_next = new MyVKRequestListener(RequestOperations.GET_USERS, mailItemList,
                        vkWorker, userIdsArray);
                request_next.executeWithListener(listener_next);
                break;
            case GET_USERS:
                VKList<VKApiUser> usersList = (VKList<VKApiUser>)response.parsedModel;
                Log.i("MYLOG_MyRequestListener", "getUser request's executed. Users list size: " +
                        usersList.size());
                MailItem mailItem = new MailItem();
                //Now we can set what information we missed until now
                VKApiUser usr;
                for(int i = 0; i < userIdsArray.length; i++) {
                    usr = usersList.getById(userIdsArray[i]);
                    mailItemList.get(i).setUserName(usr.first_name + " " + usr.last_name);
                    Log.i("MYLOG_MyRequestListener", "setName() " + i + ": " +
                            mailItemList.get(i).getUserName());
                    mailItemList.get(i).setUserPicURL(usr.photo_100);
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
        mailItemList.add(mailItem);
    }

}

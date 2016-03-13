package com.pd.a2.vkdialog.listener;


import com.pd.a2.vkdialog.model.MailItem;

import java.util.ArrayList;

public interface OnFetchDataListener {
    void onDataFetched(ArrayList<MailItem> mailItemList);
}

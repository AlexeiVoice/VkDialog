package com.pd.a2.vkdialog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pd.a2.vkdialog.model.MailItem;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;


    private RecyclerView.Adapter mAdapter;

    public void setMailItemsList(List<MailItem> mailItems) {
        Log.i("MYLOG_Main activity", "setMailItemsList. Size: " + mailItems.size());
        this.mailItemsList.clear();
        for(Iterator<MailItem> iterator = mailItems.iterator(); iterator.hasNext();) {
            mailItemsList.add(iterator.next());
        }

    }

    private RecyclerView.LayoutManager mLayoutManager;


    private List<MailItem> mailItemsList;
    private VKWorker vkWorker;
    private android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String[] myScope = new String[]{
            VKScope.MESSAGES,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vkWorker = new VKWorker(this);
        //VKSdk.initialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSwipeRefreshLayout =
                (android.support.v4.widget.SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshList(mSwipeRefreshLayout);
                FetchMailFromVK task = new FetchMailFromVK();
                task.execute();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mailItemsList = new ArrayList<MailItem>();
        mAdapter = new RecycleAdapter(mailItemsList, this);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_in:
                //TODO Authorization here
                VKSdk.login(this, myScope);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                //TODO Populate message list
                initializeData();
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                //TODO inform user about auth error
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*public void refreshList(android.support.v4.widget.SwipeRefreshLayout layout) {
        refreshDataSet();
        onItemsLoadComplete(layout);
    }*/
    /*private void onItemsLoadComplete(android.support.v4.widget.SwipeRefreshLayout layout) {
        mAdapter.notifyDataSetChanged();
         //stop refresh animation
    }*/

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private void initializeData(){
        refreshDataSet();
        mAdapter.notifyDataSetChanged();
        //TODO Initialize data set
    }

    private void refreshDataSet() {
        //mailItemsList.clear();
        vkWorker.getMessageItems();
    }

    private class FetchMailFromVK extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("Main activity", "Pull to refresh");
            refreshDataSet();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("MYLOG_Main activity", "Pull to refresh. On post execute");
            notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
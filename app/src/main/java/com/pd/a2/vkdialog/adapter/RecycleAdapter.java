package com.pd.a2.vkdialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pd.a2.vkdialog.R;
import com.pd.a2.vkdialog.model.MailItem;
import com.squareup.picasso.Picasso;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter <RecycleAdapter.MailItemViewHolder>{
    private List<MailItem> mailItemList;
    private Context context;
    public RecycleAdapter(List<MailItem> mailItemList, Context context){
        this.mailItemList = mailItemList;
        this.context = context;
    }
    public static class MailItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView userPic;
        public TextView userName;
        public TextView dateTime;
        public TextView messageContent;

        public MailItemViewHolder(View itemView) {
            super(itemView);
            userPic = (ImageView)itemView.findViewById(R.id.ivUserPic);
            userName = (TextView)itemView.findViewById(R.id.tvUserName);
            dateTime = (TextView)itemView.findViewById(R.id.tvDateTime);
            messageContent = (TextView)itemView.findViewById(R.id.tvMessageContent);
        }
    }


    @Override
    public MailItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        MailItemViewHolder mvh = new MailItemViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MailItemViewHolder holder, int position) {
        String picUrl = mailItemList.get(position).getUserPicURL();
        PrettyTime prettyTime = new PrettyTime();
        if(picUrl.trim().length() != 0) {
            Picasso.with(context).load(picUrl).into(holder.userPic);
        }
        holder.userName.setText(mailItemList.get(position).getUserName());
        Log.i("MYLOG_RecycleAdapter", String.valueOf(mailItemList.get(position).getDateTime()));
        Date date = new Date (mailItemList.get(position).getDateTime() * 1000L);
        Log.i("MYLOG_RecycleAdapter", date.toString());
        holder.dateTime.setText(prettyTime.format(date));
        holder.messageContent.setText(mailItemList.get(position).getMessageBody());
    }

    @Override
    public int getItemCount() {
        return mailItemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

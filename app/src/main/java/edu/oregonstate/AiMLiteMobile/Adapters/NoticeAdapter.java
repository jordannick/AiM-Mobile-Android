package edu.oregonstate.AiMLiteMobile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 6/15/2015.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private final static String TAG = "AiM_NoticeAdapter";

    private Context context;
    private ArrayList<Notice> notices;

    public NoticeAdapter(ArrayList<Notice> notices, Context context) {

        this.context = context;
        this.notices = notices;
    }

    public void refreshNotices(ArrayList<Notice> notices){
        this.notices = notices;
        notifyDataSetChanged();
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder holder, int position) {
        Notice notice = notices.get(position);
        Typeface GUDEA = Typeface.createFromAsset(context.getAssets(), "fonts/Gudea-Regular.otf");
        holder.type.setTypeface(GUDEA);
        holder.description.setTypeface(GUDEA);

        holder.type.setText(notice.getType());
        holder.description.setText(notice.getDescription());
        holder.valueAgo.setText(notice.getDateElements()[3]);
        holder.valueAgo.setText(notice.getDateElements()[4]);
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;
        protected TextView description;
        protected TextView valueAgo;
        protected TextView stringAgo;

        public NoticeViewHolder(View v) {
            super(v);
            type = (TextView) v.findViewById(R.id.row_type);
            description = (TextView) v.findViewById(R.id.row_description);
            valueAgo = (TextView) v.findViewById(R.id.actionRow_valueAgo);
            stringAgo = (TextView) v.findViewById(R.id.actionRow_stringAgo);
        }
    }


}

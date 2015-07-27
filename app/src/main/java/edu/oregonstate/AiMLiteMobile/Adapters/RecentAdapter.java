package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 7/22/2015.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {
    private final static String TAG = "AiM_NoticeAdapter";

    private Context context;
    public Callbacks mCallbacks;
    private ArrayList<WorkOrder> recentlyCompleted;

    public interface Callbacks {
        void onRecentSelected(WorkOrder workOrder);
    }

    public RecentAdapter(ArrayList<WorkOrder> recentlyCompleted, Context context) {
        this.context = context;
        mCallbacks = (Callbacks) context;
        this.recentlyCompleted = recentlyCompleted;
    }


    @Override
    public RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recent, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentViewHolder holder, int position) {
        final WorkOrder workOrder = recentlyCompleted.get(position);
        holder.workOrderNum.setText(workOrder.getProposalPhase());
        holder.workOrderDesc.setText(workOrder.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onRecentSelected(workOrder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recentlyCompleted.size();
    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {

        protected TextView workOrderNum;
        protected TextView workOrderDesc;

        public RecentViewHolder(View v) {
            super(v);
            workOrderNum = (TextView) v.findViewById(R.id.timeLog_recentWorkOrderNum);
            workOrderDesc = (TextView) v.findViewById(R.id.timeLog_recentDescription);
        }
    }


}

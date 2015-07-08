package edu.oregonstate.AiMLiteMobile.Adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Activities.OverviewListActivity;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by SellersK on 6/30/2015.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {
    private static final String TAG = "AiM_NavigationAdapter";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String navTitles[];
    private int icons[];
    private String name;

    private OverviewListActivity delegate;

    Typeface iconTypeface;


    public interface NavigationClickHandler{
        void handleNavigationClick(int position);
    }


    public NavigationAdapter(Activity delegate, String[] navTitles, int[] icons, String name, Typeface iconTypeface) {
        this.navTitles = navTitles;
        this.icons = icons;
        this.name = name;
        this.iconTypeface = iconTypeface;
        this.delegate = (OverviewListActivity)delegate;
        Log.d(TAG, "Display metrics, height: " + delegate.getResources().getDisplayMetrics().heightPixels);

        setFooterClickListeners();

    }


    @Override
    public NavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header, parent, false);
            v.setBackgroundResource(R.drawable.navigation_header_background);
            return new ViewHolder(v, viewType, iconTypeface);
        } else if(viewType == TYPE_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent, false);
            return new ViewHolder(v, viewType, iconTypeface);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final NavigationAdapter.ViewHolder holder, final int position) {
        holder.position = position;
        if(holder.holderId == TYPE_HEADER){
            holder.headerName.setText(name);
        }else if(holder.holderId == TYPE_ITEM){
            Log.d(TAG, "Setting onBind for navTitle #" + position);
            holder.rowTitle.setText(navTitles[position-1]);
            holder.rowIcon.setText(icons[position-1]);
            switch (position-1){
                case 0:
                    holder.rowIcon.setTextColor(delegate.getResources().getColor(R.color.routine_green));
                    break;
                case 1:
                    holder.rowIcon.setTextColor(delegate.getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case 2:
                    holder.rowIcon.setTextColor(delegate.getResources().getColor(R.color.Material_grey));
                    break;
                case 3:
                    holder.rowIcon.setTextColor(delegate.getResources().getColor(R.color.Material_grey));
                    break;
            }
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.handleNavigationClick(pos);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return navTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position){
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected int holderId;
        protected  int position;

        protected TextView rowTitle;
        protected TextView rowIcon;
        protected TextView headerName;

        public ViewHolder(View itemView, int viewType, Typeface iconTypeface) {
            super(itemView);
            if(viewType == TYPE_HEADER){
                headerName = (TextView)itemView.findViewById(R.id.nav_header_name);
            }else if(viewType == TYPE_ITEM){
                rowTitle = (TextView)itemView.findViewById(R.id.nav_item_title);
                rowIcon = (TextView)itemView.findViewById(R.id.nav_item_icon);
                rowIcon.setTypeface(iconTypeface);
            }
            holderId = viewType;
        }
    }


    private void setFooterClickListeners(){
        LinearLayout refreshLayout = (LinearLayout)delegate.findViewById(R.id.nav_footer_refresh_layout);
        LinearLayout settingsLayout = (LinearLayout)delegate.findViewById(R.id.nav_footer_settings_layout);
        LinearLayout logoutLayout = (LinearLayout)delegate.findViewById(R.id.nav_footer_logout_layout);

        setClickListener(refreshLayout, 3);
        setClickListener(settingsLayout, 4);
        setClickListener(logoutLayout, 5);
    }

    private void setClickListener(LinearLayout layout, int pos){
        final int position = pos;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.handleNavigationClick(position);
            }
        });
    }


}

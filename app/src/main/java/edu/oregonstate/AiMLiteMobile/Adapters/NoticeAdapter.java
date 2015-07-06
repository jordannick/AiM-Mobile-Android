package edu.oregonstate.AiMLiteMobile.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by jordan_n on 6/15/2015.
 */
public class NoticeAdapter extends ArrayAdapter<Notice> {
    private final static String TAG = "AiM_NoticeAdapter";


    private final Context mContext;
    ArrayList<Notice> mNotices;

    public NoticeAdapter(Context c, ArrayList<Notice> notices){
        super(c, 0, notices);
        mContext = c;
        mNotices = notices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_notice, parent, false);
        }

        Notice notice = mNotices.get(position);

        Typeface GUDEA = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gudea-Regular.otf");
        //Typeface GUDEABOLD = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gudea-Bold.otf");
        ((TextView)convertView.findViewById(R.id.row_type)).setTypeface(GUDEA);
        ((TextView)convertView.findViewById(R.id.row_description)).setTypeface(GUDEA);
        ((TextView)convertView.findViewById(R.id.row_type)).setText(notice.getType());

        ((TextView)convertView.findViewById(R.id.row_description)).setText(notice.getDescription());

        ((TextView)convertView.findViewById(R.id.actionRow_valueAgo)).setText(notice.getDateElements()[3]);
        ((TextView)convertView.findViewById(R.id.actionRow_stringAgo)).setText(notice.getDateElements()[4]);
        return convertView;
    }
}

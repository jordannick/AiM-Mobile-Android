package edu.oregonstate.AiMLiteMobile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sellersk on 6/15/2015.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {


    private List<TestContactInfo> contactList;

    public TestAdapter(List<TestContactInfo> contactList) {
        this.contactList = contactList;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.test_card_layout, parent, false);

        return new TestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        TestContactInfo ci = contactList.get(position);
        holder.vName.setText(ci.name);
        holder.vSurname.setText(ci.surname);
        holder.vEmail.setText(ci.email);
        holder.vTitle.setText(ci.name + " " + ci.surname);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public TestViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);
        }
    }
}

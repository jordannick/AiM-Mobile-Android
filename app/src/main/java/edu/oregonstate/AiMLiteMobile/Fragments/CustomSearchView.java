package edu.oregonstate.AiMLiteMobile.Fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by sellersk on 7/21/2015.
 */
public class CustomSearchView{

    private Context context;
    private int backgroundColor;
    private View v;
    private FrameLayout parent;
    private Callbacks callbacks;

    private int initialCount;
    private String hint = "Search...";

    @Bind(R.id.customSearchView_button) ImageButton clearButton;
    @Bind(R.id.customSearchView_editText) EditText editText;
    @Bind(R.id.customSearchView_count) TextView countText;


    public interface Callbacks{
        void onTextChangedFilter(String newText, TextView countText);
    }

    public CustomSearchView(Context c, int backgroundColor, FrameLayout parent, int initialCount) {
        this.callbacks = (Callbacks)c;
        this.context = c;
        this.backgroundColor = backgroundColor;
        this.parent = parent;
        this.initialCount = initialCount;
    }

    public void attachView(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.custom_search_view, parent, true);
        ButterKnife.bind(this, v);

        v.setBackgroundColor(context.getResources().getColor(backgroundColor));
        editText.setHint(hint);
        countText.setText(String.valueOf(initialCount));
        //editText.getBackground().setColorFilter(context.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);



        /*Listeners*/
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    callbacks.onTextChangedFilter("", countText);
                }else {
                    //sectionShortcutBar.setVisibility(View.VISIBLE);
                    callbacks.onTextChangedFilter(s.toString(), countText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().length() > 0){
                    editText.setText("");
                }else {
                    close();
                }
            }
        });
    }

    public void close(){
        parent.removeAllViews();
    }

    public void requestFocus(){
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}

package edu.oregonstate.AiMLiteMobile.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.TestAdapter;
import edu.oregonstate.AiMLiteMobile.TestContactInfo;

/**
 * Created by sellersk on 6/15/2015.
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private  RecyclerView recList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        //Toolbar toolbar = (Toolbar)findViewById(R.id.anim_toolbar);
        //toolbar.setTitle("HELLO");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setSupportActionBar(toolbar);
       //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("TEST");


        recList = (RecyclerView) findViewById(R.id.recycle_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        TestAdapter ca = new TestAdapter(createList(30));
        recList.setAdapter(ca);




        View view1 = findViewById(R.id.view1);
        View view2 = findViewById(R.id.view2);
        View view3 = findViewById(R.id.view3);
        View view4 = findViewById(R.id.view4);

        setOnClickListener(view1, "1");
        setOnClickListener(view2, "2");
        setOnClickListener(view3, "3");
        setOnClickListener(view4, "4");

    }

    private void setOnClickListener(View v, final String message){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "View Clicked! " + message);
                Random rand = new Random();
                recList.smoothScrollToPosition(rand.nextInt(recList.getAdapter().getItemCount()));


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private List<TestContactInfo> createList(int size) {
        List<TestContactInfo> result = new ArrayList<TestContactInfo>();
        for (int i=1; i <= size; i++) {
            TestContactInfo ci = new TestContactInfo();
            ci.name = TestContactInfo.NAME_PREFIX + i;
            ci.surname = TestContactInfo.SURNAME_PREFIX + i;
            ci.email = TestContactInfo.EMAIL_PREFIX + i + "@test.com";
            result.add(ci);
        }
        return result;
    }

}

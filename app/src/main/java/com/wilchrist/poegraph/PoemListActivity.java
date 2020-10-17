package com.wilchrist.poegraph;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PoemListActivity extends AppCompatActivity {
    ArrayList<Poem> poems;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private RecyclerView mRVPoems2;
    private PoemRecyclerAdapter mPoemRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDisplayContent();
    }

    private void initializeDisplayContent() {
        loadPoems();


        prepareAndSetPoemsRecycleView();
    }

    private void prepareAndSetPoemsRecycleView() {
        mRVPoems2 = (RecyclerView) findViewById(R.id.RVPoems2);
        final LinearLayoutManager poemsLayoutManager = new LinearLayoutManager(this);
        mRVPoems2.setLayoutManager(poemsLayoutManager);

        mPoemRecyclerAdapter = new PoemRecyclerAdapter(this);
        mRVPoems2.setAdapter(mPoemRecyclerAdapter);
    }

    private void loadPoems() {
        FirebaseUtility.poemCollectionName = getResources().getString(R.string.poem_collection_name);
        FirebaseUtility.openFbReference(FirebaseUtility.poemCollectionName, this);
        //mFirebaseDatabase = FirebaseUtility.mFirebaseDatabase;
        //mDatabaseReference = FirebaseUtility.mDatabaseReference;
        //poems = FirebaseUtility.mPoems;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtility.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPoemRecyclerAdapter.notifyDataSetChanged();
        loadPoems();

        prepareAndSetPoemsRecycleView();

        FirebaseUtility.attachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, PoemActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtility.attachListener();
                            }
                        });
                FirebaseUtility.detachListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

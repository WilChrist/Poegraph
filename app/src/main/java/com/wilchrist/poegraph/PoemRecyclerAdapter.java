package com.wilchrist.poegraph;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PoemRecyclerAdapter extends RecyclerView.Adapter<PoemRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Poem> poems;
    private final LayoutInflater mLayoutInflater;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public PoemRecyclerAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

        mFirebaseDatabase = FirebaseUtility.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtility.mDatabaseReference;
        poems = FirebaseUtility.mPoems;

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Poem poem = dataSnapshot.getValue(Poem.class);
                poem.setId(dataSnapshot.getKey());
                Log.e("Poem: ", poem.getTitle());
                poems.add(poem);
                notifyItemInserted(poems.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_poem_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poem poem = poems.get(position);
        holder.bind(poem);
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        if (poems == null) {
            return 0;
        }
        return poems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textTitle;
        public final TextView textContent;
        public final ImageView imagePoem;
        public int mCurrentPosition;

        ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.tvTitle2);
            textContent = itemView.findViewById(R.id.tvContent2);
            imagePoem = (ImageView) itemView.findViewById(R.id.ivPictureThumbnail2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PoemActivity.class);
                    intent.putExtra(PoemActivity.SELECTED_POEM, poems.get(mCurrentPosition));
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(Poem poem) {
            textTitle.setText(poem.getTitle());
            textContent.setText(poem.getContent());
            showImage(poem.getImageUrl());
        }

        private void showImage(String url) {
            if (url != null && url.isEmpty() == false) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get()
                        .load(url)
                        .resize(width, width * 2 / 3)
                        .centerCrop()
                        .into(imagePoem);
            }
        }
    }
}

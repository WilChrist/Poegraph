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

public class PoemAdapter extends RecyclerView.Adapter<PoemAdapter.PoemViewHolder> {
    ArrayList<Poem> poems;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    ImageView imagePoem;

    PoemAdapter(){
        //FirebaseUtility.openFbReference(FirebaseUtility.poemCollectionName);
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
                notifyItemInserted(poems.size()-1);
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
    public PoemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new PoemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PoemViewHolder holder, int position) {
        Poem poem = poems.get(position);
        holder.bind(poem);
    }

    @Override
    public int getItemCount() {
        if (poems == null) {
            return 0;
        }
        return poems.size();
    }


    public class PoemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvContent;

        public PoemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            imagePoem = (ImageView) itemView.findViewById(R.id.ivPictureThumbnail);

            itemView.setOnClickListener(this);
        }

        public void bind (Poem poem) {
            tvTitle.setText(poem.getTitle());
            tvContent.setText(poem.getContent());
            showImage(poem.getImageUrl());
            Log.e("Bind Poem ", poem.getId());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            Poem selectedPoem = poems.get(position);
            Intent intent = new Intent(view.getContext(), PoemActivity.class);
            intent.putExtra("Poem", selectedPoem);
            view.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && url.isEmpty()==false) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get()
                        .load(url)
                        .resize(width, width*2/3)
                        .centerCrop()
                        .into(imagePoem);
            }
        }

    }
}

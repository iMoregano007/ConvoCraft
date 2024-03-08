package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.im_oregano007.convocraft.adapters.RecentChatRecyclerAdapter;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class RecentChats extends AppCompatActivity {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_recent_chats);
        recyclerView = findViewById(R.id.recent_chat_recycler_view);
        recyclerView.setVisibility(View.INVISIBLE);
        backBtn = findViewById(R.id.back_button);


        setupRecyclerView();

        backBtn.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecentChats.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        super.onBackPressed();
    }

    void setupRecyclerView(){
        Query query = FirebaseUtils.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtils.currentUserId())
                .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).build();
        hideShimmerEffect();

        adapter = new RecentChatRecyclerAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    void hideShimmerEffect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        },1000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!= null){
            adapter.startListening();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!= null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!= null){
            adapter.notifyDataSetChanged();
        }
    }
}
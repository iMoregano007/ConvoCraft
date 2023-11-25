package com.im_oregano007.convocraft;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.im_oregano007.convocraft.adapters.SearchUserRecyclerAdapter;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class SearchUserActivity extends AppCompatActivity {

    EditText inputSearch;
    ImageButton searchUserBtn, backBtn;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        inputSearch = findViewById(R.id.input_search_user);
        searchUserBtn = findViewById(R.id.search_user_btn);
        backBtn = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        inputSearch.requestFocus();


        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        searchUserBtn.setOnClickListener(v -> {
            String searchTerm = inputSearch.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                inputSearch.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });


    }

    void setupSearchRecyclerView(String searchTerm){

        Query query = FirebaseUtils.allUsersCollectionReference()
                .whereGreaterThanOrEqualTo("userName",searchTerm);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
            adapter.startListening();
        }
    }
}
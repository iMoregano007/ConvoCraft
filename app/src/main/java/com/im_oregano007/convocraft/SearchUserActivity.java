package com.im_oregano007.convocraft;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserActivity extends AppCompatActivity {

    EditText inputSearch;
    ImageButton searchUserBtn, backBtn;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        inputSearch = findViewById(R.id.input_search_user);
        searchUserBtn = findViewById(R.id.search_user_btn);
        backBtn = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        inputSearch.requestFocus();

//        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                finish();
//            }
//        };

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

    }
}
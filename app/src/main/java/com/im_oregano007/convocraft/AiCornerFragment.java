package com.im_oregano007.convocraft;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AiCornerFragment extends Fragment {

    Button chatGPT;


    public AiCornerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ai_corner, container, false);
        chatGPT = view.findViewById(R.id.chatGptButton);
        chatGPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ChatgptScreen.class));
            }
        });

        return view;
    }
}
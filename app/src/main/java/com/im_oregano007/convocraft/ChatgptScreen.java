package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.im_oregano007.convocraft.chatgpt.ChatGptAdapter;
import com.im_oregano007.convocraft.chatgpt.ChatGptMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatgptScreen extends AppCompatActivity {
//    trying to solve a bug
public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    TextView status;
    ImageButton backBtn, messageSendBtn;
    EditText inputMessage;
    RecyclerView chatgptRecyclerView;

    List<ChatGptMessage> messageList;
    ChatGptAdapter chatGptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgpt_screen);
//        trying to solve a bug


        messageList = new ArrayList<>();

        status = findViewById(R.id.chatGptStatus);
        backBtn = findViewById(R.id.back_button);
        messageSendBtn = findViewById(R.id.message_send_btn);
        inputMessage = findViewById(R.id.input_message);
        chatgptRecyclerView = findViewById(R.id.chat_recycler_view);

        messageSendBtn.setOnClickListener( v -> {
            setStatus(true);
            String inputText = inputMessage.getText().toString().trim();
            addToChat(inputText, ChatGptMessage.SENT_BY_ME);
            inputMessage.setText("");
            callAPI(inputText);
        });

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

//        setup recycler view
        chatGptAdapter = new ChatGptAdapter(messageList);
        chatgptRecyclerView.setAdapter(chatGptAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatgptRecyclerView.setLayoutManager(linearLayoutManager);
    }

    void addResponse(String response){
        setStatus(false);
        addToChat(response, ChatGptMessage.SENT_BY_BOT);
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new ChatGptMessage(message, sentBy));
                chatGptAdapter.notifyDataSetChanged();
                chatgptRecyclerView.smoothScrollToPosition(chatGptAdapter.getItemCount());
            }
        });
    }

    void setStatus(boolean isTyping){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isTyping){
                    status.setText("Typing...");
                } else{
                    status.setText("Online");
                }
            }
        });

    }

    void callAPI(String question){
        //okhttp

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("model","gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messageArr.put(obj);

            jsonBody.put("messages",messageArr);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer --apiKey--")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    addResponse("Failed to load response due to "+response.body().toString());

                }
            }
        });

    }


}
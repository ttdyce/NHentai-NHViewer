package com.github.ttdyce.nhviewer.View;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NHAPI nhapi = new NHAPI(this);
        ResponseCallback callback = new ResponseCallback() {
            @Override
            public void onReponse(String result) {
                JsonArray array = new JsonParser().parse(result).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement jsonElement : array) {

                    Comic c = gson.fromJson(jsonElement, Comic.class);
                    updateView(c.getTitle().toString());
                }

            }
        };

        nhapi.getComicList("language:chinese", true, callback);
    }

    void updateView(String text) {
        TextView tvMain = findViewById(R.id.tvMain);
        tvMain.append(text);
    }
}
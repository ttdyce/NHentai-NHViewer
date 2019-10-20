package com.github.ttdyce.nhviewer.model.api;

import android.util.Log;

import com.android.volley.VolleyError;

public abstract class ResponseCallback {
    public abstract void onReponse(String response);
    public void onErrorResponse(VolleyError error){
        Log.e("NH ResponseCallback", "onErrorResponse: ", error);
    }
}

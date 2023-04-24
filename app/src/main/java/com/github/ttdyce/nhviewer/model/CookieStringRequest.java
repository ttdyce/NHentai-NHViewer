package com.github.ttdyce.nhviewer.model;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CookieStringRequest extends StringRequest {
    public static String challengeCookies;
    public static String userAgent;
    private Map<String, String> headers = new HashMap<>();

    public CookieStringRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        headers.put("cookie", challengeCookies);
//        headers.put("user-agent", userAgent); // 20230425 seems no effect, trying not to use it
    }

    // not in use (as for 8/1/2022)
    public CookieStringRequest(String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        headers.put("cookie", challengeCookies);
        headers.put("user-agent", userAgent);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}

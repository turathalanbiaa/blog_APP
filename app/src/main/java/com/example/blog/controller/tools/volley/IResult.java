package com.example.blog.controller.tools.volley;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;



    public interface IResult {
        void notifySuccess(String requestType, JSONObject response);
        void notifySuccessJsonArray(String requestType, JSONArray response);
        void notifyError(String requestType, VolleyError error);
    }


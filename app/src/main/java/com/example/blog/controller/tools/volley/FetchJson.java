package com.example.blog.controller.tools.volley;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class FetchJson {
   IResult mResultCallback = null;
    Context mContext;

    public FetchJson(IResult resultCallback, Context context){
        mResultCallback = resultCallback;
        mContext = context;
    }


    public void postDataVolley(final String requestType, String url,JSONObject sendObj){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(url,sendObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestType,response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestType,error);
                }
            });

            queue.add(jsonObj);

        }catch(Exception e){

        }
    }

    public void postArrayDataVolley(final String requestType, String url,JSONObject sendObj){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            CustomJsonArrayRequest jsonArray = new CustomJsonArrayRequest(Request.Method.POST, url, sendObj,
                    new Response.Listener<JSONArray>(){
                        @Override
                        public void onResponse(JSONArray response) {
                            if(mResultCallback != null)
                                mResultCallback.notifySuccessJsonArray(requestType,response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(mResultCallback != null)
                                mResultCallback.notifyError(requestType,error);
                        }
                    }
            );

            queue.add(jsonArray);

        }catch(Exception e){

        }
    }



    public void getDataVolley(final String requestType, String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            });

            queue.add(jsonObj);

        }catch(Exception e){

        }
    }

    public void getArrayDataVolley(final String requestType, String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonArrayRequest jsonArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccessJsonArray(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            });

            queue.add(jsonArray);

        }catch(Exception e){

        }
    }

    private class CustomJsonArrayRequest extends JsonRequest<JSONArray> {
        public CustomJsonArrayRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                return Response.success(new JSONArray(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }
}

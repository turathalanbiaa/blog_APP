package com.example.blog.controller.ui.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.example.blog.model.Categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatDropDownFragment extends Fragment {

    Button addCat;
    int cat_Id=1;

    IResult mResultCallback = null;
    FetchJson mVolleyService;
    String getCatUrl;
    URLs baseUrl=new URLs();
    ArrayList<Categories> catList=new ArrayList<>();
    private static final String TAG = "CatDropDownFragment";

    OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        dataPasser = (OnDataPass) context;}
        catch (Exception e) {
            Log.e(TAG, "onAttach: "+e);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//
       View root = inflater.inflate(R.layout.cat_dropdown_menu, container, false);

        getCatUrl=baseUrl.getCategoriesUrl();

        addCat=root.findViewById(R.id.addCatBtn);
//        catIdTextView =findViewById(R.id.writePost_catId);


        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.getArrayDataVolley("getArray",getCatUrl);


        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getCat();
                PopupMenu menu = new PopupMenu(getContext(), view);

                for(int i=0;i<catList.size();i++) {

                    menu.getMenu().add(catList.get(i).getName());

                }


                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                    @Override
                    public boolean onMenuItemClick(MenuItem popupItem) {

                        int index=0;
                        String name=popupItem.getTitle().toString();
                        addCat.setText("الاصناف"+" : "+name);

                        for(int i=0;i<catList.size();i++) {

                            if(name.equals(catList.get(i).getName())) {
                                index = catList.get(i).getId();
                                break;

                            }

                        }
//                        cat_Id=index;
//                        Toast.makeText(getContext(),"//"+index,Toast.LENGTH_LONG).show();
                        passData(index);
                        return true;
                    }
                });

                menu.show();
            }
        });



        return root;
    }
    private ArrayList<Categories> getCatListFromDb(JSONArray response) {
        ArrayList<Categories> list=new ArrayList<>();

        Categories noCat=new Categories(0,"الكل");
        list.add(noCat);
        try {

            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = (JSONObject) response
                        .get(i);

                int id= obj.getInt("id");
                String name = obj.getString("name");
                name=name.replaceAll("\n","");
//
                Categories cat=new Categories(id,name);
                list.add(cat);

            }


        }catch (JSONException e) {

        }
        return list;
    }
    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);



//                Toast.makeText(getApplicationContext(),"//"+response,Toast.LENGTH_LONG).show();


            }
            @Override
            public void notifySuccessJsonArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                catList=getCatListFromDb(response);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + error);

//                Toast.makeText(getContext(),"hm"+error,Toast.LENGTH_LONG).show();



            }
        };
    }
    public interface OnDataPass {
        public void onDataPass(int data);
    }
    public void passData(int data) {
        dataPasser.onDataPass(data);
    }

}
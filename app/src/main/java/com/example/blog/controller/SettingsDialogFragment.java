package com.example.blog.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.controller.tools.SwipeDismissTouchListener;
import com.example.blog.controller.ui.comments.CommentBarFragment;
import com.example.blog.controller.ui.comments.CommentsFragment;
import com.facebook.AccessToken;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;


public class SettingsDialogFragment extends DialogFragment  {

    Switch themeSwitch;
    SharedPreferences settingsPrefs;
    RadioGroup fontSelect;
    RadioButton f1,f2,f3;
    public SettingsDialogFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.dialog_settings_fragment, container,false);
        settingsPrefs = getActivity().getSharedPreferences("settings", Activity.MODE_PRIVATE);

        themeSwitch=view.findViewById(R.id.themeSwitch);

        themeSwitch.setChecked(settingsPrefs.getBoolean("nightMode",false));

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!settingsPrefs.getBoolean("nightMode",false)) {
                    SharedPreferences.Editor editor = settingsPrefs.edit();
                    editor.putBoolean("nightMode", true);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);

                }
                else{
                    SharedPreferences.Editor editor = settingsPrefs.edit();
                    editor.putBoolean("nightMode", false);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                getActivity().recreate();
            }
        });

        fontSelect=view.findViewById(R.id.fontSelect);
        f1=view.findViewById(R.id.f1);
        f2=view.findViewById(R.id.f2);
        f3=view.findViewById(R.id.f3);
        if(settingsPrefs.getInt("font",1)==1)
        {
            f1.setChecked(true);
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", 1);
            editor.apply();

        }
        else if(settingsPrefs.getInt("font",1)==2){

            f2.setChecked(true);
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", 2);
            editor.apply();
        }
        else{

            f3.setChecked(true);
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", 3);
            editor.apply();
        }
        fontSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int f=1;
                if(i==f3.getId())
                    f=3;
                else if(i==f2.getId())
                    f=2;
                else
                    f=1;
                SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", f);
            editor.apply();

           getActivity().recreate();
            }
        });

        getDialog().setTitle("CommentsDialogFragment");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corner);

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();

        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
//        params.height = (int)(getResources().getDisplayMetrics().heightPixels*0.75);
        window.setAttributes(params);
        window.getDecorView().setOnTouchListener(new SwipeDismissTouchListener(window.getDecorView(), null, new SwipeDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return true;
            }

            @Override
            public void onDismiss(View view, Object token) {
                dismiss();
            }
        }));
    }
}
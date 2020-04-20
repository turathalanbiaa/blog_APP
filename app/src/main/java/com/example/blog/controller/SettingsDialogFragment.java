package com.example.blog.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
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
    ImageButton increase,decrease;
    TextView sizeSample;
    Typeface typeface;
    boolean changed=false;

    public SettingsDialogFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.dialog_settings_fragment, container,false);
        settingsPrefs = getActivity().getSharedPreferences("settings", Activity.MODE_PRIVATE);

        //theme
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

        increase=view.findViewById(R.id.increase);
        decrease=view.findViewById(R.id.decrease);
        sizeSample=view.findViewById(R.id.sizeSample);

        //font
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
            typeface = ResourcesCompat.getFont(getContext(), R.font.myfont);

        }
        else if(settingsPrefs.getInt("font",1)==2){

            f2.setChecked(true);
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", 2);
            editor.apply();
            typeface = ResourcesCompat.getFont(getContext(), R.font.myfont2);
        }
        else{

            f3.setChecked(true);
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", 3);
            editor.apply();
            typeface = ResourcesCompat.getFont(getContext(), R.font.myfont3);

        }

        sizeSample.setTypeface(typeface);

        fontSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int f=1;
                if(i==f3.getId()) {
                    f = 3;
                    typeface = ResourcesCompat.getFont(getContext(), R.font.myfont3);
                }
                else if(i==f2.getId()) {
                    f = 2;
                    typeface = ResourcesCompat.getFont(getContext(), R.font.myfont2);
                }
                else {
                    f = 1;
                    typeface = ResourcesCompat.getFont(getContext(), R.font.myfont);
                }
                SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putInt("font", f);
            editor.apply();

            sizeSample.setTypeface(typeface);
            sizeSample.invalidate();
            changed=true;
//           getActivity().recreate();
            }
        });

        //size



        sizeSample.setTextSize(settingsPrefs.getFloat("size",16));

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float px = sizeSample.getTextSize();
                float sp = px / getResources().getDisplayMetrics().scaledDensity;
                Log.d("d", "onClick: "+sp);
                if(sp<52) {
                    sizeSample.setTextSize(sp + 4);
                    sizeSample.invalidate();
                    SharedPreferences.Editor editor = settingsPrefs.edit();
                    editor.putFloat("size", sp + 4);
                    editor.apply();
                    changed=true;

                }
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float px = sizeSample.getTextSize();
                float sp = px / getResources().getDisplayMetrics().scaledDensity;
                if(sp>12) {
                    sizeSample.setTextSize(sp - 2);
                    sizeSample.invalidate();
                    SharedPreferences.Editor editor = settingsPrefs.edit();
                    editor.putFloat("size", sp - 2);
                    editor.apply();
                    changed=true;
                }
            }
        });

        //default settings
        Button defaultSettings=view.findViewById(R.id.defaultSettings);
        defaultSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settingsPrefs.edit();
                editor.putFloat("size", 16);
                editor.putInt("font",1);
                editor.putBoolean("nightMode", false);
                editor.apply();
                changed=true;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getActivity().recreate();
                dismiss();

            }
        });

        getDialog().setTitle("CommentsDialogFragment");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corner);

        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(changed &&  isAdded())
        getActivity().recreate();
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
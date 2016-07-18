package face.dragon_ball.tsoglani.dragonface;

import android.os.Bundle;
import android.app.Activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        change_background_manual = (RadioButton) findViewById(R.id.change_background);
        change_animation_manual = (RadioButton) findViewById(R.id.change_animation);
        radio_group=(RadioGroup)findViewById(R.id.radio_group);
        do_nothing = (RadioButton) findViewById(R.id.no_change);

        change_hour_type = (CheckBox) findViewById(R.id.hour_type);
        date_visible= (CheckBox) findViewById(R.id.date_visible);
        enable_animation= (CheckBox) findViewById(R.id.enable_animation);
        if (DragonballFace.isChangingBackgoundByTouch) {
            change_background_manual.setChecked(true);
        }

        else if (DragonballFace.isChangingAnimationByTouch) {
            change_animation_manual.setChecked(true);
        }
        else if(!DragonballFace.isChangingAnimationByTouch&&!DragonballFace.isChangingBackgoundByTouch){
            do_nothing.setChecked(true);
        }


        if (DragonballFace.is24HourType) {
            change_hour_type.setChecked(true);
        } else {
            change_hour_type.setChecked(false);
        }

        if (DragonballFace.isDateVisible) {
            date_visible.setChecked(true);
        } else {
            date_visible.setChecked(false);
        }




        if (DragonballFace.isEnableAnimation) {
            enable_animation.setChecked(true);
        } else {
            enable_animation.setChecked(false);
        }
        addListener();
    }

    private RadioButton change_background_manual,change_animation_manual,do_nothing;
    private RadioGroup radio_group;
    private CheckBox change_hour_type,date_visible;
    private CheckBox enable_animation;

    private void addListener() {

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e("IDD",i+"");

                if(change_background_manual.getId()==i){
                    storeSharePref(CHANGE_BACKGROUND_ON_CLICK, true);
                    DragonballFace.isChangingBackgoundByTouch = true;

                    DragonballFace.isChangingAnimationByTouch = false;
                    storeSharePref(CHANGE_ANIMATION_ON_CLICK, false);
                }else  if(change_animation_manual.getId()==i){
                    DragonballFace.isChangingAnimationByTouch = true;
                    storeSharePref(CHANGE_ANIMATION_ON_CLICK, true);

                    storeSharePref(CHANGE_BACKGROUND_ON_CLICK, false);
                    DragonballFace.isChangingBackgoundByTouch = false;
                }else  if(do_nothing.getId()==i){
                    storeSharePref(CHANGE_BACKGROUND_ON_CLICK, false);
                    DragonballFace.isChangingBackgoundByTouch = false;
                    DragonballFace.isChangingAnimationByTouch = false;
                    storeSharePref(CHANGE_ANIMATION_ON_CLICK, false);
                }

            }
        });

//        change_background_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(change_background_manual.isChecked()) {
//                storeSharePref(CHANGE_BACKGROUND_ON_CLICK, isChecked);
//                MinecraftFace.isChangingBackgoundByTouch = isChecked;
//             if( MinecraftFace.isChangingAnimationByTouch){
//                MinecraftFace.isChangingAnimationByTouch=false;
//                storeSharePref(CHANGE_ANIMATION_ON_CLICK, false);}}
//
//            }
//        });
//        change_animation_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//if(change_animation_manual.isChecked()){
//                storeSharePref(CHANGE_ANIMATION_ON_CLICK, isChecked);
//                MinecraftFace.isChangingBackgoundByTouch = isChecked;
//                if( MinecraftFace.isChangingAnimationByTouch) {
//                    MinecraftFace.isChangingAnimationByTouch = false;
//                    storeSharePref(CHANGE_BACKGROUND_ON_CLICK, false);
//                }
//            }}
//        });
//
//        do_nothing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if(do_nothing.isChecked()) {
//                    if(MinecraftFace.isChangingBackgoundByTouch){
//                    storeSharePref(CHANGE_ANIMATION_ON_CLICK, false);
//                        MinecraftFace.isChangingBackgoundByTouch = false;
//                    }
//                    if(MinecraftFace.isChangingAnimationByTouch){
//                    MinecraftFace.isChangingAnimationByTouch = false;
//                    storeSharePref(CHANGE_BACKGROUND_ON_CLICK, false);
//                }}
//            }
//        });

        change_hour_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(HOUR_TYPE, isChecked);
                DragonballFace.is24HourType = isChecked;



            }
        });

        date_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(DATE_TYPE, isChecked);
                DragonballFace.isDateVisible = isChecked;



            }
        });


        enable_animation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(ENABLE_ANIMATION, isChecked);
                DragonballFace.isEnableAnimation = isChecked;



            }
        });


    }

    protected static final String MY_PREFS_NAME = "MortalKombat_watch_FacePref";
    protected static final String CHANGE_BACKGROUND_ON_CLICK = "isChangingBackgoundByTouch_MortalKombat";
    protected static final String CHANGE_ANIMATION_ON_CLICK = "isChangingAnimationByTouch_MortalKombat";

    protected static final String HOUR_TYPE = "Hour_type_MortalKombat";
    protected static final String DATE_TYPE = "Date_type_MortalKombat";
    protected static final String ENABLE_ANIMATION = "is enable animation_MortalKombat";


    private boolean getSharedPref(String text, boolean defVal) {
        return getSharedPref(this, text, defVal);
    }

    protected static boolean getSharedPref(Context context, String text, boolean defVal) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(text, defVal);

    }

    public void storeSharePref(String text, boolean value) {
        Log.e("isChangingBackgound", "" + value);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(text, value);
        editor.commit();
    }


}

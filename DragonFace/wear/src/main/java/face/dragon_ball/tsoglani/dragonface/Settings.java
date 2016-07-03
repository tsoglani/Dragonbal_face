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
        change_background_manual = (CheckBox) findViewById(R.id.change_background_manual);
        change_hour_type = (CheckBox) findViewById(R.id.hour_type);
        date= (CheckBox) findViewById(R.id.date);
        disable_animation = (RadioButton) findViewById(R.id.disable_animation);
        radio_group=(RadioGroup)findViewById(R.id.radioGroup);
        minuteChange= (RadioButton) findViewById(R.id.minuteChange);
       hourChange= (RadioButton) findViewById(R.id.hourChange);





        if (DragonballFace.isChangingBackgoundByTouch) {
            change_background_manual.setChecked(true);
        } else {
            change_background_manual.setChecked(false);
        }

        if (DragonballFace.is24HourType) {
            change_hour_type.setChecked(true);
        } else {
            change_hour_type.setChecked(false);
        }


        if (DragonballFace.isEnableAnimation) {
            disable_animation.setChecked(true);
        } else {
            disable_animation.setChecked(false);
        }


        if (DragonballFace.isDateVisible) {
            date.setChecked(true);
        } else {
            date.setChecked(false);
        }




        if (DragonballFace.isEnableAnimation&&  DragonballFace.IsAnimationChangingPerHour) {
            hourChange.setChecked(true);
        }

        else  if (DragonballFace.isEnableAnimation&&  DragonballFace.IsAnimationChangingPerMinute) {
            minuteChange.setChecked(true);
        }
        else if(!DragonballFace.isEnableAnimation){
            disable_animation.setChecked(true);
        }
        addListener();
    }

    private CheckBox change_background_manual;
    private CheckBox change_hour_type,date;
    private RadioButton disable_animation,minuteChange,hourChange;
private RadioGroup radio_group;
    private void addListener() {

        change_background_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(touchPref, isChecked);
                DragonballFace.isChangingBackgoundByTouch = isChecked;

            }
        });

        change_hour_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(HOUR_TYPE, isChecked);
                DragonballFace.is24HourType = isChecked;



            }
        });

        date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                storeSharePref(DATE, isChecked);
                DragonballFace.isDateVisible = isChecked;



            }
        });


        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e("IDD",i+"");

                if(hourChange.getId()==i){
                    storeSharePref(IS_HOUR_ANIMATION_CHANGE, true);
                    DragonballFace.IsAnimationChangingPerHour = true;
                    storeSharePref(ENABLE_ANIMATION, true);
                    DragonballFace.isEnableAnimation = true;
                    storeSharePref(IS_MINUTE_ANIMATION_CHANGE, false);
                    DragonballFace.IsAnimationChangingPerMinute = false;
                }else  if(minuteChange.getId()==i){

                    storeSharePref(IS_MINUTE_ANIMATION_CHANGE, true);
                    DragonballFace.IsAnimationChangingPerMinute = true;
                    storeSharePref(ENABLE_ANIMATION, true);
                    DragonballFace.isEnableAnimation = true;
                    storeSharePref(IS_HOUR_ANIMATION_CHANGE, false);
                    DragonballFace.IsAnimationChangingPerHour = false;



                }else  if(disable_animation.getId()==i){
                    storeSharePref(ENABLE_ANIMATION, false);
                    DragonballFace.isEnableAnimation = false;
                    storeSharePref(IS_MINUTE_ANIMATION_CHANGE, false);
                    DragonballFace.IsAnimationChangingPerMinute = false;
                    storeSharePref(IS_HOUR_ANIMATION_CHANGE, false);
                    DragonballFace.IsAnimationChangingPerHour = false;
                }

            }
        });



    }

    protected static final String MY_PREFS_NAME = "dragonballFacePref_dragonball";
    protected static final String touchPref = "isChangingBackgoundByTouchDragonball";
    protected static final String HOUR_TYPE = "Hour_type_dragonball";
    protected static final String ENABLE_ANIMATION = "is enable animation_dragonball";
    protected static final String IS_HOUR_ANIMATION_CHANGE = "IS_HOUR_ANIMATION_CHANGE_dragonball";
    protected static final String IS_MINUTE_ANIMATION_CHANGE = "IS_MINUTE_ANIMATION_CHANGE_dragonball";
    protected static final String DATE = "DATE Pref_dragonball";


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

package face.dragon_ball.tsoglani.dragonface;

import android.os.Bundle;
import android.app.Activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        change_background_manual = (CheckBox) findViewById(R.id.change_background_manual);
        change_hour_type = (CheckBox) findViewById(R.id.hour_type);

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
        addListener();
    }

    private CheckBox change_background_manual;
    private CheckBox change_hour_type;

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


    }

    protected static final String MY_PREFS_NAME = "dragonballFacePref";
    protected static final String touchPref = "isChangingBackgoundByTouch";
    protected static final String HOUR_TYPE = "Hour_type";

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

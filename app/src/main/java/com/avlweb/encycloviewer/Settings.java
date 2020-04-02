package com.avlweb.encycloviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NavUtils;

public class Settings extends Activity {

    public static final String KEY_PREFS = "EncycloViewerPreferences";
    public static final String KEY_DATABASES_ROOT_LOCATION = "key_language";
    public static final String KEY_HIDE_SAMPLE_DATABASE = "key_hide_sample";
    public static final String KEY_SCROLLBAR = "key_scrollbar";
    public static final String KEY_FONT_SIZE = "key_font_size";

    private String[] scrollbarPositions;
    private int fontSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.settings);

        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(false);
        }

        // Get preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        // Get flag "Hide sample database"
        boolean hideSampledatabase = pref.getBoolean(KEY_HIDE_SAMPLE_DATABASE, false);
        Switch hide = findViewById(R.id.switch_hide);
        hide.setChecked(hideSampledatabase);
        // Get Databases Root location
        String databasesRootLocation = pref.getString(KEY_DATABASES_ROOT_LOCATION, this.getExternalFilesDir(null).toString());
        TextView textView = findViewById(R.id.EditTextRootLocation);
        textView.setText(databasesRootLocation);
        // Get Scrollbar position
        int scrollbar = pref.getInt(KEY_SCROLLBAR, 0);
        scrollbarPositions = getResources().getStringArray(R.array.scrollbar_position_array);
        Spinner spinner = findViewById(R.id.spinnerScrollbar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.scrollbar_position_array, R.layout.mainlist);
        adapter.setDropDownViewResource(R.layout.mainlist);
        spinner.setAdapter(adapter);
        spinner.setSelection(scrollbar);
        // Font size
        fontSize = pref.getInt(KEY_FONT_SIZE, 0);
        TextView textSeekbar = findViewById(R.id.TextLorum);
        textSeekbar.setTextSize(getFontSizeFromPref(fontSize));
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                doOnFontSizeChanged(group, checkedId);
            }
        });
        RadioButton button = findViewById(R.id.radioButtonSmall);
        switch (fontSize) {
            case 1:
                button = findViewById(R.id.radioButtonNormal);
                break;
            case 2:
                button = findViewById(R.id.radioButtonBig);
                break;
            case 3:
                button = findViewById(R.id.radioButtonVerybig);
                break;
        }
        button.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    private void doOnFontSizeChanged(RadioGroup group, int checkedId) {
        int checkedRadioId = group.getCheckedRadioButtonId();
        if (checkedRadioId == R.id.radioButtonSmall) {
            TextView textSeekbar = findViewById(R.id.TextLorum);
            textSeekbar.setTextSize(getFontSizeFromPref(0));
            fontSize = 0;
        } else if (checkedRadioId == R.id.radioButtonNormal) {
            TextView textSeekbar = findViewById(R.id.TextLorum);
            textSeekbar.setTextSize(getFontSizeFromPref(1));
            fontSize = 1;
        } else if (checkedRadioId == R.id.radioButtonBig) {
            TextView textSeekbar = findViewById(R.id.TextLorum);
            textSeekbar.setTextSize(getFontSizeFromPref(2));
            fontSize = 2;
        } else if (checkedRadioId == R.id.radioButtonVerybig) {
            TextView textSeekbar = findViewById(R.id.TextLorum);
            textSeekbar.setTextSize(getFontSizeFromPref(3));
            fontSize = 3;
        }
    }

    private int getFontSizeFromPref(int val) {
        int res = 14;
        switch (val) {
            case 0:
                res = 14;
                break;
            case 1:
                res = 16;
                break;
            case 2:
                res = 18;
                break;
            case 3:
                res = 20;
                break;
        }
        return res;
    }

    public void SaveSettings(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // Databases Root location
        EditText rootLocation = findViewById(R.id.EditTextRootLocation);
        editor.putString(KEY_DATABASES_ROOT_LOCATION, rootLocation.getText().toString());
        // Scrollbar position
        Spinner spinner = findViewById(R.id.spinnerScrollbar);
        editor.putInt(KEY_SCROLLBAR, spinner.getSelectedItemPosition());
        // Font size
        editor.putInt(KEY_FONT_SIZE, fontSize);
        // Hide sample database
        Switch hide = findViewById(R.id.switch_hide);
        editor.putBoolean(KEY_HIDE_SAMPLE_DATABASE, hide.isChecked());
        // Save preferences
        editor.apply();
        Toast.makeText(getApplicationContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

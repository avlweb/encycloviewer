package com.avlweb.encycloviewer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NavUtils;

import com.avlweb.encycloviewer.R;
import com.avlweb.encycloviewer.model.DatabaseInfos;
import com.avlweb.encycloviewer.model.EncycloDatabase;
import com.avlweb.encycloviewer.model.FieldDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseDetails extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_database_details);

        setTitle(getString(R.string.about_database));

        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(false);
        }

        DatabaseInfos dbInfos = EncycloDatabase.getInstance().getInfos();
        TextView textView = findViewById(R.id.textName);
        textView.setText(dbInfos.getName());

        textView = findViewById(R.id.textDescription);
        textView.setText(dbInfos.getDescription());

        textView = findViewById(R.id.textVersion);
        textView.setText(dbInfos.getVersion());

        textView = findViewById(R.id.textNbItems);
        textView.setText(String.format("%d", EncycloDatabase.getInstance().getItemsList().size()));

        createFieldList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_database_details, menu);
        return true;
    }

    private ArrayList<Map<String, String>> buildData() {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (FieldDescription field : EncycloDatabase.getInstance().getFieldDescriptions()) {
            list.add(putData(field));
        }
        return list;
    }

    private HashMap<String, String> putData(FieldDescription field) {
        HashMap<String, String> item = new HashMap<>();
        item.put("name", field.getName());
        item.put("description", field.getDescription());
        return item;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.save_btn:
                if (writeFile())
                    Toast.makeText(getApplicationContext(), R.string.successfully_saved, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.problem_during_save, Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean writeFile() {
        //xmlFactory.writeXml();
        return true;
    }

    public void addField(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialog = inflater.inflate(R.layout.dialog_new_field, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.new_field));
        alertDialog.setCancelable(true);
        final EditText fieldName = dialog.findViewById(R.id.fieldName);
        fieldName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard();
                String name = fieldName.getText().toString();
                if (name.length() > 0) {
                    addNewField(name);
                }
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard();
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialog);
        alertDialog.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void addNewField(String name) {
        FieldDescription field = new FieldDescription();
        field.setName(name);
        field.setId(View.generateViewId());
        field.setDescription(null);
        EncycloDatabase.getInstance().addFieldDescription(field);

        addField(field);

        final ScrollView scrollView = findViewById(R.id.detailsScrollview);
        scrollView.post(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void addField(FieldDescription field) {
        LinearLayout linearLayout = findViewById(R.id.linearlayout);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(field.getName());
        textView.setTextColor(getColor(R.color.black));
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(textView);

        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint(getString(R.string.field_description));
        editText.setHintTextColor(getColor(R.color.dark_gray));
        editText.setGravity(Gravity.TOP);
        if (field.getDescription() != null)
            editText.setText(field.getDescription());
        editText.setPadding(20, 0, 20, 10);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        editText.setMinHeight(48);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine(true);
        editText.setId(field.getId());
        linearLayout.addView(editText);
    }

    private void createFieldList() {
        for (FieldDescription field : EncycloDatabase.getInstance().getFieldDescriptions()) {
            addField(field);
        }
    }
}

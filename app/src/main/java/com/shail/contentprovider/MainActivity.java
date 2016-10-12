package com.shail.contentprovider;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        if (R.id.add == item.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a person");
            final View inputField = getLayoutInflater().inflate(R.layout.input_person_info, null);
            builder.setView(inputField);
            final EditText ssnText = (EditText) inputField.findViewById(R.id.enter_person_ssn);
            final EditText nameText = (EditText) inputField.findViewById(R.id.enter_person_name);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String personSSN = ssnText.getText().toString();
                    String personName = nameText.getText().toString();
                    ContentValues values = new ContentValues();
                    values.clear();
                    values.put(PersonsSQLiteOpenHelper.PERSON_SSN, personSSN);
                    values.put(PersonsSQLiteOpenHelper.PERSON_NAME, personName);
                    getApplicationContext().getContentResolver().insert(PersonsContentProvider.PERSON_CONTENT_URI, values);
                    updateUI();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        } else if (R.id.update == item.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update a person");
            final View inputField = getLayoutInflater().inflate(R.layout.input_person_info, null);
            builder.setView(inputField);
            final EditText ssnText = (EditText) inputField.findViewById(R.id.enter_person_ssn);
            final EditText nameText = (EditText) inputField.findViewById(R.id.enter_person_name);
            builder.setView(inputField);
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String personSSN = ssnText.getText().toString();
                    String personName = nameText.getText().toString();
                    ContentValues values = new ContentValues();
                    values.clear();
                    values.put(PersonsSQLiteOpenHelper.PERSON_NAME, personName);
                    getApplicationContext().getContentResolver().update(PersonsContentProvider.PERSON_CONTENT_URI, values, PersonsSQLiteOpenHelper.PERSON_SSN + "=?", new String[]{personSSN});
                    updateUI();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
        return result;
    }

    private void updateUI() {
        Uri uri = PersonsContentProvider.PERSON_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.person_view,
                cursor,
                new String[]{PersonsSQLiteOpenHelper.PERSON_SSN, PersonsSQLiteOpenHelper.PERSON_NAME},
                new int[]{R.id.person_ssn, R.id.person_name},
                0
        );
        listView.setAdapter(listAdapter);
    }

    public void deletePerson(View view) {
        View parentView = (View) view.getParent();
        TextView textView = (TextView) parentView.findViewById(R.id.person_ssn);
        String personToRemove = textView.getText().toString();
        getContentResolver().delete(PersonsContentProvider.PERSON_CONTENT_URI, PersonsSQLiteOpenHelper.PERSON_SSN + "=?", new String[]{personToRemove});
        updateUI();
    }
}

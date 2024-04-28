package com.example.smd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FunctionsScreen extends Activity {
    private Button addButton, editButton, deleteButton, recycleBinButton, logoutButton;
    private EditText websiteEditText, urlEditText, usernameEditText, passwordEditText, entryIdEditText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> entries;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.functions_screen);

        db = new DatabaseHelper(this);
        entries = new ArrayList<>();

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);

        addButton = findViewById(R.id.add_button);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);
        recycleBinButton = findViewById(R.id.recycle_bin_button);
        logoutButton = findViewById(R.id.logout_button);  // Assuming the button ID is logout_button

        websiteEditText = findViewById(R.id.website_edit_text);
        urlEditText = findViewById(R.id.url_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        entryIdEditText = findViewById(R.id.entry_id_edit_text);

        addButton.setOnClickListener(v -> {
            long id = db.addPasswordEntry(
                    websiteEditText.getText().toString(),
                    urlEditText.getText().toString(),
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    1 // Assuming user ID is 1 for demonstration purposes
            );
            if (id > 0) {
                loadEntries();
                Toast.makeText(FunctionsScreen.this, "Entry Added", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            int result = db.updatePasswordEntry(
                    Integer.parseInt(entryIdEditText.getText().toString()),
                    websiteEditText.getText().toString(),
                    urlEditText.getText().toString(),
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString()
            );
            if (result > 0) {
                loadEntries();
                Toast.makeText(FunctionsScreen.this, "Entry Updated", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            db.deletePasswordEntry(Integer.parseInt(entryIdEditText.getText().toString()));
            loadEntries();
            Toast.makeText(FunctionsScreen.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
        });

        recycleBinButton.setOnClickListener(v -> {
            Intent intent = new Intent(FunctionsScreen.this, RecycleBinScreen.class);
            startActivity(intent);
            loadEntries();
        });


        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(FunctionsScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();  // This ensures the back button does not navigate back to the FunctionsScreen.
        });



        loadEntries();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();  // Refresh entries every time the activity resumes
    }
    private void loadEntries() {
        Cursor cursor = db.getAllEntries();
        entries.clear();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String entry = "ID: " + cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)) +
                    ", Website: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEBSITE)) +
                    ", URL: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URL)) +
                    ", Username: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)) +
                    ", Password: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
            entries.add(entry);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }
}

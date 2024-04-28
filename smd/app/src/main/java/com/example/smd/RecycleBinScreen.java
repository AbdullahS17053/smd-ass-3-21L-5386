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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecycleBinScreen extends Activity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> entries;
    private EditText entryIdEditText;
    private Button restoreButton, restoreAllButton, backButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_bin_screen);

        db = new DatabaseHelper(this);
        entries = new ArrayList<>();

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);

        entryIdEditText = findViewById(R.id.entry_id_edit_text);
        restoreButton = findViewById(R.id.restore_button);
        restoreAllButton = findViewById(R.id.restore_all_button);
        backButton = findViewById(R.id.back_button);

        restoreButton.setOnClickListener(v -> {
            int id = Integer.parseInt(entryIdEditText.getText().toString());
            db.restoreEntry(id);
            loadDeletedEntries();
            Toast.makeText(RecycleBinScreen.this, "Entry Restored", Toast.LENGTH_SHORT).show();
        });

        restoreAllButton.setOnClickListener(v -> {
            db.restoreAllEntries();
            loadDeletedEntries();
            Toast.makeText(RecycleBinScreen.this, "All Entries Restored", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            finish(); // Return to the previous screen
        });

        loadDeletedEntries();
    }

    private void loadDeletedEntries() {
        Cursor cursor = db.getDeletedEntries();
        entries.clear();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String entry = "ID: " + cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)) +
                        ", Website: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEBSITE)) +
                        ", URL: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URL)) +
                        ", Username: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)) +
                        ", Password: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
                entries.add(entry);
            } while (cursor.moveToNext());
        } else {
            entries.add("No recently deleted entries.");
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }
}

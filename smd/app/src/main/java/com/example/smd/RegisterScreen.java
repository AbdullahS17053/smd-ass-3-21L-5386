package com.example.smd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterScreen extends Activity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button backButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        db = new DatabaseHelper(this);

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.register_button);
        backButton = (Button) findViewById(R.id.back_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterScreen.this, "Username or password cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (db.checkUser(username)) {
                    Toast.makeText(RegisterScreen.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                long id = db.addUser(username, password);
                if (id > 0) {
                    Toast.makeText(RegisterScreen.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterScreen.this, "Registration error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the LoginScreen
                Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

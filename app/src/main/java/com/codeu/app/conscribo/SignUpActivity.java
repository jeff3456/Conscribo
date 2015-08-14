package com.codeu.app.conscribo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeu.app.conscribo.data.StoryObject;
import com.codeu.app.conscribo.data.StoryTree;
import com.codeu.app.conscribo.data.UserData;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;


public class SignUpActivity extends ActionBarActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText = (EditText) findViewById(R.id.username_edit_text);
                passwordEditText = (EditText) findViewById(R.id.password_edit_text);
                passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
                signup();
            }
        });
    }

    private void signup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder("Invalid Sign Up");
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append("Invalid Sign Up - Username length is 0");
        }
        if (username.length() < 4) {
            validationError = true;
            validationErrorMessage.append("Username must be at least 4 characters");
        }
        if (username.length() > 16) {
            validationError = true;
            validationErrorMessage.append("Username must be at most 16 characters");
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append("Invalid Sign Up - Password length is 0");
            }
            validationError = true;
            validationErrorMessage.append("Invalid Sign Up - Password length is 0");
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append("Invalid Sign Up - Passwords do not match");
            }
            validationError = true;
            validationErrorMessage.append("Invalid Sign Up - Passwords do not match");
        }
        validationErrorMessage.append("Invalid Sign Up");

        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a new Parse mUser
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        /* Initialize User Data */
        UserData userData = new UserData();
        userData.makeData(0, new ArrayList<ParseUser>());

        try {
            userData.save();
        }
        catch (ParseException e)
        {

        }

        /* Initialize User */
        user.put("contributions", new ArrayList<StoryObject>());
        user.put("liked", new ArrayList<StoryObject>());
        user.put("subscriptions", new ArrayList<StoryTree>());
        user.put("userdata" , userData.getObjectId());


        user.saveInBackground();


        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Handle the response
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

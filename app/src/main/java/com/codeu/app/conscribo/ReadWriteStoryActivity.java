package com.codeu.app.conscribo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;


public class ReadWriteStoryActivity extends ActionBarActivity {

    private boolean hasUser;
    private EditText mContributionSentenceEditText;
    private TextView mCharacterCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_write_story);

        mContributionSentenceEditText = (EditText) findViewById(R.id.rw_setence_input);
        mCharacterCountText = (TextView) findViewById(R.id.rw_character_count);

        mContributionSentenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                updateCharacterCountTextViewText();
            }
        });

    }

    private void updateCharacterCountTextViewText(){
        String updatedCharacterCount = String.format("%d/150 characters",
                mContributionSentenceEditText.getText().toString().length());
        mCharacterCountText.setText(updatedCharacterCount);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(ParseUser.getCurrentUser() == null){
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_logged_in_main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        else if (id == R.id.action_log_out) {
            ParseUser.logOut();
            Intent intent = new Intent(this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

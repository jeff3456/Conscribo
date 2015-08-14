package com.codeu.app.conscribo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codeu.app.conscribo.data.StoryObject;
import com.codeu.app.conscribo.data.StoryTree;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class CreateStoryActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    final private String LOGTAG = CreateStoryActivity.class.getSimpleName();

    final private int NULL_CREATOR = 0;
    final private int SHORT_CREATOR = 1;
    final private int NULL_TITLE = 2;
    final private int SHORT_TITLE = 3;
    final private int NULL_GENRE = 4;
    final private int NULL_SENTENCE = 5;
    final private int INVALID_SENTENCE = 6;
    final private int LONG_SENTENCE = 7;

    private String mGenreSelectedStr;
    private String mCreator;
    private EditText mTitleEditText;
    private EditText mSentenceEditText;
    private TextView mCharacterCountTextView;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        user = ParseUser.getCurrentUser();
        mCreator = ParseUser.getCurrentUser().getUsername();

        mTitleEditText = (EditText) findViewById(R.id.create_story_title);
        mSentenceEditText = (EditText) findViewById(R.id.create_story_sentence);
        mCharacterCountTextView = (TextView) findViewById(R.id.create_story_character_count);


        // Set Spinner options from genre_option_array with Adapter
        Spinner spinner = (Spinner) findViewById(R.id.genre_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genre_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Create the submit button and click listener logic
        createSubmitButtonAndListener();

        // Text Change Listener created for SentenceEditText to update the character count
        mSentenceEditText.addTextChangedListener(new TextWatcher() {
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

    private void updateCharacterCountTextViewText() {
        String updatedCharacterCount = String.format("%d/150 characters",
                mSentenceEditText.getText().toString().length());
        mCharacterCountTextView.setText(updatedCharacterCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in_main, menu);
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

    public void createSubmitButtonAndListener() {

        Button tempStoryMode = (Button) findViewById(R.id.create_story_submit);
        tempStoryMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the submission is valid and then Create Parse Object and save
                if (isValidStorySubmission()) {

                    // Create ArrayList of sentences and authors
                    List<String> sentenceList = new ArrayList<String>();
                    List<String> authorList = new ArrayList<String>();

                    sentenceList.add(mSentenceEditText.getText().toString());
                    authorList.add(mCreator);

                    // Create StoryTree
                    StoryTree tree = new StoryTree();
                    tree.makeStoryTree(mTitleEditText.getText().toString(),
                            mGenreSelectedStr,
                            mCreator,
                            ParseUser.getCurrentUser());

                    // Create StoryObject. Set depth to 0.
                    StoryObject story = new StoryObject();
                    story.makeStoryObject(mTitleEditText.getText().toString(),
                            mGenreSelectedStr,
                            authorList,
                            sentenceList,
                            0,
                            ParseUser.getCurrentUser());
                    story.setTree(tree);

                    // Save the StoryObject and the StoryTree.
                    // ***NOTE: Saving the StoryObject will save both the StoryObject and StoryTree.
                    story.saveInBackground();

                    //Adds to the mUser's contributions to be pulled from the profile page.
                    user.add("contributions", story);

                    /*
                    // Test to see if you can query a StoryObject based on its tree
                    // Result: SUCCESS! You can query a StoryObject by its tree.
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("StoryObject");
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, com.parse.ParseException e) {
                            if (parseObject == null) {
                                Log.d("StoryObject", "The getFirst request failed.");
                            } else {

                                Log.d("StoryObject", "Retrieved the object.");
                                Log.d("The title is: ", parseObject.getString("title"));

                                StoryObject tempStory = (StoryObject) parseObject;


                                // get tree and query based on that
                                ParseQuery<ParseObject> queryStoryTree =
                                        ParseQuery.getQuery("StoryObject");
                                queryStoryTree.whereEqualTo("tree", tempStory.getTree());
                                queryStoryTree.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (parseObject == null) {
                                            Log.d("SearchStoryTreeQ", "FAILED");
                                        } else {
                                            Log.d("SearchStoryTreeQ", "SUCCESS");
                                            Log.d("The title is: ", parseObject.getString("title"));
                                        }
                                    }
                                });


                            }
                        }
                    });
                    */

                    // Show toast confirmation. Finish activity and go back to MainDashboard
                    Toast.makeText(getApplicationContext(),
                            "Your Story Tree has been created",
                            Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
        });
    }

    public boolean isValidStorySubmission() {
        //Safety check for making sure mUser is logged in
        if (ParseUser.getCurrentUser() == null) {
            return false;
        }

        // Check if title was filled out correctly
        String titleString =  mTitleEditText.getText().toString();
//                Log.e(LOGTAG, titleString);
        if (titleString.length() < 1) {
            displaySubmissionErrorToast(SHORT_TITLE);
            return false;
        }

        // Check if genre was selected
//                Log.e(LOGTAG, mGenreSelectedStr);
        if(mGenreSelectedStr == null) {
            displaySubmissionErrorToast(NULL_GENRE);
            return false;
        }

        // Check if the sentence has end
        String sentenceInput = mSentenceEditText.getText().toString();
        if (!Utility.hasSentenceEnd(sentenceInput)) {
            displaySubmissionErrorToast(INVALID_SENTENCE);
            return false;
        }

        // Check if sentence is under 150 characters
        if (sentenceInput.length() > 150) {
            displaySubmissionErrorToast(LONG_SENTENCE);
            return false;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {

        mGenreSelectedStr = getResources()
                .obtainTypedArray(R.array.genre_options_array).getString(position);

        switch(position){
            case Application.Constants.SCIFI:
                break;
            case Application.Constants.FAIRY:
                break;
            case Application.Constants.HORROR:
                break;
            case Application.Constants.MYSTERY:
                break;
            case Application.Constants.FANTASY:
                break;
            case Application.Constants.ROMANCE:
                break;
            case Application.Constants.SATIRE:
                break;
            case Application.Constants.WESTERN:
                break;
            case Application.Constants.OTHER:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Show the Toast that contains the submission error
    public void displaySubmissionErrorToast(int errorCode) {

        String submissionError;
        switch(errorCode) {
            case SHORT_CREATOR:
                submissionError = "Creator's name must be at least 4 characters";
                break;
            case SHORT_TITLE:
                submissionError = "Please title your Story Tree";
                break;
            case NULL_GENRE:
                submissionError = "Please select a genre";
                break;
            case INVALID_SENTENCE:
                submissionError = "Please finish your sentence";
                break;
            case LONG_SENTENCE:
                submissionError = "Keep your sentence under 150 characters";
                break;
            default:
                submissionError = "Submission error!";
        }

        Toast.makeText(getApplicationContext(), submissionError, Toast.LENGTH_SHORT).show();
    }
}

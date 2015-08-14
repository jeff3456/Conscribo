package com.codeu.app.conscribo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeu.app.conscribo.data.StoryObject;
import com.codeu.app.conscribo.data.StoryTree;
import com.codeu.app.conscribo.data.UserData;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ParseUser mUser;
    private ParseQueryAdapter<StoryObject> mParseQueryAdapter;

    private final String LOGTAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // Retrieve intent and check if there is a userObjectId if null display currentUser
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null && b.containsKey("userObjectId")) {
            String userId = b.getString("userObjectId");

            // query ParseUser based on userObjectId
            ParseQuery userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("objectId", userId);

            userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {

                    UserData userData = Utility.getUserData((String) parseUser.get("userdata"));

                    if (e == null) {
                        TextView username =         (TextView) findViewById(R.id.profile_username);
                        TextView numLikes =         (TextView) findViewById(R.id.profile_likes);
                        //TextView numFavorites =   (TextView) findViewById(R.id.profile_favorites);
                        TextView numSubscribers =   (TextView) findViewById(R.id.profile_subscribers);
                        TextView changePassword =   (TextView) findViewById(R.id.change_password_textview);
                        ListView selectedList =     (ListView) findViewById(R.id.profile_listview);

                        ArrayList<StoryObject> contributions = (ArrayList<StoryObject>) parseUser.get("contributions");
                        ArrayList<StoryTree> subscriptions = (ArrayList<StoryTree>) parseUser.get("subscriptions");

                        username.setText(parseUser.getUsername());
                        numLikes.setText(userData.getLikes() + " Likes");
                        numSubscribers.setText(userData.getSubscribers().size() + " Subscribers");
                        if(changePassword != null) {
                            changePassword.setVisibility(TextView.INVISIBLE);
                        }

                    } else {
                        e.printStackTrace();
                        Log.e(LOGTAG, "Couldn't retrieve ParseUser with userId");
                    }

                    createContributionsQueryAdapter(parseUser);

                }
                });

        } else {

            mUser = ParseUser.getCurrentUser();

            if(mUser == null) {
                Toast.makeText(this, "Sorry you are not logged in properly", Toast.LENGTH_SHORT);
                finish();
            }

            UserData userData = Utility.getUserData((String) mUser.get("userdata"));

            TextView username = (TextView) findViewById(R.id.profile_username);
            TextView numLikes = (TextView) findViewById(R.id.profile_likes);
//            TextView numFavorites = (TextView) findViewById(R.id.profile_favorites);
            TextView numSubscribers = (TextView) findViewById(R.id.profile_subscribers);

            ListView selectedList = (ListView) findViewById(R.id.profile_listview);

            ArrayList<StoryObject> contributions = (ArrayList<StoryObject>) mUser.get("contributions");
            ArrayList<StoryTree> subscriptions = (ArrayList<StoryTree>) mUser.get("subscriptions");

            username.setText(mUser.getUsername());
            numLikes.setText(userData.getLikes() + " Likes");
            //numFavorites.setText(((ArrayList<StoryObject>) mUser.get("favorites")).size() + " Favorites");
            numSubscribers.setText(userData.getSubscribers().size() + " Subscribers");

            createContributionsQueryAdapter(mUser);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mParseQueryAdapter != null) {
            mParseQueryAdapter.loadObjects();
        }
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


    private void createContributionsQueryAdapter(final ParseUser parseUser) {
        ListView lv = (ListView) findViewById(R.id.profile_listview);

        ParseQueryAdapter.QueryFactory<StoryObject> factoryContributions =
                new ParseQueryAdapter.QueryFactory<StoryObject>() {
                    public ParseQuery<StoryObject> create() {

                        ParseQuery<StoryObject> query = StoryObject.getQuery();
                        query.whereEqualTo("user", parseUser);
                        return query;
                    }
                };

        mParseQueryAdapter = new ParseQueryAdapter<StoryObject>(this, factoryContributions) {
            @Override
            public View getItemView(StoryObject story, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.main_story_list_item, null);
                }

                //Find all relevant views
                TextView titleView = (TextView)     view.findViewById( R.id.list_story_title);
                TextView authorView = (TextView)    view.findViewById( R.id.list_story_author);
                TextView likesView = (TextView)     view.findViewById( R.id.list_story_likes);
                ImageView genreImage = (ImageView)  view.findViewById( R.id.list_story_image);
                TextView blurb = (TextView)         view.findViewById( R.id.list_story_blurb);

                //  Set the content based on the story
                titleView.setText(story.getTitle());
                authorView.setText( Utility.getLastStringFromJSONArray(story.getAuthorsJSONArray()));
                likesView.setText(Integer.toString(story.getLikes()) +  " likes" );
                genreImage.setImageResource( Utility.findGenreDrawable( story.getGenre() ) );
                blurb.setText( Utility.generateStringFromJSONArray(story.getSentencesJSONArray()));

                return view;
            }
        };

        mParseQueryAdapter.setAutoload(false);
        mParseQueryAdapter.setPaginationEnabled(false);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(mParseQueryAdapter);

        mParseQueryAdapter.loadObjects();
    }
}

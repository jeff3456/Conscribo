package com.codeu.app.conscribo;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeu.app.conscribo.data.StoryObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainDashboard extends ActionBarActivity implements ActionBar.TabListener {

    private ListView mListView;

    private ParseQueryAdapter<StoryObject> mParseQueryAdapter;

    private StoryObject mSelectedStory;
    private List<StoryObject> mListStories;

    private Activity self;

    final private String LOGTAG = MainDashboard.class.getSimpleName();

    //Static variable to maintain that the mUser is logged in throughout all new calls to MainDashboard
    private static boolean hasUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectedStory = null;

        self = this;

        // Get reference to ListView and set ChoiceModeSingle
        mListView = (ListView) findViewById(R.id.listview_main);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // TODO: Create the tabs
        createActionBarTabs();

        createNewStoriesQueryAdapter();

        // Set ParseQueryAdapter to ListView
        mListView.setAdapter(mParseQueryAdapter);

        // Set up click listener on list items to save the selected StoryObjects
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedStory = mParseQueryAdapter.getItem(position);
            }
        });

        Button tempReadStoryButton = (Button) findViewById(R.id.read_story_button);
        tempReadStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Place story name in extra of Intent and send to activity
                if (mSelectedStory != null) {
                    Intent i = new Intent(self, ReadWriteStoryActivity.class);
                    i.putExtra("selectedStoryId", mSelectedStory.getObjectId());
                    i.putExtra(Intent.EXTRA_TEXT,
                            Utility.generateStringFromJSONArray(mSelectedStory.getSentencesJSONArray()));
                    startActivity(i);
                } else {
                    // Tell mUser to select a story first
                    Toast.makeText(getApplicationContext(),
                            "Please select a story",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button tempCreateStoryButton = (Button) findViewById(R.id.create_story_button);
        tempCreateStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasUser) {
                    startActivity(new Intent(self, CreateStoryActivity.class));
                }
                else {
                    //Cannot do action until mUser is logged in.
                    Toast.makeText(getApplicationContext(),
                            "Please login before creating a new story",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button tempDiscoverButton = (Button) findViewById(R.id.discover_button);
        tempDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get random story from queryAdapter and send intent to ReadWriteStoryActivity
                StoryObject randomStory = mParseQueryAdapter.getItem(
                        (int) (mParseQueryAdapter.getCount() * Math.random()) );

                if (randomStory != null) {
                    Intent i = new Intent(self, ReadWriteStoryActivity.class);
                    i.putExtra("selectedStoryId", randomStory.getObjectId());
                    i.putExtra(Intent.EXTRA_TEXT,
                            Utility.generateStringFromJSONArray(randomStory.getSentencesJSONArray()));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Try discover again!",
                            Toast.LENGTH_SHORT).show();
                    Log.e(LOGTAG, "randomStory was null.");
                }
            }
        });


    }

    private void createActionBarTabs() {
        /*
        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.new_tab)
                .setTabListener(new TabListener<ArtistFragment>(
                        this, "artist", ArtistFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.album)
                .setTabListener(new TabListener<AlbumFragment>(
                        this, "album", AlbumFragment.class));

        actionBar.addTab(tab);
        */
    }

    private void createNewStoriesQueryAdapter() {
        /*
        *   Generate QueryFactor for ParseQueryAdapter with constraints:
        *   - query <int> MAIN_DASHBOARD_MAXPOSTS number of posts
        *   - descending order of createdAt
        */
        ParseQueryAdapter.QueryFactory<StoryObject> factoryNewStories =
                new ParseQueryAdapter.QueryFactory<StoryObject>() {
                    public ParseQuery<StoryObject> create() {

                        ParseQuery<StoryObject> query = StoryObject.getQuery();
                        query.setLimit(Application.Constants.MAIN_DASHBOARDS_MAX_POSTS);
                        query.orderByDescending("updatedAt");
                        return query;
                    }
                };


        /*
        *   Set up the ParseQueryAdapter with QueryFactory
        */
        mParseQueryAdapter = new ParseQueryAdapter<StoryObject>(this, factoryNewStories) {
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

        mParseQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<StoryObject>() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(List<StoryObject> list, Exception e) {
            }
        });

        // Turn off autoloading and default pagination
        mParseQueryAdapter.setAutoload(false);
        mParseQueryAdapter.setPaginationEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (hasUser) {
            getMenuInflater().inflate(R.menu.menu_logged_in_main, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_login) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }
        else if (id == R.id.action_refresh) {
            mParseQueryAdapter.loadObjects();
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
            return true;
        }
        else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        else if (id == R.id.action_log_out) {
            ParseUser.logOut();
            Intent intent = new Intent(MainDashboard.this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mParseQueryAdapter.loadObjects();
    }

    @Override
    public void onStart() {
        super.onStart();

        hasUser = Utility.userLoggedIn();

        //Test whether logging in and logging out works.
        //Log.v("Logged in:", "User logged in is " + hasUser);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}

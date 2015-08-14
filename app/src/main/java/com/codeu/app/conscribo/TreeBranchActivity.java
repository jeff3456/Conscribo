package com.codeu.app.conscribo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeu.app.conscribo.data.BranchNode;
import com.codeu.app.conscribo.data.StoryObject;
import com.codeu.app.conscribo.data.StoryTree;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeBranchActivity extends AppCompatActivity {

    private final String LOGTAG = TreeBranchActivity.class.getSimpleName();

    private StoryTree mStoryTree;

    private StoryObject mSelectedBranch;
    private BranchNode mRootBranchNode;
    private BranchesAdapter mBranchesAdapter;

    private Activity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_branch);

        self = this;

        // Receive Tree id from Intent
        Intent i = getIntent();
        Bundle b = i.getExtras();

        String treeId = null;

        if (b != null && b.containsKey("treeId")) {
//            Log.v(LOGTAG, "bundle is not empty");
            treeId = b.getString("treeId");
        }

        if (treeId == null) {
            // Error in finding the StoryID in the Intent
            Log.e(LOGTAG, "Error finding selectedTreeId from intent");
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            this.finish();

        } else {
            //Query StoryTree based on treeId
            ParseQuery<StoryTree> queryTree = StoryTree.getQuery();
            queryTree.whereEqualTo("objectId", treeId);
            queryTree.getFirstInBackground(new GetCallback<StoryTree>() {
                @Override
                public void done(StoryTree storyTree, ParseException e) {

                    if(e == null) {
                        // Now proceed to retrieve StoryObjects with queryBranches(storyTree)
                        queryBranches(storyTree);
                    } else {
                        e.printStackTrace();
                        Log.e(LOGTAG, "Couldn't retrieve StoryTree with treeId");
                    }
                }
            });
        }

        ListView listView = (ListView) findViewById(R.id.branches_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedBranch = mBranchesAdapter.getItem(position).getStory();
            }
        });

        Button readBranchButton = (Button) findViewById(R.id.read_branch_button);
        readBranchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedBranch == null){
                    Toast.makeText(getApplicationContext(), "Please select a branch",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(self, ReadWriteStoryActivity.class);
                    i.putExtra("selectedStoryId", mSelectedBranch.getObjectId());
                    startActivity(i);
                }

            }
        });

    }


    private void queryBranches(StoryTree tree) {
        // Check to see if tree is null. If it is then the tree query failed.
        if(tree == null) {

            Log.e(LOGTAG, "tree was null in " +
                    "queryBranches(tree). Finish activity.");
            Toast.makeText(this, "Story tree could not be found!", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            mStoryTree = tree;
        }

        /*
        * This query for the story objects must have the constraints of whereEqualTo() and
        *  orderByAscending to ensure we get a list of StoryObjects that are under the same
        *  StoryTree and also increase in depth as the list grows. The depth ordering ensures that
        *  parent StoryObjects will appear before their children.
         */
        ParseQuery<StoryObject> query = StoryObject.getQuery();
        query.whereEqualTo("tree", mStoryTree);
        query.orderByAscending("depth");

        query.findInBackground(new FindCallback<StoryObject>() {
            @Override
            public void done(List<StoryObject> list, ParseException e) {
                // Once we have the List of StoryObjects we want to order them
                if (e == null) {
                    sortBranches(new ArrayList<StoryObject>(list));

                } else {
                    Log.e(LOGTAG, "Error: " + e.getMessage());
                }
            }
        });
    }


    private void sortBranches(List<StoryObject> branchesList) {
        if(branchesList.size() <= 0) {
            Log.e(LOGTAG, "sortBranches() given an empty branchesList");
            finish();
        }

        // HashMap for parentBranchNodeLookup: Key is depth, author and sentence String is this format
        //     depthauthorsentence
        HashMap<String, BranchNode> parentBranchNodeLookup = new HashMap<String, BranchNode >();
        mRootBranchNode = new BranchNode(branchesList.get(0));
        parentBranchNodeLookup.put(mRootBranchNode.generateKey(), mRootBranchNode);

        TextView storyTitle = (TextView) findViewById(R.id.tree_story_title);
        storyTitle.setText(mRootBranchNode.getStory().getTitle());


        // Iterate through the rest of the branchesList and place them after their parent BranchNode
        for(int i = 1; i < branchesList.size(); i++) {
            BranchNode current = new BranchNode(branchesList.get(i));
            String parentKey = current.generateParentKey();

            // Look up parent BranchNode based on parentKey
            if(parentBranchNodeLookup.containsKey(parentKey)) {
                BranchNode parent = parentBranchNodeLookup.get(parentKey);
                BranchNode child = parent.getNext();

                parentBranchNodeLookup.put(current.generateKey(), current);

                // Insert current node between parent and child
                current.setNext(child);
                parent.setNext(current);
            } else {
                Log.e(LOGTAG, "Error: Could not find parentKey in parentBranchNodeLookup");
                finish();
            }
        }
        // Transfer the BranchNodes Linked List into ArrayList<BranchNode>
        ArrayList<BranchNode> sortedBranches = new ArrayList<BranchNode>();
        BranchNode current = mRootBranchNode;
        while(current != null) {
            sortedBranches.add(current);
            current = current.getNext();
        }

        // Now we create an arrayAdapter, define its getView()
        setAdapter(sortedBranches);
    }

    private void setAdapter(List<BranchNode> branches) {
        ListView listView = (ListView) findViewById(R.id.branches_list);

        mBranchesAdapter = new BranchesAdapter(this, (ArrayList<BranchNode>) branches);

        listView.setAdapter(mBranchesAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tree_branch, menu);
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

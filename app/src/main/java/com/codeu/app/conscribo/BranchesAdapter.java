package com.codeu.app.conscribo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codeu.app.conscribo.data.BranchNode;

import java.util.ArrayList;

/**
 * Created by jeff on 8/4/15.
 */
public class BranchesAdapter extends ArrayAdapter<BranchNode> {

    final private int[] BRANCH_DIVIDERS_ARRAY = new int[]{
            R.id.branch_divider_0,
            R.id.branch_divider_1,
            R.id.branch_divider_2,
            R.id.branch_divider_3,
            R.id.branch_divider_4,
            R.id.branch_divider_5,
            R.id.branch_divider_6,
            R.id.branch_divider_7,
            R.id.branch_divider_8,
            R.id.branch_divider_9
    };

    public BranchesAdapter(Context context, ArrayList<BranchNode> branches) {
        super(context, 0, branches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item <BranchNode> for this position
        BranchNode branch = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.tree_branch_list_item, parent, false);
        }

        // Set the text of list_item to the branch sentence
        TextView branchSentence = (TextView) convertView.findViewById(R.id.sentence_branch_text);
        TextView branchAuthor = (TextView) convertView.findViewById(R.id.branch_author_text);
        TextView branchLikes = (TextView) convertView.findViewById(R.id.branch_num_likes);

        branchSentence.setText(branch.getSentence());
        branchAuthor.setText(branch.getAuthor());
        branchLikes.setText(branch.getLikes() + " likes");

        /*
        * The root branch should be left as is.
         */
        if(position != 0) {
            /*
            * For branches higher than the root branch we:
            *   1. Set the dividers to be visible depending on the depth
            *   2. Then set align_parent_left to false.
            *   3. Then dynamically set to Right Of the last visible divider.
            */

            int depth = branch.getDepth();

            for(int i = 0; i < depth; i++) {
                ImageView divider = (ImageView) convertView.
                        findViewById(BRANCH_DIVIDERS_ARRAY[i]);
                divider.setVisibility(ImageView.VISIBLE);
            }

            // Create new layout params for the sentence
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.RIGHT_OF, BRANCH_DIVIDERS_ARRAY[depth - 1]);

            branchSentence.setLayoutParams(params);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

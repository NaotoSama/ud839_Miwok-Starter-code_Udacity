package com.example.android.miwok;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class WordAdapter extends ArrayAdapter<Word> {

    /** Resource ID for the background color for this list of words
     * Create a color integer to be passed in the WordAdapter
     */
    private int mColorResourceId;

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want to populate into the lists.
     * @param context   The current context. Used to inflate the layout file.
     * @param words  A List of AndroidFlavor objects to display in a list
     */
    public WordAdapter(Activity context, ArrayList<Word> words, int colorResourceId) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and one ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, words);
        // Set the value of mColorResourceId to be whatever (namely, R.color.category_colors, R.color.category_colors, etc. in the respective WordAdapter of respective Activity.java files) is put into mColorResourceId.
        mColorResourceId = colorResourceId;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the object located at this position in the list
        Word currentWord = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID miwok_text_view
        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.miwok_text_view);
        // Get the MiwokTranslation and set this text on the name TextView
        miwokTextView.setText(currentWord.getMiwokTranslation());

        // Find the TextView in the list_item.xml layout with the ID default_text_view
        TextView defultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
        // Get the DefaultTranslation and set this text on the number TextView
        defultTextView.setText(currentWord.getDefaultTranslation());

        //Find the ImageView in the list_item.xml layout with the ID image.
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        //Set the ImageView to the image resource specified in the current Word
        imageView.setImageResource(currentWord.getmImageResourceId());

            // Check if an image is provided for this word or not
            if(currentWord.hasImage()){
                // If an image is available, display the provided image based on the resource ID
                imageView.setImageResource(currentWord.getmImageResourceId());
                // Make sure the view is visible
                imageView.setVisibility(View.VISIBLE);
            }
            else{
                // Otherwise hide the ImageView (set visibility to GONE)
                imageView.setVisibility(View.GONE);
            }



        // Set the theme color for the list item. Use ContextCompat to create the color.
        View textContainer = listItemView.findViewById(R.id.text_container);
        // Then set the background color to the integer variable color. Find the color that the resource ID maps to.
        textContainer.setBackgroundResource(mColorResourceId);


        // Return the whole list item layout (containing 2 TextViews and 1 ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}

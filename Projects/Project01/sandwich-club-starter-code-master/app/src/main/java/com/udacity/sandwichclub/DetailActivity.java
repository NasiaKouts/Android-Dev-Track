package com.udacity.sandwichclub;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // allow Up navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView ingredientsIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(ingredientsIv);

        setTitle(sandwich.getMainName());
    }

    // handle Up navigation button pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        /* ---------------------------------------
         * we find all the views needed
         */
        TextView alsoKnownLabelTextView = findViewById(R.id.also_known_as_label_tv);
        TextView alsoKnownTextView = findViewById(R.id.also_known_as_tv);
        View divider = findViewById(R.id.first_divider);
        TextView ingredientsTextView = findViewById(R.id.ingredients_tv);
        TextView originTextView = findViewById(R.id.orgin_tv);
        TextView descTextView = findViewById(R.id.description_tv);
        // ---------------------------------------


        /* ---------------------------------------
         * first we deal with the alternative names (also known as)
         */
        List<String> alsoKnownAsList = sandwich.getAlsoKnownAs();
        // if there is none available, then we decided to not show the label at all.
        if(alsoKnownAsList.size() == 0) {
            alsoKnownLabelTextView.setVisibility(View.GONE);
            alsoKnownTextView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        // otherwise we bring back to visible the label and populate it with all the alternative names
        else{
            alsoKnownLabelTextView.setVisibility(View.VISIBLE);
            alsoKnownTextView.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            boolean isFirst = true;
            for(String alternativeName : alsoKnownAsList){
                if(isFirst){
                    alsoKnownTextView.append(alternativeName);
                    isFirst = false;
                }
                else{
                    alsoKnownTextView.append(", " + alternativeName);
                }
            }
        }
        // ---------------------------------------


        /* ---------------------------------------
         * then we deal with the ingredients
         */
        List<String> ingredientsList = sandwich.getIngredients();
        // if there is none available, then we set a text informing the user about the lack of info.
        if(ingredientsList.size() == 0) {
            ingredientsTextView.setText(getResources().getString(R.string.not_available));
            ingredientsTextView.setTypeface(ingredientsTextView.getTypeface(), Typeface.ITALIC);
        }
        // otherwise we populate the textview with the corresponding info
        else{
            boolean isFirst = true;
            for(String ingredient : ingredientsList){
                if(isFirst){
                    ingredientsTextView.append(ingredient);
                    isFirst = false;
                }
                else{
                    ingredientsTextView.append(", " + ingredient);
                }
            }
            ingredientsTextView.setTypeface(null, Typeface.NORMAL);
        }
        // ---------------------------------------

        /* ---------------------------------------
         * then we deal with the origin and description.
         * in both cases, in case we don't have the corresponding info we set a text informing the user about the lack of it.
         * otherwise we populate the textviews with the info.
         */
        String origin = sandwich.getPlaceOfOrigin();
        if(origin.equals("")){
            originTextView.setText(getResources().getString(R.string.not_available));
            originTextView.setTypeface(originTextView.getTypeface(), Typeface.ITALIC);
        }
        else{
            originTextView.setText(origin);
            originTextView.setTypeface(null, Typeface.NORMAL);
        }

        String description = sandwich.getDescription();
        if(description.equals("")){
            descTextView.setText(getResources().getString(R.string.not_available));
            descTextView.setTypeface(null, Typeface.ITALIC);
        }
        else{
            descTextView.setText(description);
            descTextView.setTypeface(null, Typeface.NORMAL);
        }
    }
}

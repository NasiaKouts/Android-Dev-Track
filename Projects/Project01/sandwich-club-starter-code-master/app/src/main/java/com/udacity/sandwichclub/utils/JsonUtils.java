package com.udacity.sandwichclub.utils;

import android.util.Log;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {

        Sandwich sandwichObject = null;

        try {
            JSONObject detailsJsonObject = new JSONObject(json);

            /*
             * ------------------------------------------------------------------------
             * it is time to get the sandwich's main and alternative (also known as) names
             */
            JSONObject name = detailsJsonObject.getJSONObject("name");

            // sandwich's main name
            String mainName = name.getString("mainName");

            // sandwich's alternative names
            ArrayList<String> alsoKnownAsArraylist = new ArrayList<>();
            JSONArray alternativeNames = name.getJSONArray("alsoKnownAs");
            for (int i=0; i < alternativeNames.length(); i++) {
                try {
                    String alternativeName = alternativeNames.getString(i);
                    alsoKnownAsArraylist.add(alternativeName);
                } catch (JSONException e) {
                    Log.e("Failed to get alternative name at position " + i, e.getMessage());
                }
            }
            // ------------------------------------------------------------------------


            /*
             * ------------------------------------------------------------------------
             * it is time to get the sandwich's placeOfOrigin
             */
            String placeOfOrigin = detailsJsonObject.getString("placeOfOrigin");
            // ------------------------------------------------------------------------


            /*
             * ------------------------------------------------------------------------
             * it is time to get the sandwich's description
             */
            String description = detailsJsonObject.getString("description");
            // ------------------------------------------------------------------------


            /*
             * ------------------------------------------------------------------------
             * it is time to get the sandwich's image url
             */
            String image = detailsJsonObject.getString("image");
            // ------------------------------------------------------------------------


            /*
             * ------------------------------------------------------------------------
             * it is time to get the sandwich's ingredients
             */
            ArrayList<String> ingredientsArraylist = new ArrayList<>();
            JSONArray ingredients = detailsJsonObject.getJSONArray("ingredients");
            for (int i=0; i < ingredients.length(); i++) {
                try {
                    String ingredient = ingredients.getString(i);
                    ingredientsArraylist.add(ingredient);
                } catch (JSONException e) {
                    Log.e("Failed to get ingredient at position " + i, e.getMessage());
                }
            }
            // ------------------------------------------------------------------------

            // create our sandwich object using the info pared from json
            sandwichObject = new Sandwich(mainName, alsoKnownAsArraylist, placeOfOrigin, description, image, ingredientsArraylist);
        } catch (JSONException e) {
            Log.e("Failed to parse json", e.getMessage());
            return null;
        }
        return sandwichObject;
    }
}

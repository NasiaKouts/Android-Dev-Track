package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

public class ReviewsResponse {
    private int id;

    private int totalResults;

    @SerializedName("results")
    private Reviews[] reviews;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reviews[] getReviews() {
        return reviews;
    }

    public void setReviews(Reviews[] reviews) {
        this.reviews = reviews;
    }
}

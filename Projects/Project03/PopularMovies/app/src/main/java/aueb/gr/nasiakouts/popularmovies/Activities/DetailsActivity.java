package aueb.gr.nasiakouts.popularmovies.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CirclePageIndicator;
import com.synnapps.carouselview.ViewListener;

import java.net.URL;

import aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract;
import aueb.gr.nasiakouts.popularmovies.Models.MovieDetails;
import aueb.gr.nasiakouts.popularmovies.Models.ReviewsResponse;
import aueb.gr.nasiakouts.popularmovies.Models.VideosInfo;
import aueb.gr.nasiakouts.popularmovies.Models.VideosResponse;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry.*;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.details_rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.details_backdrop_image)
    ImageView backdrop;

    @BindView(R.id.details_avg)
    TextView avg;

    @BindView(R.id.detailsReleaseYear)
    TextView releaseDate;

    @BindView(R.id.length)
    TextView length;

    @BindView(R.id.plot)
    TextView plot;

    @BindView(R.id.details_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.details_appbar)
    AppBarLayout appBar;

    @BindView(R.id.details_poster)
    ImageView poster;

    @BindView(R.id.popularity)
    TextView popularity;

    @BindView(R.id.collection)
    TextView collection;

    @BindView(R.id.spoken_languages)
    TextView moreLanguages;

    @BindView(R.id.genre)
    TextView genre;

    @BindView(R.id.details_linear_layout)
    LinearLayout detailsLinear;

    @BindView(R.id.details_connectivity_layout)
    LinearLayout connectivityProblem;

    @BindView(R.id.details_progress_bar)
    ProgressBar pb;

    @BindView(R.id.details_nested_scrollview)
    NestedScrollView nestedScrollView;

    @BindView(R.id.trailer_carousel)
    CarouselView trailerCarousel;

    @BindView(R.id.reviews)
    TextView reviews;

    @BindView(R.id.review_by)
    TextView by;

    @BindView(R.id.fav_fab)
    FloatingActionButton addRemoveFavFab;

    private String selectedMovieId;
    MovieDetails movieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intentStartedThisActivity = getIntent();

        if (intentStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            selectedMovieId = intentStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        if (!selectedMovieId.equals("")) {
            new FetchMovieDetailsTask().execute(this, selectedMovieId);
        }

        addRemoveFavFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(
                        FavoriteMovieContract.FavoriteMovieEntry.buildFlavorsUri(Long.parseLong(selectedMovieId)),
                        null,null,null,null );

                if(cursor.getCount() <= 0){
                    cursor.close();
                    addRemoveFavFab.setImageResource(R.drawable.ic_favorite);

                    ContentValues contentValues = new ContentValues();
                    // Put the task description and selected mPriority into the ContentValues
                    contentValues.put(MOVIE_ID, movieDetails.getId());
                    contentValues.put(TITLE, movieDetails.getTitle());
                    contentValues.put(BACKDROP_IMAGE, movieDetails.getBackdropPath());
                    contentValues.put(GENRES, movieDetails.getGenresFullString());
                    contentValues.put(ORIGINAL_LANGUAGE, movieDetails.getOriginalLanguage());
                    contentValues.put(SPOKEN_LANGUAGES, movieDetails.getMoreLanguagesFullString());
                    contentValues.put(PLOT_SUMMARY, movieDetails.getOverview());
                    contentValues.put(POPULARITY, movieDetails.getPopularity());
                    contentValues.put(POSTER_IMAGE, movieDetails.getPosterPath());
                    contentValues.put(RELEASE_DATE, movieDetails.getReleaseDate());
                    contentValues.put(COLLECTION, movieDetails.getCollectionInfo());
                    contentValues.put(LENGTH, movieDetails.getRuntime());
                    contentValues.put(RATING, movieDetails.getAvgVote());

                    // Insert the content values via a ContentResolver
                    Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_URI, contentValues);

                }
                else{
                    cursor.close();
                    addRemoveFavFab.setImageResource(R.drawable.ic_favorite_border);
                    getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.buildFlavorsUri(Long.parseLong(selectedMovieId)), null, null);
                }
            }
        });
    }

    @OnClick(R.id.try_again)
    public void refresh() {
        connectivityProblem.setVisibility(View.GONE);
        if (!selectedMovieId.equals("")) {
            new FetchMovieDetailsTask().execute(this, selectedMovieId);
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateUi(MovieDetails details) {
        collapsingToolbarLayout.setTitle(details.getTitle());

        if (details.getBackdropUrl() == null) {
            backdrop.setImageResource(R.drawable.gradient_gv_item_border);
        } else {
            int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.BACKDROP_IMAGE_TRANSFORMATION, null);
            Picasso.with(this).load(details.getBackdropUrl()).resize(dimens[0], dimens[1]).into(backdrop);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            lp.width = dimens[0];
            lp.height = dimens[1];
            appBar.setLayoutParams(lp);

            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            lp2.width = dimens[0];
            lp2.height = dimens[1];
            collapsingToolbarLayout.setLayoutParams(lp2);
        }

        if (details.getFullPosterUrl() == null) {
            poster.setImageResource(R.drawable.no_image_available);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW, null);
                Picasso.with(this).load(details.getFullPosterUrl()).resize(dimens[0], dimens[1]).into(poster);
            } else {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW_LAND, null);
                Picasso.with(this).load(details.getFullPosterUrl()).resize(dimens[0], dimens[1]).into(poster);
            }
        }

        ratingBar.setRating((float) (details.getAvgVote() / 2.0));

        avg.setText("" + details.getAvgVote());

        popularity.setText("" + (int) details.getPopularity());

        collection.setText(details.getCollectionInfo());

        releaseDate.setText(details.getReleaseDateModified());

        length.setText("" + details.getRuntime());

        moreLanguages.setText(details.getMoreLanguagesFullString());

        genre.setText(details.getGenresFullString());

        plot.setText(details.getOverview());

        /*
            Populate trailer's carousel view
         */
        final VideosInfo[] trailers = details.getTrailers();
        if(trailers != null && trailerCarousel != null){
                trailerCarousel.setViewListener(new ViewListener() {
                    @Override
                    public View setViewForPosition(final int position) {
                        View view = getLayoutInflater().inflate(R.layout.trailer_carousel, null);

                        ImageView image = view.findViewById(R.id.carousel_trailer_thumbnail);
                        Picasso.with(getApplicationContext())
                                .load(NetworkUtils.buildYoutubeThumbnailUrl(trailers[position].getKey()))
                                .fit()
                                .centerCrop()
                                .into(image);

                        TextView text = view.findViewById(R.id.carousel_trailer_name);
                        text.setText(trailers[position].getName());

                        trailerCarousel.setIndicatorGravity(Gravity.CENTER | Gravity.BOTTOM);

                        ImageView playTrailer = view.findViewById(R.id.play_trailer);
                        playTrailer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playTrailer(trailers[position].getKey());
                            }
                        });

                        return view;
                    }
                });

                trailerCarousel.setPageCount(trailers.length);
        }
        if(trailers == null || trailers.length == 0) {
            trailerCarousel.setVisibility(View.GONE);
        }
        if(trailers.length == 1){
            CirclePageIndicator indicator = (CirclePageIndicator) trailerCarousel.findViewById(R.id.indicator);
            if(indicator !=null){
                indicator.setVisibility(View.GONE);
            }
        }
        /* --------------------------------- */

        if (details.getReviews() == null || details.getReviews().length == 0) {
            reviews.setText("no review available yet");
        }
        else if(details.getReviews() != null) {
            reviews.setText(details.getReviews()[0].getContent());
            by.setText(details.getReviews()[0].getAuthor());
        }


    }

    public void playTrailer(String trailerKey){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeVideoAppUri(trailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeVideoUri(trailerKey));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }
    public class FetchMovieDetailsTask extends AsyncTask<Object, Void, MovieDetails> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected MovieDetails doInBackground(Object... params) {
            if (params.length == 0) {
                return null;
            }

            URL movieDetailsUrl = NetworkUtils.buildDetailsUrl(params[1].toString());

            try {
                String jsonMovieDetailsResponse = NetworkUtils
                        .getHttpResponse(movieDetailsUrl);

                Gson gson = new GsonBuilder().create();
                MovieDetails selectedMovie = gson.fromJson(jsonMovieDetailsResponse, MovieDetails.class);


                URL movieTrailersUrl = NetworkUtils.buildGetVideosUrl(params[1].toString());

                try {
                    String jsonMovieTrailesResponse = NetworkUtils
                            .getHttpResponse(movieTrailersUrl);

                    gson = new GsonBuilder().create();
                    VideosResponse videos = gson.fromJson(jsonMovieTrailesResponse, VideosResponse.class);
                    selectedMovie.setTrailers(videos.getOnlyTrailersOnYoutube());
                } catch (Exception e) {
                    Log.e("Fetching video error", e.getMessage());
                    return selectedMovie;
                }

                URL movieReviewsUrl = NetworkUtils.buildGetReviewsUrl(params[1].toString());

                try {
                    String jsonMovieReviewsResponse = NetworkUtils
                            .getHttpResponse(movieReviewsUrl);

                    gson = new GsonBuilder().create();
                    ReviewsResponse reviewsResponse = gson.fromJson(jsonMovieReviewsResponse, ReviewsResponse.class);
                    selectedMovie.setReviews(reviewsResponse.getReviews());
                } catch (Exception e) {
                    Log.e("Fetching reviews error", e.getMessage());
                    return selectedMovie;
                }

                return selectedMovie;

            } catch (Exception e) {
                Log.e("Fetching data error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieDetails details) {
            pb.setVisibility(View.GONE);

            if (details == null) {
                detailsLinear.setVisibility(View.GONE);
                connectivityProblem.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                collapsingToolbarLayout.setTitle(getString(R.string.no_internet));
            } else {
                populateUi(details);
                movieDetails = details;
                nestedScrollView.setVisibility(View.VISIBLE);
                detailsLinear.setVisibility(View.VISIBLE);
                connectivityProblem.setVisibility(View.GONE);
            }
        }
    }
}

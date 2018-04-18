package aueb.gr.nasiakouts.popularmovies.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import aueb.gr.nasiakouts.popularmovies.Activities.InfoActivity;
import aueb.gr.nasiakouts.popularmovies.Adapters.MoviesGridviewAdapter;
import aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract;
import aueb.gr.nasiakouts.popularmovies.Models.Movie;
import aueb.gr.nasiakouts.popularmovies.Models.ExploreMoviesResponse;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviesGridviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private final static String SORTED_BY_POPULARITY_PATH = "popular";
    private final static String SORTED_BY_RATING_PATH = "top_rated";

    @BindView(R.id.connectivity_layout)
    LinearLayout connectivityProblem;

    @BindView(R.id.movies_gridview)
    GridView gridView;

    @BindView(R.id.progress_bar)
    ProgressBar pb;

    private ArrayList<Movie> movies = null;
    private MoviesGridviewAdapter gridviewAdapter = null;
    private static final int FAVORITES_FETCH = 40;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movies_gridview, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        String drawerItemSelected = getString(R.string.sort_by_popularity);
        if(bundle != null) {
            drawerItemSelected = bundle.getString(getString(R.string.shared_pref_sort_key));
        }

        movies = new ArrayList<>();
        gridviewAdapter = new MoviesGridviewAdapter(getContext(), R.layout.grid_item, movies, gridView, (FrameLayout) root.findViewById(R.id.grid_item_root), bundle);
        gridView.setAdapter(gridviewAdapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(4);
        }

        if (drawerItemSelected.equals(getString(R.string.sort_by_popularity))) {
            new FetchMoviesTask().execute(SORTED_BY_POPULARITY_PATH);
        } else if(drawerItemSelected.equals(getString(R.string.sort_by_rating))) {
            new FetchMoviesTask().execute(SORTED_BY_RATING_PATH);
        } else if(drawerItemSelected.equals(getString(R.string.fav))) {
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> githubSearchLoader = loaderManager.getLoader(FAVORITES_FETCH);
            if (githubSearchLoader == null) {
                loaderManager.initLoader(FAVORITES_FETCH, null, this);
            } else {
                loaderManager.restartLoader(FAVORITES_FETCH, null, this);
            }
        }

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            Intent openInfoAcitivity = new Intent(getContext(), InfoActivity.class);
            startActivity(openInfoAcitivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.try_again)
    public void refresh() {
        connectivityProblem.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOptionSelected = sharedPreferences.getString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));
        if (sortOptionSelected.equals(getString(R.string.sort_by_popularity))) {
            new FetchMoviesTask().execute(SORTED_BY_POPULARITY_PATH);
        } else {
            new FetchMoviesTask().execute(SORTED_BY_RATING_PATH);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(final int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getContext()) {

            @Override
            protected void onStartLoading() {
                movies.clear();
                connectivityProblem.setVisibility(View.GONE);
                gridviewAdapter.clear();
                pb.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);

                forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                if (id != FAVORITES_FETCH) {
                    return null;
                }
                Context ctx = getContext();
                if(ctx == null) return null;

                Cursor cursor = ctx.getContentResolver()
                        .query(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_URI,
                                new String[] {FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID,
                                        FavoriteMovieContract.FavoriteMovieEntry.TITLE,
                                        FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE,
                                        FavoriteMovieContract.FavoriteMovieEntry.POSTER_IMAGE,
                                        FavoriteMovieContract.FavoriteMovieEntry.POPULARITY },
                                null,
                                null,
                                null);

                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        String movieId = cursor
                                .getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID));
                        String title = cursor
                                .getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.TITLE));
                        String releaseDate = cursor
                                .getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE));
                        String poster = cursor
                                .getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.POSTER_IMAGE));
                        String popularity = cursor
                                .getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.POPULARITY));

                        Movie movie = new Movie(movieId, title, poster, popularity, releaseDate);
                        movies.add(movie);
                    }
                }
                cursor.close();
                return movies;
            }
        };

    }


    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        for(Movie movie : data){
            System.out.print("");
        }
        gridviewAdapter.notifyDataSetChanged();
        pb.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        if(data == null){
            //TODO
        }
        if(data.size() == 0){
            //TODO
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            movies.clear();
            connectivityProblem.setVisibility(View.GONE);
            gridviewAdapter.clear();
            pb.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);

            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL exploreMoviesUrl = NetworkUtils.buildSortUrl(params[0]);

            try {
                String jsonExploreMoviesResponse = NetworkUtils
                        .getHttpResponse(exploreMoviesUrl);

                Gson gson = new GsonBuilder().create();
                ExploreMoviesResponse moviesResponse = gson.fromJson(jsonExploreMoviesResponse, ExploreMoviesResponse.class);
                movies.addAll(Arrays.asList(moviesResponse.getMovies()));
                return movies;

            } catch (Exception e) {
                Log.e("Fetching data error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                gridviewAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            } else {
                pb.setVisibility(View.GONE);
                connectivityProblem.setVisibility(View.VISIBLE);
            }
        }
    }
}
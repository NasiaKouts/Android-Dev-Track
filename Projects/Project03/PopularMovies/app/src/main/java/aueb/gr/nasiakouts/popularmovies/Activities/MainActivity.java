package aueb.gr.nasiakouts.popularmovies.Activities;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import aueb.gr.nasiakouts.popularmovies.Fragments.MoviesGridviewFragment;
import aueb.gr.nasiakouts.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Replace ActionBar with the Toolbar
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        openSelectedFragment(menuItem);
                        return true;
                    }
                });


        // open default fragment and set the item selected navigationView.setCheckedItem(id);
        navigationView.setCheckedItem(R.id.nav_popular);
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            fragmentManager.beginTransaction()
                    .replace(R.id.selected_fragment,  (Fragment) MoviesGridviewFragment.class.newInstance())
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Open / close the drawer when the Home - Up navigation button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSelectedFragment(MenuItem menuItem) {
        Fragment selectedFragment = null;
        Class selectedFragmentClass;

        Bundle sortingInfo = new Bundle();

        switch(menuItem.getItemId()) {
            case R.id.nav_popular:
                sortingInfo.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));
                selectedFragmentClass = MoviesGridviewFragment.class;
                break;
            case R.id.nav_top_rated:
                sortingInfo.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_rating));
                selectedFragmentClass = MoviesGridviewFragment.class;
                break;
            default:
                sortingInfo.putString(getString(R.string.shared_pref_sort_key), getString(R.string.fav));
                selectedFragmentClass = MoviesGridviewFragment.class;
                break;

        }

        try {
            selectedFragment = (Fragment) selectedFragmentClass.newInstance();
            selectedFragment.setArguments(sortingInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.selected_fragment, selectedFragment).commit();

        // highlight the selected item
        menuItem.setChecked(true);

        // Set Action Bar title
        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }
}

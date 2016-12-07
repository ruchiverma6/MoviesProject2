package project1.android.com.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import project1.android.com.project1.helper.Constant;

public class ContainerActivity extends AppCompatActivity {
    String uiType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
      setUpActionBar();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            uiType = bundle.getString(Constant.DATA_TYPE);
        }
        if (uiType.equals(getString(R.string.trailers))) {
            TrailersFragment trailersFragment = new TrailersFragment();
            trailersFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, trailersFragment, uiType).commit();
        } else if (uiType.equals(getString(R.string.reviews))) {
            ReviewsFragment reviewsFragment = new ReviewsFragment();
            reviewsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, reviewsFragment, uiType).commit();
        }
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void setActionBarTitle(String title){

        getSupportActionBar().setTitle(title);
    }
    private void initComponents() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

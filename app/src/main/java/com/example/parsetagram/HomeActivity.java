package com.example.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.parsetagram.fragments.ComposeFragment;
import com.example.parsetagram.fragments.PostsFragment;
import com.example.parsetagram.fragments.ProfileFragment;
import com.example.parsetagram.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageButton logoutButton;
    public BottomNavigationView bottom_navigation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setLogo(R.drawable.nav_logo_whiteout);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        logoutButton = findViewById(R.id.logoutBtn);

        bottom_navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });


        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        default:
                            fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottom_navigation.setSelectedItemId(R.id.action_home);
    }



    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("homeActivity", "Post[" + i + "] = "+ objects.get(i).getDescription());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

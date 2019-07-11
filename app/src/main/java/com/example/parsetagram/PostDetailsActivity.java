package com.example.parsetagram;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parsetagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    public Post post;
    public ImageView imageView;
    public TextView description;
    public TextView tvHandle;
    public TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_post_details);
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        // finding the individual views
        imageView = (ImageView) findViewById(R.id.image);
        description = (TextView) findViewById(R.id.tvDescription);
        tvHandle = (TextView) findViewById(R.id.tvHandle);
        tvDate = (TextView) findViewById(R.id.tvDate);

        bind();
    }

    public void bind() {
        // binding the views
        tvHandle.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(PostDetailsActivity.this).load(image.getUrl()).into(imageView);
        }
        description.setText(post.getDescription());
        tvDate.setText(getRelativeTimeAgo(post.getDate()));
    }

    public String getRelativeTimeAgo(Date rawDate) {
        String relativeDate = "";
        long dateMillis = rawDate.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }
}

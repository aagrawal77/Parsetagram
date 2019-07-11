package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parsetagram.EndlessRecyclerViewScrollListener;
import com.example.parsetagram.PostAdapter;
import com.example.parsetagram.R;
import com.example.parsetagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends PostsFragment {

    private ImageView ivProfile;
    private TextView tvHandle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        mPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), mPosts);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvHandle = view.findViewById(R.id.tvHandle);

        ParseUser user = ParseUser.getCurrentUser();
        tvHandle.setText(user.getUsername());
        ParseFile profilePic = user.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfile);
        }

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        // adds divider between tweets
        rvPosts.addItemDecoration(new DividerItemDecoration(rvPosts.getContext(), DividerItemDecoration.VERTICAL));
        queryPosts();
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20);
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}

package com.example.parsetagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.parsetagram.EndlessRecyclerViewScrollListener;
import com.example.parsetagram.PostAdapter;
import com.example.parsetagram.R;
import com.example.parsetagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    public Date earliest_date;
    protected RecyclerView rvPosts;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected PostAdapter adapter;
    protected List<Post> mPosts;
    protected SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       rvPosts = view.findViewById(R.id.rvPosts);
       mPosts = new ArrayList<>();
       adapter = new PostAdapter(getContext(), mPosts);
       rvPosts.setAdapter(adapter);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       rvPosts.setLayoutManager(linearLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

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

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        Log.d("YEE", "ENTERED");
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20);
        postQuery.whereLessThan(Post.KEY_CREATED_AT, earliest_date);
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
                earliest_date = mPosts.get(mPosts.size() - 1).getCreatedAt();
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        mPosts.clear();
        adapter.clear();
        queryPosts();
        swipeContainer.setRefreshing(false);
    }


    protected void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20);
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
                earliest_date = mPosts.get(mPosts.size() - 1).getCreatedAt();
            }
        });
    }
}

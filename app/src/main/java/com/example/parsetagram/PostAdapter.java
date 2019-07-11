package com.example.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parsetagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // methods needed for swipeToRefresh: clean and addAll

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public ImageView profileImage;
        public TextView description;
        public TextView tvHandle;
        public TextView tvDate;
        public ImageButton btnLike;
        public Post post;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            profileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            btnLike = (ImageButton) itemView.findViewById(R.id.btnHeart);

            itemView.setOnClickListener(this);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    post = posts.get(position);
                    if (post.likedByCurrentUser()) {
                        List liked = post.getList("likedBy");
                        liked.remove(ParseUser.getCurrentUser().getUsername());
                        post.put("likedBy", liked);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("XYZ", "we chillin");
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                        notifyItemChanged(position);
                    } else {
                        List liked = post.getList("likedBy");
                        liked.add(ParseUser.getCurrentUser().getUsername());
                        post.put("likedBy", liked);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("XYZ", "we chillin");
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                        notifyItemChanged(position);
                    }
                }
            });

        }

        public void bind(Post post) {
            tvHandle.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            ParseFile profilePic = post.getUser().getParseFile("profilePic");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(imageView);
            }
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(profileImage);
            }
            description.setText(post.getDescription());
            tvDate.setText(getRelativeTimeAgo(post.getDate()));

            if (post.likedByCurrentUser()) {
                btnLike.setImageResource(R.drawable.ufi_heart_active);
                btnLike.setColorFilter(ContextCompat.getColor(context, R.color.red_6));
            } else {
                btnLike.setImageResource(R.drawable.ufi_heart);
                btnLike.setColorFilter(ContextCompat.getColor(context, R.color.black));
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Toast.makeText(context, "Clicked!", Toast.LENGTH_LONG).show();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post", Parcels.wrap(post));
                context.startActivity(intent);
            }
        }
    }

    public String getRelativeTimeAgo(Date rawDate) {
        String relativeDate = "";
        long dateMillis = rawDate.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }


}

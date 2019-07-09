package com.example.parsetagram;

import android.app.Application;

import com.example.parsetagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("mac-the-dog")
                .clientKey("manish-kavita")
                .server("http://aagrawal77-fbu-parsetagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}


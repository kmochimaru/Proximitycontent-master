package com.estimote.proximitycontent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class ShowActivity extends AppCompatActivity {
    TextView date_start, date_end, time_start, time_end, activity_name, description;
    ImageView image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Bundle bundle = getIntent().getExtras();

        activity_name = findViewById(R.id.activity_name);
        description = findViewById(R.id.description);
        date_start = findViewById(R.id.date_start);
        date_end = findViewById(R.id.date_end);
        time_start = findViewById(R.id.time_start);
        time_end = findViewById(R.id.time_end);
        image_url = findViewById(R.id.image_url);

        activity_name.setText(bundle.getString("activity_name"));
        description.setText(bundle.getString("description"));
        date_start.setText(bundle.getString("date_start"));
        date_end.setText(bundle.getString("date_end"));
        time_start.setText(bundle.getString("time_start")+" น.");
        time_end.setText(bundle.getString("time_end")+" น.");
        new DownLoadImageTask(image_url).execute(bundle.getString("image_url"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    /*
        doInBackground(Params... params)
            Override this method to perform a computation on a background thread.
     */
    protected Bitmap doInBackground(String...urls){
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try{
            InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);
        }catch(Exception e){ // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    /*
        onPostExecute(Result result)
            Runs on the UI thread after doInBackground(Params...).
     */
    protected void onPostExecute(Bitmap result){
        imageView.setImageBitmap(result);
    }
}

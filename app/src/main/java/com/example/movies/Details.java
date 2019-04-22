package com.example.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Details extends AppCompatActivity {
    TextView tvopis,tvRelDate,tvgenres,tvtitle;
    String API_KEY = BuildConfig.APIKey;
    //String uid = "456740";
    private ArrayList<String> mGenres = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        String mdesc,url,title,reldate,uid,img_url;
        String defaulturl = "https://image.tmdb.org/t/p/w300/";
        ImageView imageView = findViewById(R.id.imageview2);
        ImageView imageView2 = findViewById(R.id.imageview3);
        tvopis = findViewById(R.id.opis);
        tvRelDate = findViewById(R.id.release_date);
        tvtitle = findViewById(R.id.titleTV);
        tvgenres = findViewById(R.id.genresTv);


        Intent intent = getIntent();
        uid = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("backdrop_path");
        reldate = intent.getStringExtra("release_date");
        img_url = intent.getStringExtra("poster_path");
        mdesc = intent.getStringExtra("overview");
        Log.w("film",uid);

        String genre_url2 = "https://api.themoviedb.org/3/movie/"+uid+"?api_key="+API_KEY+"&language=en-US";

        new Details.AsyncHttpTask2().execute(genre_url2);

        Log.w("Titles","Oprez");
        Picasso.get().load(defaulturl+url).into(imageView);
        Picasso.get().load(defaulturl+img_url).into(imageView2);

        tvopis.setText(mdesc);
        tvtitle.setText(title);
        tvRelDate.setText(reldate);
    }


    public class AsyncHttpTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Api-Key", API_KEY);
                String response = streamTostring(urlConnection.getInputStream());
                return response;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute (String result){
            parseResult(result);
            String listString = String.join(", ", mGenres);
        //  Log.i("Genre",zanr);
            tvgenres.setText(listString);
        }
    }

    String streamTostring (InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String data;
        String result = "";
        while ((data=bufferedReader.readLine()) != null){
            result += data;
        }
        if(null != stream){
            stream.close();
        }
        return result;
    }

    private void parseResult(String result){
        JSONObject response;
        try {
            response = new JSONObject(result);
            JSONArray articles = response.optJSONArray("genres");
            for (int i=0; i<articles.length();i++){
                JSONObject article=articles.optJSONObject(i);

           //   String id = article.optString("budget");
                String name = article.optString("name");
                Log.w("genre","Dada");
                Log.w("genre",String.valueOf(articles.length()));
                mGenres.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
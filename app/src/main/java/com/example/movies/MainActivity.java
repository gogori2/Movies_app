package com.example.movies;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> dataList2 = new ArrayList<>();
    boolean flag=false;
    int mostPopPage = 2;
    int topRatedPage = 2;
    //mogu dodat gitignore za gradleprop
    String API_KEY = BuildConfig.APIKey;
    //String API_KEY = "";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImagesUrls = new ArrayList<>();
    private ArrayList<String> mNames2 = new ArrayList<>();
    private ArrayList<String> mImagesUrls2 = new ArrayList<>();

    RecyclerViewAdapter adapter = new RecyclerViewAdapter (this, mNames, mImagesUrls, dataList);
    RecyclerViewAdapter adapter2 = new RecyclerViewAdapter (this, mNames2, mImagesUrls2, dataList2);
    private EndlessRecyclerViewScrollListener scrollListener;
    private EndlessRecyclerViewScrollListener scrollListener2;

    final String mostPop2 = "https://api.themoviedb.org/3/movie/popular?api_key="+API_KEY+"&language=en-US&page=";
    final String topRated2 = "https://api.themoviedb.org/3/movie/top_rated?api_key="+API_KEY+"&language=en-US&page=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String mostPop = "https://api.themoviedb.org/3/movie/popular?api_key="+API_KEY+"&language=en-US&page=1";
        final String topRated = "https://api.themoviedb.org/3/movie/top_rated?api_key="+API_KEY+"&language=en-US&page=1";

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView recyclerView2 = findViewById(R.id.my_recycler_view2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                String newPage;
                newPage = mostPop2+mostPopPage;
                flag=false;
                new MainActivity.AsyncHttpTask().execute(newPage);
                mostPopPage=mostPopPage+1;
            }
        };
        scrollListener2 = new EndlessRecyclerViewScrollListener(layoutManager2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                String newPage;
                newPage = topRated2+topRatedPage;
                flag=true;
                new MainActivity.AsyncHttpTask().execute(newPage);
                topRatedPage=topRatedPage+1;
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView2.addOnScrollListener(scrollListener2);

        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);

        new MainActivity.AsyncHttpTask().execute(mostPop);
        new MainActivity.AsyncHttpTask().execute(topRated);
    }

    public class AsyncHttpTask extends AsyncTask <String, Void, String>{
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

            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
    }

    String streamTostring (InputStream stream) throws IOException{
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
            JSONArray articles = response.optJSONArray("results");
            for (int i=0; i<articles.length();i++){
                JSONObject article=articles.optJSONObject(i);
                String id = article.optString("id");
                String title = article.optString("title");
                String backdrop_path = article.optString("backdrop_path");
                String release_date = article.optString("release_date");
                String poster_path = article.optString("poster_path");
                String genre_ids = article.optString("genre_ids");
                String overview = article.optString("overview");

            HashMap<String, String> map = new HashMap<>();
            HashMap<String, String> map2 = new HashMap<>();

                if (!flag){
                    mNames.add(title);
                    mImagesUrls.add(poster_path);
                    map.put("id",id);
                    map.put("title", title);
                    map.put("backdrop_path", backdrop_path);
                    map.put("release_date", release_date);
                    map.put("poster_path", poster_path);
                    map.put("genre_ids", genre_ids);
                    map.put("overview", overview);
                    dataList.add(map);
                 // System.out.println(dataList);
                }else {
                    mNames2.add(title);
                    mImagesUrls2.add(poster_path);
                    map2.put("id",id);
                    map2.put("title", title);
                    map2.put("backdrop_path", backdrop_path);
                    map2.put("release_date", release_date);
                    map2.put("poster_path", poster_path);
                    map2.put("genre_ids", genre_ids);
                    map2.put("overview", overview);
                    dataList2.add(map2);
                }
                Log.i("Titles",title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        flag=!flag;
    }
}

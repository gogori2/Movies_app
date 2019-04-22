package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> mImagesUrls = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> myMap = new ArrayList<>();
    private Context myContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> ImagesUrls, ArrayList<HashMap<String, String>> map) {
        mNames = names;
        mImagesUrls = ImagesUrls;
        myContext = context;
        myMap=map;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        String defaulturl = "https://image.tmdb.org/t/p/w400/";
        String url = mImagesUrls.get(i);
        Glide.with(myContext)
                .asBitmap()
                .load(defaulturl+url)
                .into(viewHolder.image);
        viewHolder.name.setText(mNames.get(i));
        viewHolder.no.setText(String.valueOf(i+1));

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext, Details.class);
                intent.putExtra("id", myMap.get(+i).get("id"));
                intent.putExtra("title", myMap.get(+i).get("title"));
                intent.putExtra("backdrop_path", myMap.get(+i).get("backdrop_path"));
                intent.putExtra("release_date", myMap.get(+i).get("release_date"));
                intent.putExtra("poster_path", myMap.get(+i).get("poster_path"));
                intent.putExtra("genre_ids", myMap.get(+i).get("genre_ids"));
                intent.putExtra("overview", myMap.get(+i).get("overview"));
                myContext.startActivity(intent);

                //Toast.makeText(myContext,  myMap.get(+i).get("title"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name,no;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageview);
            name = itemView.findViewById(R.id.textview);
            no = itemView.findViewById(R.id.no);
        }
    }
}

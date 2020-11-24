package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> AlbumFiles;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.AlbumFiles = mFiles;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyHolder holder, int position) {
        holder.album_name.setText(AlbumFiles.get(position).getAlbum());
        byte[] image=getAlbumArt(AlbumFiles.get(position).getPath());
        if(image!=null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }
        else{
            Glide.with(mContext).asBitmap().load(R.drawable.no_album_art).into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,PlayerActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return AlbumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView album_name;
        ImageView album_art;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_name=itemView.findViewById(R.id.album_name);
            album_art=itemView.findViewById(R.id.album_image);
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}

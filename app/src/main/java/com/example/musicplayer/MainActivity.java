package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE=1;
   public static ArrayList<MusicFiles> musicFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();

    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext()
                , permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            musicFiles=getAllAudio(this);
            initViewPager();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission.WRITE_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    musicFiles=getAllAudio(this);
                    initViewPager();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this, "No media access", Toast.LENGTH_SHORT).show();
                   // ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void initViewPager() {
        ViewPager viewPager=findViewById(R.id.view_pager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongsFragment(),"songs");
        viewPagerAdapter.addFragment(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragments=new ArrayList<>();
            titles=new ArrayList<>();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudio=new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        if(cursor!=null){

            while(cursor.moveToNext()){

                String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String duration=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                MusicFiles musicFiles=new MusicFiles(title,album,path,artist,duration);
                Log.e("Path"+path,"Album "+album);
                tempAudio.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudio;
    }
}
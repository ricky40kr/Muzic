package com.example.muzic.util;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muzic.R;
import com.example.muzic.adapter.SongListAdapter;
import com.example.muzic.model.MediaPlayerSingleton;
import com.example.muzic.model.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noSongText;
    ArrayList<SongModel> songs=new ArrayList<>();
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noSongText=findViewById(R.id.no_song);
        recyclerView=findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC+" != 0"; // to get only music files from the storage

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        assert cursor!=null;
        while (cursor.moveToNext()) {
            Uri albumArt= ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),cursor.getLong(4));
            SongModel songData = new SongModel(cursor.getString(1), cursor.getString(0), cursor.getString(2), cursor.getString(3), albumArt);

            if (new File(songData.getPath()).exists()) {
                songs.add(songData);
            }
        }
        cursor.close();

        if (songs.size()==0){
            noSongText.setVisibility(View.VISIBLE);
        }else{
            // recycler View
            Collections.reverse(songs);
            SongListAdapter adapter=new SongListAdapter(songs, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
    }

    boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Storage Permission Needed!", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123); // requestCode added is random
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView!=null){
            recyclerView.setAdapter(new SongListAdapter(songs, getApplicationContext()));
            recyclerView.scrollToPosition(MediaPlayerSingleton.currIdx);
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);
        }
    }

    @Override
    protected void onPause() {
        recyclerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
        super.onPause();
    }
}

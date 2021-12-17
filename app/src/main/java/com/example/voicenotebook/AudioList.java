 package com.example.voicenotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioList extends AppCompatActivity {

    Button openRec, Play, Edit, Delete;
    EditText edit;

    ListView lv;
    List<String> Filename = new ArrayList<>();
    String pathSave ;
    private DBHandler dbHandler;
    MediaPlayer mediaPlayer;

    public void setFilename(String name) {
        Filename.add(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        lv = findViewById(R.id.lv);
        openRec = findViewById(R.id.OpenAdd);
        Play = findViewById(R.id.Play);
        Edit = findViewById(R.id.Edit);
        Delete = findViewById(R.id.Del);
        edit = findViewById(R.id.filename);

        dbHandler = new DBHandler(AudioList.this);
        Filename = dbHandler.getFilename();
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.activity_listview,Filename);
        lv.setAdapter(adapter);


        openRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AudioList.this, MainActivity.class);
                AudioList.this.startActivity(myIntent);
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString().isEmpty()){
                    Toast.makeText(AudioList.this, "Filename is empty", Toast.LENGTH_SHORT).show();
                }else {
                    if(Filename.contains(edit.getText().toString())){
                        mediaPlayer = new MediaPlayer();
                        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+edit.getText().toString()+".3gp";
                        try {
                            mediaPlayer.setDataSource(pathSave);
                            mediaPlayer.prepare();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }else{
                        Toast.makeText(AudioList.this, "File not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString().isEmpty()){
                    Toast.makeText(AudioList.this, "Filename is empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(Filename.contains(edit.getText().toString())){
                        Intent intent = new Intent(AudioList.this, EditActivity.class);
                        intent.putExtra("var",edit.getText().toString());
                        AudioList.this.startActivity(intent);
                    }else{
                        Toast.makeText(AudioList.this, "File not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString().isEmpty()){
                    Toast.makeText(AudioList.this, "Filename is empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(Filename.contains(edit.getText().toString())){
                        dbHandler.deleteFile(edit.getText().toString());
                        File fdelete = new File(pathSave);
                        fdelete.delete();
                        finish();
                        startActivity(getIntent());
                    }else{
                        Toast.makeText(AudioList.this, "File not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}
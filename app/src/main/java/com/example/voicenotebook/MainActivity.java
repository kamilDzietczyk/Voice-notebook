package com.example.voicenotebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button RecordBTN, StopRecBTN, PlayBTN, StopPlaydBTN, DeleteRecBTN, AddBTN, Back;
    EditText editText;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String name;
    DBHandler dbHandler;
    List<String> Filename = new ArrayList<>();

    final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DBHandler(MainActivity.this);
        if(!CheckPermisionFromDevice())
            requestPermission();
        if(dbHandler.getFilename()!=null){
            Filename = dbHandler.getFilename();
        }
        RecordBTN = findViewById(R.id.Record);
        StopRecBTN = findViewById(R.id.StopR);
        PlayBTN = findViewById(R.id.Play);
        StopPlaydBTN = findViewById(R.id.StopP);
        DeleteRecBTN = findViewById(R.id.Delete);
        AddBTN = findViewById(R.id.AddToList);
        editText = findViewById(R.id.filename);
        Back = findViewById(R.id.Back);

        StopRecBTN.setEnabled(false);
        PlayBTN.setEnabled(false);
        StopPlaydBTN.setEnabled(false);
        DeleteRecBTN.setEnabled(false);
        AddBTN.setEnabled(false);


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AudioList.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
            RecordBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "File name is empty", Toast.LENGTH_SHORT).show();
                    }else{
                        if(Filename.contains(editText.getText().toString())){
                            Toast.makeText(MainActivity.this, "File exist", Toast.LENGTH_SHORT).show();
                        }else{
                            RecordBTN.setEnabled(false);
                            StopRecBTN.setEnabled(true);
                            if(CheckPermisionFromDevice())
                            {
                                name = editText.getText().toString();
                                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+editText.getText().toString()+".3gp";
                                setupMediaRecorder();
                                try {
                                    mediaRecorder.prepare();
                                    mediaRecorder.start();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, pathSave, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                requestPermission();
                            }
                        }
                    }
                }
            });

            StopRecBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StopRecBTN.setEnabled(false);
                    PlayBTN.setEnabled(true);
                    StopPlaydBTN.setEnabled(true);
                    DeleteRecBTN.setEnabled(true);
                    AddBTN.setEnabled(true);
                    mediaRecorder.stop();
                }
            });

            PlayBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            });

            StopPlaydBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 mediaPlayer.stop();
                 mediaPlayer.release();
                 setupMediaRecorder();
                }
            });
            DeleteRecBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.release();
                    RecordBTN.setEnabled(true);
                    StopRecBTN.setEnabled(false);
                    PlayBTN.setEnabled(false);
                    StopPlaydBTN.setEnabled(false);
                    DeleteRecBTN.setEnabled(false);
                    AddBTN.setEnabled(false);
                }
            });
            AddBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.release();
                    dbHandler.addNewName(editText.getText().toString());
                    RecordBTN.setEnabled(true);
                    StopRecBTN.setEnabled(false);
                    PlayBTN.setEnabled(false);
                    StopPlaydBTN.setEnabled(false);
                    DeleteRecBTN.setEnabled(false);
                    AddBTN.setEnabled(false);
                    Intent myIntent = new Intent(MainActivity.this, AudioList.class);
                    MainActivity.this.startActivity(myIntent);
                }
            });
    }
    public void onClick(View view){

    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_CODE: 
            {
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean CheckPermisionFromDevice(){
        int write_external_stoarge_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_stoarge_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
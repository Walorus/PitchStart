package com.around8.pitchstart;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String [] possibleNotes = new String [] {"a","aflat","asharp","b","bflat","c","csharp",
            "d","dflat","dsharp","e","eflat","f","fsharp","g","gflat","gsharp"};


    private static final int REQUEST_CODE = 1234;
    /*private MediaPlayer[] songs = {MediaPlayer.create(this, R.raw.a), MediaPlayer.create(this, R.raw.gsharp),
            MediaPlayer.create(this, R.raw.asharp),
            MediaPlayer.create(this, R.raw.b), MediaPlayer.create(this, R.raw.asharp), MediaPlayer.create(this, R.raw.c),
            MediaPlayer.create(this, R.raw.csharp), MediaPlayer.create(this, R.raw.d), MediaPlayer.create(this, R.raw.csharp),
            MediaPlayer.create(this, R.raw.dsharp), MediaPlayer.create(this, R.raw.e), MediaPlayer.create(this, R.raw.dsharp),
            MediaPlayer.create(this, R.raw.f), MediaPlayer.create(this, R.raw.fsharp), MediaPlayer.create(this, R.raw.g),
            MediaPlayer.create(this, R.raw.fsharp), MediaPlayer.create(this, R.raw.gsharp)};*/
    private int [] songs = {R.raw.a,R.raw.gsharp,R.raw.asharp,R.raw.b,R.raw.asharp,R.raw.c,R.raw.csharp,R.raw.d,R.raw.csharp,
            R.raw.dsharp,R.raw.e,R.raw.dsharp,R.raw.f,R.raw.fsharp,R.raw.g,R.raw.fsharp,R.raw.gsharp};


    private ListView wordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button speakButton = (Button) findViewById(R.id.speakButton);

        wordsList = (ListView) findViewById(R.id.list);

        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }



    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();



    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "PitchStart");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private String [] splitNotes(ArrayList<String> x){ //splits the string of voice text into separated array


        String[] notes = x.get(0).split("&|and");


        for(int i=0;i<notes.length;i++){
            notes[i] = notes[i].replace(" ","");
            notes[i]=notes[i].toLowerCase();
            System.out.println(notes[i]);


        }
        return notes;

    }

    private String[] sortNotes(String [] x){ //checks if the string is a note, if not sets it as unknown
        for(int i =0;i<x.length;i++){
            boolean isIn = false;
            for(String checkNote:possibleNotes){
                if(checkNote.equals(x[i])){
                    isIn=true;
                }
            }
            if(!isIn){
                x[i] = "Note not recognized";
            }
        }
        return x;
    }
    private void playSong(int x){
        MediaPlayer note = MediaPlayer.create(this,x);
        note.start();
    }

    private void playPitches(String [] notes){

        //int lastNoteIndex=0;
        for(int i = 0; i < notes.length; i++){
            for(int j = 0; j<possibleNotes.length; j++){
                if(notes[i].equals(possibleNotes[j])){
                    playSong(songs[j]);
                    //lastNoteIndex=j;
                    SystemClock.sleep(1000);
                    break;
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            ArrayList<String> closest = new ArrayList<String>();
            closest.add(matches.get(0));
            String[] separatedText = splitNotes(closest);
            String [] notes = sortNotes(separatedText);
            ArrayList<String> outputNotes = new ArrayList<String>();
            for(int i =0;i<notes.length;i++) { //changes the notes array into an arrayList to satisfy function below
                outputNotes.add(notes[i]);
            }
            playPitches(notes);

            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    outputNotes)); //sets the viewList on the app to the arrayList as the 3rd parameter

        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}

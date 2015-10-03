package com.around8.pitchstart;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String [] possibleNotes = new String [] {"a","aflat","asharp","b","bflat","bsharp","c","cflat","csharp",
            "d","dflat","dsharp","e","eflat","esharp","f","fflat","fsharp","g","gflat","gsharp"};
    private static final int REQUEST_CODE = 1234;
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
            System.out.println(notes[i]);

        }
        return notes;

    }

    private String[] sortNotes(String [] x){ //checks if the string is a note, if not sets it as unknown
        for(int i =0;i<x.length;i++){
            for(String checkNote:possibleNotes){
                if(checkNote.equals(x[i].toLowerCase())){
                }else {
                    x[i] = "Note not recognized";
                }
            }
        }
        return x;
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
            String[] notes = splitNotes(closest);
            notes = sortNotes(notes);
            ArrayList<String> outputNotes = new ArrayList<String>();
            for(int i =0;i<notes.length;i++) { //changes the notes array into an arrayList to satisfy function below
                outputNotes.add(notes[i]);
            }
            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    outputNotes)); //sets the viewList on the app to the arrayList as the 3rd parameter
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}

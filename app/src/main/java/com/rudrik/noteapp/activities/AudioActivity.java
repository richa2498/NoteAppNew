package com.rudrik.noteapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.rudrik.noteapp.R;
import com.rudrik.noteapp.models.Note;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.rudrik.noteapp.MyApplication.SEL_NOTE;
import static com.rudrik.noteapp.MyApplication.editor;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.activities.NotesActivity.REQ_CODE_NOTE;

public class AudioActivity extends AppCompatActivity {

    private Note selNote;
    public static String MY_TRACKS = "Note_tracks";

    HashSet<String> mSet = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

//        checkNote();
    }

    private void checkNote() {
        selNote = (Note) getIntent().getSerializableExtra(SEL_NOTE);
        if (selNote != null) {
            System.out.println("from note : " + selNote.getTitle());
        }
        if (selNote.getAudios() != null) {
            for (String audio : selNote.getAudios()) {
                mSet.add(audio);
            }
        }

        editor.putStringSet(MY_TRACKS, mSet);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        List<String> list = new ArrayList<>();
//        for (String s : prefs.getStringSet(MY_TRACKS, new HashSet<>())) {
//            list.add(s);
//        }
//        selNote.setAudios(list);
//
//        intent.putExtra(SEL_NOTE, selNote);
//        setResult(20, intent);
        super.onBackPressed();
    }
}

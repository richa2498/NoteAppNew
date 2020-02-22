package com.rudrik.noteapp.adapters;

import android.content.Context;
import android.content.Intent;

import com.rudrik.noteapp.R;
import com.rudrik.noteapp.activities.AddNoteActivity;
import com.rudrik.noteapp.activities.AudioActivity;
import com.rudrik.noteapp.models.Note;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

import static com.rudrik.noteapp.MyApplication.SEL_NOTE;
import static com.rudrik.noteapp.MyApplication.editor;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.activities.AudioActivity.MY_TRACKS;

public class AdptAudioSpeedDial extends SpeedDialMenuAdapter {

    Context context;
    Note selNote;
    List<SpeedDialMenuItem> list = new ArrayList<>();

    public AdptAudioSpeedDial(Context context, Note note) {
        this.context = context;
        this.selNote = note;

        //  0
        SpeedDialMenuItem mic = new SpeedDialMenuItem(context);
        mic.setIcon(R.drawable.ic_mic);
        mic.setLabel("Record");

        //  1
        SpeedDialMenuItem tracks = new SpeedDialMenuItem(context);
        tracks.setIcon(R.drawable.ic_file);
        tracks.setLabel("Tracks");

        list.add(mic);
//        list.add(tracks);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NotNull
    @Override
    public SpeedDialMenuItem getMenuItem(@NotNull Context context, int i) {
        return list.get(i);
    }

    @Override
    public boolean onMenuItemClick(int position) {

        switch (position){
            case 0 :
                //  mic
                Intent i = new Intent(context, AudioActivity.class);

//                i.putExtra(SEL_NOTE, selNote);
                context.startActivity(i);
                break;

            case 1 :
                //  tracks

                break;
        }

        return super.onMenuItemClick(position);
    }
}

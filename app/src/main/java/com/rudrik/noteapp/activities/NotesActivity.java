package com.rudrik.noteapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rudrik.noteapp.MyApplication;
import com.rudrik.noteapp.R;
import com.rudrik.noteapp.adapters.AdptMyFolders;
import com.rudrik.noteapp.adapters.AdptMyNotes;
import com.rudrik.noteapp.models.Folder;
import com.rudrik.noteapp.models.Note;

import java.util.ArrayList;
import java.util.List;

import static com.rudrik.noteapp.MyApplication.NOTES_DATA_CHANGES;
import static com.rudrik.noteapp.MyApplication.SEL_FOLDER;
import static com.rudrik.noteapp.MyApplication.SEL_NOTE;
import static com.rudrik.noteapp.MyApplication.bg;
import static com.rudrik.noteapp.MyApplication.db;
import static com.rudrik.noteapp.MyApplication.editor;
import static com.rudrik.noteapp.MyApplication.main;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.models.Folder.myFolders;
import static com.rudrik.noteapp.models.Note.myNotes;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, TextWatcher {

    public static int REQ_CODE_NOTE = 10;

    private AppBarLayout appBar;
    private MaterialToolbar toolbar;
    private EditText edtSearch;
    private RecyclerView recyclerNotes;
    private FloatingActionButton fabAdd;

    private Folder selFolder;
    private AdptMyNotes adpt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        init();
        checkFolder();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myNotes.clear();
        if (selFolder != null) {
            MyApplication.noteUpdates(this, selFolder.getId());
        }
    }

    private void checkFolder() {
        selFolder = (Folder) getIntent().getSerializableExtra(SEL_FOLDER);
        if (selFolder != null) {
            bg.execute(() -> {
                List<Note> list = db.notesDao().getFolderNotes(selFolder.getId());

                if (!list.isEmpty()) {
                    main.execute(() -> recyclerNotes.swapAdapter(new AdptMyNotes(this, list), true));
                }
            });
        }
    }

    private void init() {
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        recyclerNotes = (RecyclerView) findViewById(R.id.recyclerView);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtSearch.addTextChangedListener(this);

        adpt = new AdptMyNotes(this, new ArrayList<>());
        recyclerNotes.setHasFixedSize(true);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setAdapter(adpt);

        fabAdd.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add) {
            Intent i = new Intent(this, AddNoteActivity.class);
            i.putExtra(SEL_FOLDER, selFolder);
            startActivityForResult(i, REQ_CODE_NOTE);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ON_ACTIVITY_RES", "TRUE");

        if (requestCode == REQ_CODE_NOTE) {
            Note note = null;
            if (data != null) {
                note = (Note) data.getSerializableExtra(SEL_NOTE);
            }

            if (note != null) {
                note.setfId(selFolder.getId());
                if (!myNotes.contains(note)) {
                    Note finalNote = note;
                    bg.execute(() -> {
                        db.notesDao().insertNote(finalNote);

                        List<Note> list = db.notesDao().getFolderNotes(selFolder.getId());

                        if (!list.isEmpty() && !list.equals(myNotes)) {
                            main.execute(() -> recyclerNotes.swapAdapter(new AdptMyNotes(this, list), true));
                        }

                    });
                }else{
                    Note finalNote = note;
                    bg.execute(() -> {
                        db.notesDao().updateNote(finalNote);

                        List<Note> list = db.notesDao().getFolderNotes(selFolder.getId());

                        if (!list.isEmpty() && !list.equals(myNotes)) {
                            main.execute(() -> recyclerNotes.swapAdapter(new AdptMyNotes(this, list), true));
                        }

                    });
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(NOTES_DATA_CHANGES)) {
            if (preferences.getBoolean(NOTES_DATA_CHANGES, false)) {
                Log.e(NOTES_DATA_CHANGES, String.valueOf(preferences.getBoolean(NOTES_DATA_CHANGES, false)));
                if (adpt != null) {
                    recyclerNotes.swapAdapter(new AdptMyNotes(this, myNotes), true);

                    editor.putBoolean(NOTES_DATA_CHANGES, false);
                    editor.apply();
                }
            }
        }
    }

    //  on search

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        return;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        edtSearch.removeTextChangedListener(this);

        if (!TextUtils.isEmpty(s)) {
            List<Note> list = new ArrayList<>();
            for (Note note : myNotes) {
                if (note.getTitle().contains(s.toString()) || note.getDesc().contains(s.toString())) {
                    list.add(note);
                }
            }

            if (!list.isEmpty()) {
                recyclerNotes.swapAdapter(new AdptMyNotes(this, list), true);
            } else {
                recyclerNotes.swapAdapter(new AdptMyNotes(this, new ArrayList<>()), true);
            }
        } else {
            recyclerNotes.swapAdapter(new AdptMyNotes(this, myNotes), true);
        }
        edtSearch.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        return;
    }
}

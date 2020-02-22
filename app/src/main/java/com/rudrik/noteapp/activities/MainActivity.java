package com.rudrik.noteapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rudrik.noteapp.MyApplication;
import com.rudrik.noteapp.R;
import com.rudrik.noteapp.adapters.AdptMyFolders;
import com.rudrik.noteapp.models.Folder;
import com.rudrik.noteapp.models.Note;

import java.util.ArrayList;
import java.util.List;

import static com.rudrik.noteapp.MyApplication.FOLDER_DATA_CHANGES;
import static com.rudrik.noteapp.MyApplication.bg;
import static com.rudrik.noteapp.MyApplication.db;
import static com.rudrik.noteapp.MyApplication.editor;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.models.Folder.myFolders;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, TextWatcher {

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private EditText edtSearch;
    private RecyclerView recyclerFolders;
    private FloatingActionButton fab_add;

    private AdptMyFolders adpt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        new MyApplication().folderUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void init() {
//        getData();
//        setData();
//        deleteData();

        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        recyclerFolders = (RecyclerView) findViewById(R.id.recyclerView);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        toolbar.setTitle("Folders");
        edtSearch.addTextChangedListener(this);

        adpt = new AdptMyFolders(this, new ArrayList<>());
        recyclerFolders.setHasFixedSize(true);
        recyclerFolders.setLayoutManager(new LinearLayoutManager(this));
        recyclerFolders.setAdapter(adpt);

        fab_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add) {
            addNewFolder();
        }
    }

    private void addNewFolder() {

        View v = getLayoutInflater().inflate(R.layout.dialog_add_folder, null);

        ImageView btnClose = v.findViewById(R.id.diag_btn_close);

        Button btnAdd = v.findViewById(R.id.diag_btn_add);

        TextView txtTitle = v.findViewById(R.id.diag_title);
        txtTitle.setText(R.string.add_folder);

        EditText edt = v.findViewById(R.id.diag_edtName);
        edt.setHint("folder name");
        edt.requestFocus();

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(v)
                .create();

        btnClose.setOnClickListener(v1 -> alert.dismiss());
        btnAdd.setOnClickListener(v1 -> {
            bg.execute(() -> {
                db.foldersDao().insertFolder(new Folder(edt.getText().toString()));
            });
            edt.clearFocus();
            alert.dismiss();
        });

        alert.show();
    }

    private void setData() {
        //  create folder

        Folder f1 = new Folder("folder1");
        Folder f2 = new Folder("folder2");
        Folder f3 = new Folder("folder3");

        //  Add Folders

        bg.execute(() -> {
            db.foldersDao().insertFolder(f1);
            db.foldersDao().insertFolder(f2);
            db.foldersDao().insertFolder(f3);


            //  Add Notes

            Note n1 = new Note(
                    3,
                    "note1",
                    "hello note 1",
                    "19/02/20",
                    43.0f,
                    -79.0f,
                    new ArrayList<>(),
                    new ArrayList<>()
            );


            Note n2 = new Note(
                    3,
                    "note2",
                    "hello note 2",
                    "20/02/20",
                    44.0f,
                    -80.0f,
                    new ArrayList<>(),
                    new ArrayList<>()
            );


            Note n3 = new Note(
                    3,
                    "note3",
                    "hello note 3",
                    "21/02/20",
                    45.0f,
                    -81.0f,
                    new ArrayList<>(),
                    new ArrayList<>()
            );


            db.notesDao().insertNote(n1);
            db.notesDao().insertNote(n2);
            db.notesDao().insertNote(n3);
        });


//        bg.execute(() -> {
//            db.notesDao().insertNote(n1);
//            db.notesDao().insertNote(n2);
//            db.notesDao().insertNote(n3);
//        });

    }

    private void deleteData() {

        //  delete Folder

        new Handler().postDelayed(() -> {
            bg.execute(() -> {
                db.foldersDao().deleteFolder(myFolders.get(2));
            });
        }, 4000);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        Log.e("KEY", key);
        if (key.equals(FOLDER_DATA_CHANGES)) {
            if (preferences.getBoolean(FOLDER_DATA_CHANGES, false)) {
                Log.e(FOLDER_DATA_CHANGES, String.valueOf(preferences.getBoolean(FOLDER_DATA_CHANGES, false)));
                if (adpt != null) {
                    recyclerFolders.swapAdapter(new AdptMyFolders(this, myFolders), true);

                    editor.putBoolean(FOLDER_DATA_CHANGES, false);
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
            List<Folder> list = new ArrayList<>();
            for (Folder folder : myFolders) {
                if (folder.getfName().contains(s.toString())) {
                    list.add(folder);
                }
            }

            if (!list.isEmpty()) {
                recyclerFolders.swapAdapter(new AdptMyFolders(this, list), true);
            } else {
                recyclerFolders.swapAdapter(new AdptMyFolders(this, new ArrayList<>()), true);
            }
        }else{
            recyclerFolders.swapAdapter(new AdptMyFolders(this, myFolders), true);
        }
        edtSearch.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        return;
    }
}

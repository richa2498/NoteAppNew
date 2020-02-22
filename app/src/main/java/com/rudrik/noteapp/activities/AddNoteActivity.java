package com.rudrik.noteapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.core.content.FileProvider;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.rudrik.noteapp.MyApplication;
import com.rudrik.noteapp.R;
import com.rudrik.noteapp.adapters.AdptAudioSpeedDial;
import com.rudrik.noteapp.adapters.AdptImagesSpeedDial;
import com.rudrik.noteapp.models.Note;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuOpenListener;

import static com.rudrik.noteapp.MyApplication.LOC_REQ_CODE;
import static com.rudrik.noteapp.MyApplication.SEL_NOTE;
import static com.rudrik.noteapp.MyApplication.editor;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.MyApplication.statusCheck;
import static com.rudrik.noteapp.activities.AudioActivity.MY_TRACKS;
import static com.rudrik.noteapp.activities.NotesActivity.REQ_CODE_NOTE;

public class AddNoteActivity extends AppCompatActivity implements SpeedDialMenuOpenListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Note selNote;
    private LatLng userLocation = null;

    private List<String> images;
    private List<String> audios;

    private AppBarLayout appBar;
    private MaterialToolbar toolbar;
    private EditText edtSearch;

    private TextInputEditText edtNtitle;
    private AppCompatMultiAutoCompleteTextView edtNdesc;

    private FloatingActionButton fabAudioAction;
    private FloatingActionButton fabImageAction;
    private FloatingActionButton fabDirectionAction;

    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init();
    }

    private void init() {
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        edtNtitle = (TextInputEditText) findViewById(R.id.edtNtitle);
        edtNdesc = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.edtNdesc);

        fabAudioAction = (FloatingActionButton) findViewById(R.id.fab_audio_action);
        fabImageAction = (FloatingActionButton) findViewById(R.id.fab_image_action);
        fabDirectionAction = (FloatingActionButton) findViewById(R.id.fab_direction_action);

        toolbar.setTitle("Add new Note");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkNote();

        edtSearch.setVisibility(View.GONE);

        fabAudioAction.setOnSpeedDialMenuOpenListener(this);
        fabAudioAction.setSpeedDialMenuAdapter(new AdptAudioSpeedDial(this, selNote));

        fabImageAction.setOnSpeedDialMenuOpenListener(this);
        fabImageAction.setSpeedDialMenuAdapter(new AdptImagesSpeedDial(this, selNote));

        fabDirectionAction.setOnClickListener(v -> {
            // on direction click
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra(SEL_NOTE, selNote);
            startActivity(i);
        });

        edtNtitle.setText(selNote.getTitle());
        edtNdesc.setText(selNote.getDesc());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("LOCATION_SERVICE", String.valueOf(statusCheck(this)));

        if (selNote.getLat() == 0f || selNote.getLng() == 0f) {
            if (checkPermissions()) {
                MyApplication.initUserLocation(this);
            }
        }

        userLocation = new LatLng(MyApplication.getPrefs().getFloat("U_LAT", 0f), MyApplication.getPrefs().getFloat("U_LNG", 0f));
        if (selNote.getLat() == 0f) {
            selNote.setLat((float) userLocation.latitude);
        }
        if (selNote.getLng() == 0f) {
            selNote.setLng((float) userLocation.longitude);
        }
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkNote() {
        selNote = (Note) getIntent().getSerializableExtra(SEL_NOTE);
        if (selNote != null) {
            System.out.println("from note : " + selNote.getTitle());
            toolbar.setTitle("Edit Note");

            if (images == null) {
                images = new ArrayList<>();
            }

            if (audios == null) {
                audios = new ArrayList<>();
            }
        } else {
            selNote = new Note();
            images = new ArrayList<>();
            audios = new ArrayList<>();
        }


        HashSet<String> set = new HashSet<>();
        if (selNote.getAudios() != null) {
            for (String audio : selNote.getAudios()) {
                set.add(audio);
            }
        }
        editor.putStringSet(MY_TRACKS, set);
        editor.apply();

    }

    @SuppressLint("NewApi")
    private boolean checkPermissions() {
        boolean isPermitted = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;
        if (!isPermitted) {
            requestPermission();
        }
        return isPermitted;
    }

    @SuppressLint("NewApi")
    private void requestPermission() {
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == LOC_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Application", "Location permission has been granted");
            }

            if (requestCode == LOC_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                this.finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onOpen(@NotNull FloatingActionButton fab) {
        switch (fab.getId()) {
            case R.id.fab_image_action:
                System.out.println("fab_image_action");
                break;

            case R.id.fab_audio_action:
                System.out.println("fab_audio_action");
                break;

            case R.id.fab_direction_action:
                System.out.println("fab_direction_action");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {

                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "Note_" + timeStamp + ".png";

                try {
                    //create a file to write bitmap data
                    File f = new File(getCacheDir(), fileName);
                    f.createNewFile();

//Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    Log.e("SAVING PATH", f.getAbsolutePath());
                    //  saving path

                    images.add(f.getAbsolutePath());
                    selNote.setImages(images);

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                ((ImageView)findViewById(R.id.mImageView)).setImageBitmap(imageBitmap);
            }
        } else if (requestCode == 2) {
            if (data != null && data.getExtras() != null) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor cursor = null;
                String picturePath = "";
                try {
                    cursor = getContentResolver().query(selectedImage,
                            null,
                            null,
                            null,
                            null);

                    if (cursor != null && cursor.moveToFirst()) {
                        int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        picturePath = cursor.getString(displayNameIndex);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                if (!picturePath.isEmpty()) {
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    images.add(picturePath);
                    selNote.setImages(images);
                    Toast.makeText(this, "Image" + picturePath, Toast.LENGTH_SHORT).show();
                }
//            viewImage.setImageBitmap(thumbnail);
            }
        }

        if (requestCode == 20) {
            if (data != null && data.getExtras() != null) {
                selNote = (Note) data.getSerializableExtra(SEL_NOTE);
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onBackPressed() {
        selNote.setTitle(edtNtitle.getText().toString());
        selNote.setDesc(edtNdesc.getText().toString());
        selNote.setImages(images);

        HashSet<String> set = (HashSet<String>) prefs.getStringSet(MY_TRACKS, new HashSet<String>());
        if (!set.isEmpty()){
            for (String s : set) {
                audios.add(s);
            }
        }

        selNote.setAudios(audios);

        if (selNote.getTitle().isEmpty() && selNote.getDesc().isEmpty()) {
            if (selNote.getTitle().isEmpty()) {
                edtNtitle.setError("No title!");
            }
            if (selNote.getDesc().isEmpty()) {
                edtNdesc.setError("No description!");
            }

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

        } else {
            Intent intent = new Intent();
            intent.putExtra(SEL_NOTE, selNote);
            setResult(REQ_CODE_NOTE, intent);
            super.onBackPressed();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("U_LAT") || key.equals("U_LNG")) {
            userLocation = new LatLng(prefs.getFloat("U_LAT", 0f), prefs.getFloat("U_LNG", 0f));
            if (selNote.getLat() == 0f) {
                selNote.setLat((float) userLocation.latitude);
            }
            if (selNote.getLng() == 0f) {
                selNote.setLng((float) userLocation.longitude);
            }
            Log.e("USER_LOCATION", userLocation.toString());
        }
    }
}

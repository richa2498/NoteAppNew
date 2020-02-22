package com.rudrik.noteapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import com.rudrik.noteapp.R;
import com.rudrik.noteapp.activities.AddNoteActivity;
import com.rudrik.noteapp.activities.ImageSliderActivity;
import com.rudrik.noteapp.models.Note;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

import static com.rudrik.noteapp.MyApplication.SEL_NOTE;

public class AdptImagesSpeedDial extends SpeedDialMenuAdapter {
    private String currentPhotoPath;
    Context context;
    Note selNote;
    List<SpeedDialMenuItem> list = new ArrayList<>();

    public AdptImagesSpeedDial(Context context, Note note) {
        this.context = context;
        this.selNote = note;
        //  0
        SpeedDialMenuItem camera = new SpeedDialMenuItem(context);
        camera.setIcon(R.drawable.ic_camera);
        camera.setLabel("Camera");

        //  1
        SpeedDialMenuItem gallery = new SpeedDialMenuItem(context);
        gallery.setIcon(R.drawable.ic_image);
        gallery.setLabel("Gallery");

        //  2
        SpeedDialMenuItem images = new SpeedDialMenuItem(context);
        images.setIcon(R.drawable.ic_file);
        images.setLabel("Images");

        list.add(camera);
        list.add(gallery);
        list.add(images);
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

        switch (position) {
            case 0:
                // camera

                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );
                if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
//                    File f = null;
//                    String filename = "NOTE_IMAGE";
//                    File imageFile = null;
//                    try {
//                        f = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                        imageFile = File.createTempFile(filename, ".jpg", f);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    currentPhotoPath = imageFile.getAbsolutePath();
//                    Uri uri = FileProvider.getUriForFile(context, "com.rudrik.noteapp.fileprovider", imageFile);
//                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    ((AddNoteActivity) context).startActivityForResult(pictureIntent, 11);
                }
//                File f = null;
//                File imageFile = null;
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String filename = "JPEG_" + timeStamp + "_";
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                f = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//                try {
//                    imageFile  = File.createTempFile(filename,".jpg",f);
//                    currentPhotoPath = imageFile.getAbsolutePath();
//                    Uri uri = FileProvider.getUriForFile(context,"com.rudrik.noteapp.fileprovider",imageFile);
//                    intent.putExtra("PATH", imageFile.toString());
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    ((AddNoteActivity)context).startActivityForResult(intent, 11);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Filepath"+imageFile.toString());
//

                break;

            case 1:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                ((Activity) context).startActivityForResult(intent, 2);

//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);

                break;

            case 2:
                //  images

                Intent i = new Intent(context, ImageSliderActivity.class);
                i.putExtra(SEL_NOTE, selNote);
                context.startActivity(i);
                break;
        }

        return super.onMenuItemClick(position);
    }
}

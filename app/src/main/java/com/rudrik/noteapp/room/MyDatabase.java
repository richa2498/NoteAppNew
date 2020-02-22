package com.rudrik.noteapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rudrik.noteapp.models.Folder;
import com.rudrik.noteapp.models.Note;

@Database(entities = {Folder.class, Note.class}, exportSchema = false, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DB_NAME = "note_app_db";
    private static MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract FoldersDao foldersDao();

    public abstract NotesDao notesDao();
}

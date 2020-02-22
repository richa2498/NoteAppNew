package com.rudrik.noteapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.rudrik.noteapp.models.Note;

import java.util.List;

@Dao
public interface NotesDao {


    @Query("select * from note where fId = :fId")
    List<Note> getFolderNotes(int fId);

    @Query("select * from note where fId = :fId")
    LiveData<List<Note>> getLiveNotes(int fId);

    @RawQuery
    List<Note> findNote(SupportSQLiteQuery query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Update
    int updateNote(Note note);

    @Delete
    int deleteNote(Note note);

}

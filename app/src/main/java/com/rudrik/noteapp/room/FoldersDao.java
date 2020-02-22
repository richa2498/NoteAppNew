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

import com.rudrik.noteapp.models.Folder;

import java.util.List;

@Dao
public interface FoldersDao {

//    @Query("select * from folder")
//    List<Folder> getFolderList();

    @Query("select id, folder_name, (select count(*) from note n where f.id=n.fId ) total_notes from folder f")
    LiveData<List<Folder>> getLiveFolders();

    @RawQuery
    List<Folder> findFolder(SupportSQLiteQuery query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(Folder folder);

    @Update
    int updateFolder(Folder folder);

    @Delete
    int deleteFolder(Folder folder);

}

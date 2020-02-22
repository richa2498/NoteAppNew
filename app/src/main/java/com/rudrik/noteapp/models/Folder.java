package com.rudrik.noteapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "folder")
public class Folder implements Serializable {

    public static List<Folder> myFolders = new ArrayList<>();

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "folder_name")
    public String fName;

    @ColumnInfo(name = "total_notes")
    public long totalNotes = 0;

    public Folder(int id, String fName, long totalNotes) {
        this.id = id;
        this.fName = fName;
        this.totalNotes = totalNotes;
    }

    @Ignore
    public Folder(String fName) {
        this.fName = fName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public long getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(long totalNotes) {
        this.totalNotes = totalNotes;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", fName='" + fName + '\'' +
                ", totalNotes=" + totalNotes +
                '}';
    }
}

package com.rudrik.noteapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "note", foreignKeys = @ForeignKey(entity = Folder.class, parentColumns = "id", childColumns = "fId", onDelete = ForeignKey.CASCADE), indices = {@Index("fId")})
@TypeConverters({Converters.class})
public class Note implements Serializable {

    public static List<Note> myNotes = new ArrayList<>();

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "fId")
    public int fId;

    @ColumnInfo(name = "title")
    String title = "";

    @ColumnInfo(name = "desc")
    String desc = "";

    @ColumnInfo(name = "dt")
    String dt;

    @ColumnInfo(name = "lat")
    float lat = 0f;

    @ColumnInfo(name = "lng")
    float lng = 0f;

    @ColumnInfo(name = "images")
    List<String> images;

    @ColumnInfo(name = "audios")
    List<String> audios;

    public Note(int id, int fId, String title, String desc, String dt, float lat, float lng, List<String> images, List<String> audios) {
        this.id = id;
        this.fId = fId;
        this.title = title;
        this.desc = desc;
        this.dt = dt;
        this.lat = lat;
        this.lng = lng;
        this.images = images;
        this.audios = audios;
    }

    @Ignore
    public Note(int fId, String title, String desc, String dt, float lat, float lng, List<String> images, List<String> audios) {
        this.fId = fId;
        this.title = title;
        this.desc = desc;
        this.dt = dt;
        this.lat = lat;
        this.lng = lng;
        this.images = images;
        this.audios = audios;
    }

    @Ignore
    public Note(String title, String desc, String dt, float lat, float lng, List<String> images, List<String> audios) {
        this.title = title;
        this.desc = desc;
        this.dt = dt;
        this.lat = lat;
        this.lng = lng;
        this.images = images;
        this.audios = audios;
    }

    @Ignore
    public Note() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getAudios() {
        return audios;
    }

    public void setAudios(List<String> audios) {
        this.audios = audios;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", fId=" + fId +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", dt='" + dt + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", images=" + images +
                ", audios=" + audios +
                '}';
    }
}



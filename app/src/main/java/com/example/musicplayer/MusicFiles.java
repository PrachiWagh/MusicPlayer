package com.example.musicplayer;

public class MusicFiles {
    private String title;
    private String album;
    private String path;
    private String artist;
    private String duration;

    public MusicFiles(String title, String album, String path, String artist, String duration) {
        this.title = title;
        this.album = album;
        this.path = path;
        this.artist = artist;
        this.duration = duration;
    }

    public MusicFiles() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

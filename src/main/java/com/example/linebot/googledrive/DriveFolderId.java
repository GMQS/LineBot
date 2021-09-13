package com.example.linebot.googledrive;

public class DriveFolderId {
    private String rootFolderId;
    private String parentFolderId;
    private String imageFolderId;
    private String videoFolderId;
    private String audioFolderId;
    private String fileFolderId;

    public DriveFolderId(){
    }

    public String getRootFolderId() {
        return rootFolderId;
    }
    public boolean setRootFolderId(String rootFolderId) {
        if(rootFolderId == null){
            return false;
        }
        this.rootFolderId = rootFolderId;
        return true;
    }
    public String getParentFolderId() {
        return parentFolderId;
    }
    public boolean setParentFolderId(String parentFolderId) {
        if(parentFolderId == null){
            return false;
        }
        this.parentFolderId = parentFolderId;
        return true;
    }

    public String getImageFolderId() {
        return imageFolderId;
    }
    public boolean setImageFolderId(String imageFolderId) {
        if(imageFolderId == null){
            return false;
        }
        this.imageFolderId = imageFolderId;
        return true;
    }
    public String getVideoFolderId() {
        return videoFolderId;
    }
    public boolean setVideoFolderId(String videoFolderId) {
        if(videoFolderId == null){
            return false;
        }
        this.videoFolderId = videoFolderId;
        return true;
    }
    public String getAudioFolderId() {
        return audioFolderId;
    }
    public boolean setAudioFolderId(String audioFolderId) {
        if(audioFolderId == null){
            return false;
        }
        this.audioFolderId = audioFolderId;
        return true;
    }
    public String getFileFolderId() {
        return fileFolderId;
    }
    public boolean setFileFolderId(String fileFolderId) {
        if(fileFolderId == null){
            return false;
        }
        this.fileFolderId = fileFolderId;
        return true;
    }

    
}

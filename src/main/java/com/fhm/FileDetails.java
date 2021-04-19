package com.fhm;

import java.io.File;

public class FileDetails {
    private String fileName;
    private String fileExtension;
    private long fileSize;
    private long fileModificationDate;

    public FileDetails(File file) {
        extractFileName(file);
        extractFileExtension(file);
        extractFileSize(file);
        extractFileModificationDate(file);


    }
    private void extractFileName(File file) {
        this.fileName = file.getName();
    }

    private void extractFileExtension(File file) {
        String fileName = file.getName();

        int index = fileName.lastIndexOf(".");
        this.fileExtension = fileName.substring(index + 1);
    }

    private void extractFileSize(File file){
        this.fileSize = file.length();
    }

    private void extractFileModificationDate(File file){
        this.fileModificationDate = file.lastModified();
    }



    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getFileModificationDate() {
        return fileModificationDate;
    }


}

package com.downloader.get;

public interface DownloadProgressCallback {
    void onProgress(int progress);   // Called to update progress
    void onComplete();               // Called when the download is finished
    void onError(String error);      // Called if there's an error
}

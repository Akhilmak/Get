package com.downloader.get;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {

    private static final String PERMISSION_STORAGE = Manifest.permission.READ_MEDIA_VIDEO;
    private static final int PERMISSION_REQ_CODE = 100;
    private EditText urlInput;
    private Button downloadButton;
    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        urlInput = findViewById(R.id.urlInput);
        downloadButton = findViewById(R.id.downloadButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        requestRuntimePermissions();

        downloadButton.setOnClickListener(v -> {
            String videoUrl = urlInput.getText().toString();
            if (!videoUrl.isEmpty()) {
                downloadBestQuality(videoUrl);
            } else {
                Toast.makeText(this, "Please enter a video URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_STORAGE}, PERMISSION_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission required to download videos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadBestQuality(String url) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("download_best_quality");

        pyObject.callAttr("download_best_quality", url, (ProgressCallback) progress -> runOnUiThread(() -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressText.setText(progress + "%");
            progressBar.setProgress(progress);
        }));

        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
    }

    interface ProgressCallback {
        void onProgressUpdate(int progress);
    }
}

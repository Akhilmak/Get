package com.downloader.get;

import static kotlin.jvm.internal.Reflection.typeOf;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String PERMISSION_STORAGE = Manifest.permission.READ_MEDIA_VIDEO;
    private static final int PERMISSION_REQ_CODE = 100;

    private EditText urlInput;
    private Button downloadButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private Spinner formatSpinner;  // Spinner to choose the format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Python environment
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        // Initialize UI elements
        urlInput = findViewById(R.id.urlInput);
        downloadButton = findViewById(R.id.downloadButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        formatSpinner = findViewById(R.id.formatSpinner);

        // Request runtime permissions for storage
        requestRuntimePermissions();

        // Handle button click
        downloadButton.setOnClickListener(v -> {
            String videoUrl = urlInput.getText().toString();
            if (!videoUrl.isEmpty()) {
                listFormats(videoUrl);  // Fetch formats when the URL is entered
            } else {
                Toast.makeText(this, "Please enter a video URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Request runtime permissions for storage access
    private void requestRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_STORAGE}, PERMISSION_REQ_CODE);
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission required to download videos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void listFormats(String url) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("download_best_quality");
        PyObject formats = pyObject.callAttr("list_formats", url);
        if (formats == null) {
            Toast.makeText(this, "Error retrieving formats from Python", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<PyObject> l1=new ArrayList<>(formats.asList());
        System.out.println(l1.get(0).getClass());
        System.out.println(l1.get(0).get("format_id"));

//        ArrayList<String> formatOptions = new ArrayList<>();
//        ArrayList<String> formatIds = new ArrayList<>();
//
//        if (formats.asList().isEmpty()) {
//            Toast.makeText(this, "No formats available for this video", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        for (PyObject format : formats.asList()) {
//            // Accessing dictionary elements using get(key)
//            System.out.println(Objects.requireNonNull(format.get("format_id")).toString());
//            String formatId = safeGetString(format, "format_id");
//            String ext = safeGetString(format, "ext");
//
//            // Height is expected to be an integer; retrieve it as an int and convert to String
//            String height;
//            try {
//                PyObject heightObj = format.get("height");
//                height = (heightObj != null) ? String.valueOf(heightObj.toInt()) : "N/A"; // Convert Integer to String
//            } catch (Exception e) {
//                height = "N/A"; // Handle any exceptions and set a default value
//            }
//
//            String formatNote = safeGetString(format, "format_note");
//            String urlValue = safeGetString(format, "url");         // Accessing by key
//
//            // Debug log for each format after retrieving values
//            Log.d("Format Debug", "formatId: " + formatId + ", height: " + height + ", ext: " + ext + ", formatNote: " + formatNote + ", url: " + urlValue);
//
//            if (!urlValue.isEmpty()) {  // Only add formats with valid URLs
//                String formatInfo = "Format ID: " + formatId + "\nResolution: " + height +
//                        "\nExtension: " + ext + "\nDescription: " + formatNote;
//
//                formatOptions.add(formatInfo);
//                formatIds.add(formatId);
//            }
//        }
//
//
//
//
//
//        // If no formats found, show an error message
//        if (formatOptions.isEmpty()) {
//            Toast.makeText(this, "No formats available for this video.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Populate the Spinner with available formats
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, formatOptions);
//        formatSpinner.setAdapter(adapter);
//
//        // Set a listener to handle selection
//        downloadButton.setOnClickListener(v -> {
//            int selectedPosition = formatSpinner.getSelectedItemPosition();
//            if (selectedPosition != -1) {
//                // Get the selected format ID
//                String selectedFormatId = formatIds.get(selectedPosition);
//                downloadSelectedFormat(url, selectedFormatId);  // Download the selected format
//            } else {
//                Toast.makeText(this, "Please select a format", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // Helper method to safely get values from a PyObject
    private String safeGetString(PyObject obj, String key) {

        if (obj == null || obj.get(key) == null) {
            return "N/A";  // Return a default value if the key doesn't exist or is null
        }
        PyObject valueObj = obj.get(key);
        if (valueObj == null) {
            return "N/A";  // Handle null case
        }

        String value = valueObj.toString();
        return "None".equals(value) ? "N/A" : value;  // Handle "None" as well
    }



    private void downloadSelectedFormat(String url, String selectedFormatId) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("download_best_quality");

        pyObject.callAttr("download_selected_format", url, selectedFormatId, new ProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    progressText.setText(progress + "%");
                    progressBar.setProgress(progress);
                });
            }
        });

        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
    }

    // Callback interface for progress updates during the download
    interface ProgressCallback {
        void onProgressUpdate(int progress);
    }
}

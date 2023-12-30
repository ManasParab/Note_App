package com.example.to_donote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.textfield.TextInputEditText;

public class CreateTask extends AppCompatActivity {

    private TextInputEditText enterTitle, enterDesc;
    private Button discardButton, saveButton;
    private int position = -1;
    private List<Uri> selectedImageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        enterTitle = findViewById(R.id.enterTitle);
        enterDesc = findViewById(R.id.enterDesc);

        discardButton = findViewById(R.id.Discard);
        saveButton = findViewById(R.id.Save);

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity without saving the data
                finish();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = enterTitle.getText().toString();
                String desc = enterDesc.getText().toString();
                String dateTime = getCurrentDateTime();

                if (!title.isEmpty()) {
                    // Prepare the result intent and set it as the activity result

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("title", title);
                    resultIntent.putExtra("description", desc);
                    resultIntent.putExtra("position", position);
                    resultIntent.putExtra("dateTime", dateTime);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(CreateTask.this, "Please enter title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check if the Intent has extras (title and description)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title") && intent.hasExtra("description") && intent.hasExtra("position")) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            position = intent.getIntExtra("position", -1);

            // Set the title and description in the TextInputEditText fields
            enterTitle.setText(title);
            enterDesc.setText(description);
        }
    }

    private String getCurrentDateTime() {
        // Get the current date and time in the format "dd/MM/yyyy hh:mm:ss a"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
        return sdf.format(new Date());
    }

}
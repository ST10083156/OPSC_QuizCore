package com.example.opsc_quizcore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.opsc_quizcore.Models.SettingsModel;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgColors, rgFontSize;
    private RadioButton rbRed, rbBlue, rbGreen, rbSmall, rbMedium, rbLarge;
    private Switch switchDarkMode;
    private Button btnSaveSettings, btnBack;

    // Initialize settingsModel to prevent NullPointerException
    private SettingsModel settingsModel = new SettingsModel("defaultUserID", "Light", "Red", "Small");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e("SettingsActivity", "View with ID 'main' is null!");
        }

        // Initialize UI components
        rgColors = findViewById(R.id.rgColors);
        rbRed = findViewById(R.id.rbWhite);
        rbBlue = findViewById(R.id.rbBlue);
        rbGreen = findViewById(R.id.rbGreen);

        rgFontSize = findViewById(R.id.rgFontSize);
        rbSmall = findViewById(R.id.rbSmall);
        rbMedium = findViewById(R.id.rbMedium);
        rbLarge = findViewById(R.id.rbLarge);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnBack = findViewById(R.id.btnBack);

        // Load saved UI state on activity start
        loadUIState();

        btnSaveSettings.setOnClickListener(v -> {
            saveSettings();
            // Apply the settings immediately to reflect changes
            applySettingsToUI(settingsModel);
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Closes the current activity
        });
    }

    private void applySettingsToUI(SettingsModel settingsModel) {
        switch (settingsModel.getTheme()) {
            case "White":
                rbRed.setChecked(true);
                break;
            case "Blue":
                rbBlue.setChecked(true);
                break;
            case "Green":
                rbGreen.setChecked(true);
                break;
        }

        switch (settingsModel.getSize()) {
            case "Small":
                rbSmall.setChecked(true);
                break;
            case "Medium":
                rbMedium.setChecked(true);
                break;
            case "Large":
                rbLarge.setChecked(true);
                break;
        }
        switchDarkMode.setChecked("Dark".equals(settingsModel.getMode()));
    }

    private void saveSettings() {
        String selectedColor = rbRed.isChecked() ? "White" : (rbBlue.isChecked() ? "Blue" : "Green");
        String selectedFontSize = rbSmall.isChecked() ? "Small" : (rbMedium.isChecked() ? "Medium" : "Large");
        String selectedMode = switchDarkMode.isChecked() ? "Dark" : "Light";

        // Update the settingsModel object with the new values
        settingsModel = new SettingsModel(settingsModel.getUserID(), selectedMode, selectedColor, selectedFontSize);

        // Save the settings into SharedPreferences
        saveUIState(selectedColor, selectedFontSize, selectedMode);

        Toast.makeText(this, "Settings Saved:\nTheme: " + settingsModel.getTheme() +
                        "\nFont Size: " + settingsModel.getSize() +
                        "\nDark Mode: " + settingsModel.getMode(),
                Toast.LENGTH_LONG).show();
    }

    private void saveUIState(String theme, String fontSize, String mode) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", theme);
        editor.putString("fontSize", fontSize);
        editor.putString("mode", mode);
        editor.apply();  // Save asynchronously
    }

    private void loadUIState() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String savedTheme = sharedPreferences.getString("theme", "Red"); // Default to Red if not found
        String savedFontSize = sharedPreferences.getString("fontSize", "Small"); // Default to Small
        String savedMode = sharedPreferences.getString("mode", "Light"); // Default to Light

        // Apply UI based on saved preferences
        applyUIChange(savedTheme, savedFontSize, savedMode);
    }

    private void applyUIChange(String theme, String fontSize, String mode) {
        // Apply the theme
        if (theme.equals("White")) {
            rbRed.setChecked(true);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else if (theme.equals("Blue")) {
            rbBlue.setChecked(true);
            getWindow().getDecorView().setBackgroundColor(Color.BLUE); // Optional: Set background color for blue
        } else if (theme.equals("Green")) {
            rbGreen.setChecked(true);
            getWindow().getDecorView().setBackgroundColor(Color.GREEN); // Optional: Set background color for green
        }

        // Apply the font size
        if (fontSize.equals("Small")) {
            rbSmall.setChecked(true);
        } else if (fontSize.equals("Medium")) {
            rbMedium.setChecked(true);
        } else if (fontSize.equals("Large")) {
            rbLarge.setChecked(true);
        }

        // Apply dark mode switch
        switchDarkMode.setChecked("Dark".equals(mode));
    }
}


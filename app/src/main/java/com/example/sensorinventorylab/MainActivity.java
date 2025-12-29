package com.example.sensorinventorylab;

// Android system imports for sensor handling and UI
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

// AppCompatActivity for backward compatibility support
import androidx.appcompat.app.AppCompatActivity;

// Java utility imports
import java.util.ArrayList;
import java.util.List;

// MainActivity acts as the main screen AND listens to sensor updates
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // SensorManager: central system service to access device sensors
    private SensorManager sensorManager;

    // List to store all available sensors on the device
    private List<Sensor> sensorList;

    // Stores the sensor currently selected by the user
    private Sensor selectedSensor;

    // TextView to display live sensor readings (X, Y, Z)
    private TextView sensorDataValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the UI layout defined in activity_main.xml
        setContentView(R.layout.activity_main);

        // Get reference to the ListView that will show all sensors
        ListView sensorListView = findViewById(R.id.sensorListView);

        // Get reference to the TextView that will show live sensor values
        sensorDataValue = findViewById(R.id.sensorDataValue);

        // Step 2: Initialize Sensor Services
        // 1. Obtain the SensorManager system service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 2. Retrieve a list of ALL sensors available on the device
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Create a list of sensor names for display in the ListView
        List<String> sensorNames = new ArrayList<>();

        // Loop through each sensor and add its name + vendor to the list
        for (Sensor s : sensorList) {
            sensorNames.add(s.getName() + " (" + s.getVendor() + ")");
        }

        // Create an ArrayAdapter to bind sensorNames to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sensorNames
        );

        // Attach the adapter to the ListView so items appear on screen
        sensorListView.setAdapter(adapter);

        // Handle user clicking on a sensor from the list
        sensorListView.setOnItemClickListener((parent, view, position, id) -> {

            // Stop listening to any previously selected sensor
            sensorManager.unregisterListener(this);

            // Set the newly selected sensor based on clicked position
            selectedSensor = sensorList.get(position);

            // Inform user that the app is waiting for sensor data
            sensorDataValue.setText("Waiting for data from: " + selectedSensor.getName());

            // Register listener immediately to receive live sensor updates
            sensorManager.registerListener(
                    this,
                    selectedSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        });
    }

    // Step 3: SensorEventListener callback
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Ensure the event belongs to the currently selected sensor
        if (event.sensor.getType() == selectedSensor.getType()) {

            // StringBuilder used to efficiently build display text
            StringBuilder data = new StringBuilder();

            // Extract sensor values safely (some sensors may not have all axes)
            if (event.values.length > 0)
                data.append("X: ").append(event.values[0]).append("\n");

            if (event.values.length > 1)
                data.append("Y: ").append(event.values[1]).append("\n");

            if (event.values.length > 2)
                data.append("Z: ").append(event.values[2]);

            // Update the TextView with the latest sensor readings
            sensorDataValue.setText(data.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // This callback is not required for this lab
        // Included only because SensorEventListener interface requires it
    }

    // Step 4: Lifecycle Management
    @Override
    protected void onResume() {
        super.onResume();

        // Re-register the sensor listener when the app returns to foreground
        if (selectedSensor != null) {
            sensorManager.registerListener(
                    this,
                    selectedSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister listener to stop sensor updates when app is not visible
        // This prevents battery drain and unnecessary processing
        sensorManager.unregisterListener(this);
    }
}

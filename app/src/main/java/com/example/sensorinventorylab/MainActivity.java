package com.example.sensorinventorylab;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private Sensor selectedSensor;
    private TextView sensorDataValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView sensorListView = findViewById(R.id.sensorListView);
        sensorDataValue = findViewById(R.id.sensorDataValue);

        // Step 2: Initialize Sensor Services [cite: 777]
        // 1. Obtain Service [cite: 779]
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 2. Get List: Retrieve all sensors using TYPE_ALL [cite: 780]
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // 3. Display: Use ArrayAdapter with sensor.getName() and sensor.getVendor()
        List<String> sensorNames = new ArrayList<>();
        for (Sensor s : sensorList) {
            sensorNames.add(s.getName() + " (" + s.getVendor() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, sensorNames);
        sensorListView.setAdapter(adapter);

        // Handle list clicks to select a sensor
        sensorListView.setOnItemClickListener((parent, view, position, id) -> {
            // Unregister previous sensor if any
            sensorManager.unregisterListener(this);

            // Set new sensor
            selectedSensor = sensorList.get(position);
            sensorDataValue.setText("Waiting for data from: " + selectedSensor.getName());

            // Register immediately to start seeing data
            sensorManager.registerListener(this, selectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
    }

    // Step 3: Implement the Listener [cite: 783]
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 1. Override onSensorChanged [cite: 785]
        if (event.sensor.getType() == selectedSensor.getType()) {
            StringBuilder data = new StringBuilder();

            // 2. Extract Values [cite: 786-787]
            // Note: Not all sensors have 3 values, but for Accelerometer/Gyro/Magnetometer they do.
            if (event.values.length > 0) data.append("X: ").append(event.values[0]).append("\n");
            if (event.values.length > 1) data.append("Y: ").append(event.values[1]).append("\n");
            if (event.values.length > 2) data.append("Z: ").append(event.values[2]);

            // 3. Update UI [cite: 788]
            sensorDataValue.setText(data.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this lab logic
    }

    // Step 4: Lifecycle Management [cite: 789]
    @Override
    protected void onResume() {
        super.onResume();
        // 1. Call registerListener [cite: 791]
        if (selectedSensor != null) {
            sensorManager.registerListener(this, selectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 2. Call unregisterListener [cite: 793]
        sensorManager.unregisterListener(this);
    }
}
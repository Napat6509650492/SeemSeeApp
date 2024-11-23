package com.example.seemseeapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView seemseeView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastUpdate;
    private static final int SHAKE_THRESHOLD = 600;
    private static boolean isShowDialog = false;
    private Handler handler;
    private float speed;
    private int images[] = {R.drawable.seemsee1,R.drawable.seemsee2,R.drawable.seemsee3};
    private int currentImageIndex = 0;
    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seemseeView = findViewById(R.id.seemsee);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (speed > SHAKE_THRESHOLD) {
                    showDialog();
                }
                handler.postDelayed(this,100);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    private void showDialog() {
        if(!isShowDialog){
            isShowDialog = true;
            startImageRotationFor3Seconds();
        }

    }

    private String getPrediction(int number) {
        if (number >= 1 && number <= 20) {
            return "วันนี้คุณจะมีโชคเล็กน้อยในด้านการเงิน!";
        } else if (number >= 21 && number <= 40) {
            return "คุณอาจพบโอกาสใหม่ ๆ ที่น่าตื่นเต้น!";
        } else if (number >= 41 && number <= 60) {
            return "วันนี้คุณควรระวังการสื่อสารผิดพลาด!";
        } else if (number >= 61 && number <= 80) {
            return "สุขภาพของคุณแข็งแรง แต่อย่าลืมพักผ่อน!";
        } else if (number >= 81 && number <= 100) {
            return "คุณจะได้รับข่าวดีในเร็ว ๆ นี้!";
        } else {
            return "ไม่มีคำทำนายสำหรับเลขนี้";
        }
    }


    private void startImageRotationFor3Seconds() {
        // Runnable ที่จะทำการสลับภาพทุกๆ 1 วินาที ภายใน 3 วินาที
        Runnable imageRotationRunnable = new Runnable() {
            @Override
            public void run() {
                // เปลี่ยนภาพใน ImageView
                seemseeView.setImageDrawable(getResources().getDrawable(images[currentImageIndex]));

                // เลือกภาพถัดไป
                currentImageIndex = (currentImageIndex + 1) % images.length;
            }
        };
        int randomNumber = getRandomNumber();
        // เริ่มการทำงานของ Runnable ครั้งแรก
        handler.post(imageRotationRunnable);

        // ตั้งเวลาให้ทำการเปลี่ยนภาพทุกๆ 1 วินาที (สำหรับ 3 วินาที)
        handler.postDelayed(imageRotationRunnable, 500); // 1 วิ
        handler.postDelayed(imageRotationRunnable, 1000); // 2 วิ
        handler.postDelayed(imageRotationRunnable, 1500);
        handler.postDelayed(imageRotationRunnable, 2000);
        handler.postDelayed(imageRotationRunnable, 2500);
        handler.postDelayed(imageRotationRunnable, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int randomNumber = getRandomNumber();
                String prediction = getPrediction(randomNumber);

                new AlertDialog.Builder(ctx)
                        .setTitle("คุณได้รับเลข: " + randomNumber)
                        .setMessage("♥" + prediction + "♥")
                        .setPositiveButton("OK", (dialog, which) -> {
                            isShowDialog = false;
                            seemseeView.setImageDrawable(getResources().getDrawable(images[0]));
                        })
                        .setOnDismissListener(dialog -> {
                            isShowDialog = false;
                            seemseeView.setImageDrawable(getResources().getDrawable(images[0]));
                        })
                        .show();
            }
        },3500);
    }

    private int getRandomNumber(){
        Random random = new Random();
        return random.nextInt(100) + 1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ไม่ต้องใช้ในกรณีนี้
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}

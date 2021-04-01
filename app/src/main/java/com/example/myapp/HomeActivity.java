package com.example.myapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener, BottomSheetDialog.BottomSheetListener {

    private static final int TAKE_IMAGE_PERMISSION_CODE = 1000;
    private static final int UPLOAD_IMAGE_PERMISSION_CODE = 2000;
    public static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_UPLOAD_CODE = 1002;
    private ImageView imgProfile;
    private Uri image_uri;

    private DrawerLayout drawer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isAccelerometerAvailable;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private boolean isNotFirstTime = false;
    private float xDiff, yDiff, zDiff;
    private float shakeThreshold = 6f;
    private int counts = 0;
    private boolean shakeMenuSwitch = true;
    private SettingsFragment settingsFragment;

    public Uri getImage_uri() {
        return image_uri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //navigation drawer -> start
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //profile image -> start
        View headView = navigationView.getHeaderView(0);
        imgProfile = (ImageView) headView.findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bottom sheet -> start
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheet");
                //bottom sheet -> end
            }
        });
        //profile image -> end

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        settingsFragment = new SettingsFragment();
        //navigation drawer -> end

        //sensor -> start
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvailable = true;
        } else {
            Toast.makeText(getApplicationContext(), "Accelerometer not available...", Toast.LENGTH_SHORT).show();
            isAccelerometerAvailable = false;
        }
        //sensor -> end
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_products:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).commit();
                break;
            case R.id.nav_contact:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        shakeMenuSwitch = settingsFragment.checkSwitch();
        if (shakeMenuSwitch) {
            currentX = event.values[0];
            currentY = event.values[1];
            currentZ = event.values[2];

            if (isNotFirstTime) {
                xDiff = Math.abs(lastX - currentX);
                yDiff = Math.abs(lastY - currentY);
                zDiff = Math.abs(lastZ - currentZ);

                if ((xDiff > shakeThreshold && yDiff > shakeThreshold) ||
                        (xDiff > shakeThreshold && zDiff > shakeThreshold) ||
                        (yDiff > shakeThreshold && zDiff > shakeThreshold)) {
                    counts++;
                    if (counts % 2 == 1) {
                        if (!drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    } else {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            }

            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;
            isNotFirstTime = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAccelerometerAvailable) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAccelerometerAvailable) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onButtonClicked(String text) {
        switch (text) {
            case "Take":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //old version
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        //request permission
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, TAKE_IMAGE_PERMISSION_CODE);
                    } else {
                        //permission granted
                        openCamera();
                    }
                } else {
                    //newer version
                    openCamera();
                }
                break;
            case "Upload":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //old version
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        //request permission
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, UPLOAD_IMAGE_PERMISSION_CODE);
                    } else {
                        //permission granted
                        openGallery();
                    }
                } else {
                    //newer version
                    openGallery();
                }
                break;
        }
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMAGE_UPLOAD_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case TAKE_IMAGE_PERMISSION_CODE: {
                    //permission granted
                    openCamera();
                    break;
                }
                case UPLOAD_IMAGE_PERMISSION_CODE: {
                    //permission granted
                    openGallery();
                    break;
                }

            }
        } else {
            //permission denied
            Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            imgProfile.setImageURI(image_uri);
        } else if (resultCode == RESULT_OK && requestCode == IMAGE_UPLOAD_CODE) {
            image_uri = data.getData();
            imgProfile.setImageURI(image_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

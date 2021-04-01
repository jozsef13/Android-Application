package com.example.myapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.myapp.HomeActivity.IMAGE_CAPTURE_CODE;

public class ContactFragment extends Fragment {

    private GoogleMap mMap;
    private LatLng craiova = new LatLng(44.32722493881123, 23.78330160846058);
    private ImageButton setMarkerIconButton;
    private ImageButton addNewMarkerButton;
    private Uri image_uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.addMarker(new MarkerOptions().position(craiova).title("Marker in Craiova"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(craiova, 10));
        });

        setMarkerIconButton = view.findViewById(R.id.addMarkerIconButton);
        setMarkerIconButton.setOnClickListener(v -> openCamera());

        addNewMarkerButton = view.findViewById(R.id.addMarkerButton);
        addNewMarkerButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Select where you want to place the new  marker!", Toast.LENGTH_SHORT).show();
            mMap.setOnMapClickListener(latLng -> {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + ":" + latLng.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                mMap.addMarker(markerOptions);
                mMap.setOnMapClickListener(null);
            });
        });

        return view;
}

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private GoogleMap.InfoWindowAdapter setMarkerWindow() {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View mWindow = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
                redoInfoWindow(marker, mWindow);
                return mWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View mWindow = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
                redoInfoWindow(marker, mWindow);
                return mWindow;
            }
        };
    }

    private void redoInfoWindow(Marker marker, View mWindow) {
        TextView tv = mWindow.findViewById(R.id.markerTitle);
        tv.setText(marker.getTitle());

        ImageView iv = mWindow.findViewById(R.id.markerImage);
        iv.getLayoutParams().height = 350;
        iv.getLayoutParams().width = 350;
        if (image_uri != null) {
            iv.setImageURI(image_uri);
        } else {
            iv.setImageResource(R.drawable.ic_camera);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("We are here!");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            mMap.setInfoWindowAdapter(setMarkerWindow());
        }
    }
}

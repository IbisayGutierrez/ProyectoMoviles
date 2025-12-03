package com.example.proyectomoviles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class UbicacionActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapView map;
    private EditText txtLatitud, txtLongitud;
    private Button btnGuardarUb;
    private Marker marca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_ubicacion);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });


        map = findViewById(R.id.map);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        btnGuardarUb = findViewById(R.id.btnGuardarUb);


        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);


        double latInicial = 10.430684188597372;
        double lonInicial = -85.08498580135634;

        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra("latitud_actual") &&
                intent.hasExtra("longitud_actual")) {

            try {
                String latStr = intent.getStringExtra("latitud_actual");
                String lonStr = intent.getStringExtra("longitud_actual");

                if (latStr != null && lonStr != null &&
                        !latStr.isEmpty() && !lonStr.isEmpty()) {
                    latInicial = Double.parseDouble(latStr);
                    lonInicial = Double.parseDouble(lonStr);
                }
            } catch (NumberFormatException e) {

            }
        }

        GeoPoint puntoInicial = new GeoPoint(latInicial, lonInicial);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(puntoInicial);


        marca = new Marker(map);
        marca.setPosition(puntoInicial);
        marca.setTitle("Ubicación seleccionada");
        map.getOverlays().add(marca);

        txtLatitud.setText(String.valueOf(puntoInicial.getLatitude()));
        txtLongitud.setText(String.valueOf(puntoInicial.getLongitude()));


        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                updateMarker(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                updateMarker(p);
                return true;
            }
        };

        map.getOverlays().add(new MapEventsOverlay(mapEventsReceiver));

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


        btnGuardarUb.setOnClickListener(v -> {
            String lat = txtLatitud.getText().toString();
            String lon = txtLongitud.getText().toString();

            Intent data = new Intent();
            data.putExtra("latitud", lat);
            data.putExtra("longitud", lon);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void updateMarker(GeoPoint punto) {
        map.getOverlays().remove(marca);
        marca = new Marker(map);
        marca.setPosition(punto);
        marca.setTitle("Ubicación seleccionada");
        map.getOverlays().add(marca);
        txtLatitud.setText(String.valueOf(punto.getLatitude()));
        txtLongitud.setText(String.valueOf(punto.getLongitude()));
        map.getController().setCenter(punto);
        map.invalidate();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                return;
            }
        }
    }
}

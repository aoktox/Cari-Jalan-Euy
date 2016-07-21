package com.jsproject.carijalaneuy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public Titik[] titiks;
    ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    PolylineOptions polylineOptions = new PolylineOptions();
    private GoogleMap mMap;
    private boolean ada = false, adaAkhir = false, poly = false;
    Polyline polyline;
    Marker msrc,mdst;

    private static final String TAG = "MapsActivity";
    public static final String API_GET_TITIK = "http://192.168.43.121/petaku/frontend/web/restmap/titik";
    private static final String API_GET_JALUR = "http://192.168.43.121/petaku/frontend/web/map/route";

    Button button_set_titik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button_set_titik = (Button) findViewById(R.id.button_set_titik);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng indo = new LatLng(-7.258136, 112.752746);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(indo)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(12.5f)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //marker.showInfoWindow();
                return true;
            }
        });
        Bundle b = getIntent().getExtras();
        if (b!=null){
            titiks= (Titik[]) b.getSerializable("titik");
            for (Titik titik : titiks) {
                buatMarker(titik.lat, titik.lng,""+(titik.getId_titik()-1));
            }
        }
        //ambilTitik();
    }

    void ambilTitik(){
        final ProgressDialog progress = ProgressDialog.show(MapsActivity.this, "Mengambil titik...","Silakan tunggu sebentar", true);
        OkHttpClient client = new OkHttpClient();
        String url = API_GET_TITIK;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                titiks = new Gson().fromJson(response.body().string(), Titik[].class);
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Titik titik : titiks) {
                            buatMarker(titik.lat, titik.lng,""+(titik.getId_titik()-1));
                        }
                    }
                });
                progress.dismiss();
            }
        });
    }

    void pilihMarkerAwal(){
        if (polyline != null) {
            arrayPoints.clear();
            polyline.remove();
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                msrc = marker;
                Toast.makeText(MapsActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                //marker.showInfoWindow();
                button_set_titik.setText("Set Titik Akhir");
                return true;
            }
        });
        ada = true;
    }

    void pilihMarkerAkhir(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                mdst = marker;
                Toast.makeText(MapsActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                //marker.showInfoWindow();
                button_set_titik.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        adaAkhir = true;
    }

    void buatMarker (double lat, double lng,String t){
        LatLng indo = new LatLng(lat, lng);
        //MarkerOptions mark = new MarkerOptions().position(indo).title(t).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        MarkerOptions mark = new MarkerOptions().position(indo).title(t);
        mMap.addMarker(mark);
    }

    void gambarGaris(LatLng poin){
        arrayPoints.add(poin);
    }

    public void klikCariJalan(View view) {
        if(!ada){
            Toast.makeText(MapsActivity.this, "Silakan tentukan titik awal!", Toast.LENGTH_SHORT).show();
            return;
        }else if(!adaAkhir){
            Toast.makeText(MapsActivity.this, "Silakan tentukan titik akhir!", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder builder = HttpUrl.parse(API_GET_JALUR).newBuilder();
        builder.addQueryParameter("a", ""+msrc.getTitle());
        builder.addQueryParameter("b", ""+mdst.getTitle());

        String url = builder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Jalur[] jalurs = new Gson().fromJson(response.body().string(),Jalur[].class);
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("HASIL", "Jumlah titik : "+jalurs.length);
                        for (Jalur jalur : jalurs) {
                            LatLng tmp = new LatLng(jalur.getLat(),jalur.getLng());
                            gambarGaris(tmp);
                        }
                        polylineOptions.addAll(arrayPoints);
                        polyline = mMap.addPolyline(polylineOptions);

                        button_set_titik.setVisibility(View.VISIBLE);
                        button_set_titik.setText("Set Titik Awal");
                    }
                });
            }
        });
        adaAkhir = false;
        ada = false;
    }

    public void klikSetTitik(View view) {
        if (!ada) {
            new AlertDialog.Builder(this)
                    .setTitle("Set Titik Awal")
                    .setMessage("Anda belum menentukan titik awal, set sekarang?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pilihMarkerAwal();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else if (!adaAkhir){
            new AlertDialog.Builder(this)
                    .setTitle("Set Titik Akhir")
                    .setMessage("Anda belum menentukan titik akhir, set sekarang?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pilihMarkerAkhir();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}

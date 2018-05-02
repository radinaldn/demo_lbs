package com.inkubator.radinaldn.demolbs.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.demolbs.model.User;
import com.inkubator.radinaldn.demolbs.rest.ApiClient;
import com.inkubator.radinaldn.demolbs.rest.ApiInterface;
import com.inkubator.radinaldn.demolbs.utils.AbsRunTimePermission;
import com.inkubator.radinaldn.demolbs.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AbsRunTimePermission {

    @BindView(R.id.etmakanan)
    TextInputEditText etmakanan;

    @BindView(R.id.etporsi)
    TextInputEditText etporsi;

    @BindView(R.id.etket)
    TextInputEditText etket;

    @BindView(R.id.etlat)
    TextInputEditText etlat;

    @BindView(R.id.etlng)
    TextInputEditText etlng;

    @BindView(R.id.btkirim)
    Button btkirim;

    ApiInterface apiService;

    String makanan, porsi, ket, lat, lng;
    public final String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 10;
    TextView tvlat, tvlng, tvalt;

    // Class Location
    LocationManager lm;
    LocationListener locationListener;

    // class for get current location
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                lat = String.valueOf(loc.getLatitude());
                etlat.setText(lat);

                lng = String.valueOf(loc.getLongitude());
                etlng.setText(lng);

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";
            switch (status) {
                case LocationProvider.AVAILABLE:
                    statusString = "available";
                case LocationProvider.OUT_OF_SERVICE:
                    statusString = "out of service";
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusString = "temporarily unavailable";
            }

            Toast.makeText(getBaseContext(),
                    provider + " " + statusString,
                    Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " enabled",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        //request permission here
        requestAppPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                R.string.msg, REQUEST_PERMISSION);

//        tvlat = findViewById(R.id.tvlat);
//        tvlng = findViewById(R.id.tvlng);
//        tvalt = findViewById(R.id.tvalt);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        btkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimPesanan();
            }
        });
    }

    private void kirimPesanan() {
        makanan = etmakanan.getText().toString();
        porsi = etporsi.getText().toString();
        ket = etket.getText().toString();
        lat = etlat.getText().toString();
        lng = etlng.getText().toString();

        apiService.do_pemesanan(makanan, porsi, ket, lat, lng).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        Log.d(TAG, "onResponse: Pemesanan: "+response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Berhasil memesan makanan", Toast.LENGTH_LONG).show();
                    restartFirstActivity();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal konek ke server", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestcode) {
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestAppPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    R.string.msg, REQUEST_PERMISSION);
        }

        lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0,
                locationListener
        );
    }

    private void restartFirstActivity()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }
}

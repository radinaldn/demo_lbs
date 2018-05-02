package com.inkubator.radinaldn.demolbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inkubator.radinaldn.demolbs.R;
import com.inkubator.radinaldn.demolbs.model.User;
import com.inkubator.radinaldn.demolbs.response.ResponseLogin;
import com.inkubator.radinaldn.demolbs.rest.ApiClient;
import com.inkubator.radinaldn.demolbs.rest.ApiInterface;
import com.inkubator.radinaldn.demolbs.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 01/05/18.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etusername)
    TextInputEditText etusername;

    @BindView(R.id.etpassword)
    TextInputEditText etpassword;

    @BindView(R.id.btlogin)
    Button btlogin;

    SessionManager sessionManager;
    ApiInterface apiService;

    String username, password;
    public final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        username = etusername.getText().toString();
        password = etpassword.getText().toString();

        apiService.login(username, password).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: Dapat terhubung ke server");
                    Log.d(TAG, "onResponse: User: "+response.body().getUser().getUsername());
                    User loginUser = response.body().getUser();
                    sessionManager.createLoginSession(loginUser);
                    Log.d(TAG, "onResponse: Dapat data user");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(LoginActivity.this, "Berhasil Login", Toast.LENGTH_LONG).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Gagal konek ke server", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }
}

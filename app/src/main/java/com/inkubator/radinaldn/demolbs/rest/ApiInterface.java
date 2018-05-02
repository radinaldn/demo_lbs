package com.inkubator.radinaldn.demolbs.rest;

/**
 * Created by radinaldn on 01/05/18.
 */

import com.inkubator.radinaldn.demolbs.response.ResponseLogin;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by radinaldn on 18/04/18.
 */

public interface ApiInterface {

    // di sini adalah penghubung antara android dan server

    // untuk login
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseLogin> login(
            @Field("username") String username,
            @Field("password") String password
    );

    //untuk signup
    @FormUrlEncoded
    @POST("signup.php")
    Call<ResponseBody> signup(
            @Field("username") String username,
            @Field("password") String password,
            @Field("nama_lengkap") String namaLengkap,
            @Field("email") String email,
            @Field("no_hp") String nomorHP
    );

    //untuk pemesanan
    @FormUrlEncoded
    @POST("do_pemesanan.php")
    Call<ResponseBody> do_pemesanan(
            @Field("makanan") String makanan,
            @Field("porsi") String porsi,
            @Field("ket") String ket,
            @Field("lat") String lat,
            @Field("lng") String lng
    );

}

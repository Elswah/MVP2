package com.example.mvp2.ui.main.network;

import com.example.mvp2.ui.main.model.PojoResponse;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestApiService {
    @Multipart
    @POST("upload.php")
    Single<PojoResponse> onFileUpload(@Part MultipartBody.Part file);

    @Multipart
    @POST("upload.php")
    Call<PojoResponse> onFileUpload2(@Part MultipartBody.Part file);
}

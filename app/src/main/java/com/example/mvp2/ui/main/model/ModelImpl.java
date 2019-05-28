package com.example.mvp2.ui.main.model;

import android.util.Log;

import com.example.mvp2.ui.main.network.RestApiService;
import com.example.mvp2.ui.main.network.RetrofitInstance;
import com.example.mvp2.ui.main.utils.ProgressRequestBody;
import com.example.mvp2.ui.main.views.upload.UploadInterface;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelImpl implements UploadInterface.Interactor, ProgressRequestBody.UploadCallbacks {
    //another way we can use retrofit call here to upload file and
    //return result in OnFinishedListener interface inside model interface
    //we use here service to upload to run in background service
    // this way we can cancel request and retry
    //but using intent service in service difficult to stop because it designed to
    //run long task and stop it self with caller.


    private OnProgressListener listener;

    public ModelImpl(OnProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void uploadImage(String status, String filePath, OnFinishedListener onFinishedListener) {
        // call servce to start upload throw service

        /*Intent mIntent = new Intent(context, FileUploadService.class);
        mIntent.putExtra("mFilePath", filePath);
        FileUploadService.enqueueWork(context, mIntent);*/

        // starting http service upload

        if (!filePath.isEmpty()) {

            File file = new File(filePath.trim());
            ProgressRequestBody fileBody = new ProgressRequestBody(file, "image", this);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("fileUpload", file.getName(), fileBody);

            RestApiService apiService = RetrofitInstance.getApiService();

            Call<PojoResponse> callUpload = apiService.onFileUpload2(filePart);
            if (status.equals("upload")) {
                callUpload.enqueue(new Callback<PojoResponse>() {

                    @Override
                    public void onResponse(Call<PojoResponse> call, Response<PojoResponse> response) {
                        Log.d("ResponseData", "" + response.body().getUrl());
                        onFinishedListener.onFinished(response.body());

                    }

                    @Override
                    public void onFailure(Call<PojoResponse> call, Throwable t) {
                        if (call != null && !call.isCanceled()) {
                            // Call is not cancelled, Handle network failure

                            onFinishedListener.onFailure(call, t);
                        } else if (call != null && call.isCanceled()) {

                            // Call is CANCELLED. IGNORE THIS SINCE IT WAS CANCELLED.
                            onFinishedListener.onFailure(call, t);
                        }

                        //onFinishedListener.onFailure(call, t);


                    }
                });
            } else {
                if (callUpload != null && callUpload.isExecuted()) {
                }
                callUpload.cancel();
            }
        }


    }


    @Override
    public void onProgressUpdate(int percentage) {
        Log.d("percent", "" + percentage);
        listener.onProgressChange(percentage);

    }

    @Override
    public void onError() {

    }


    @Override
    public void onFinish() {
        Log.d("percent", "" + "finishedddddd");
        listener.onProgressFinished();


    }


}

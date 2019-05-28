package com.example.mvp2.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.example.mvp2.ui.main.model.PojoResponse;
import com.example.mvp2.ui.main.network.RestApiService;
import com.example.mvp2.ui.main.network.RetrofitInstance;
import com.example.mvp2.ui.main.utils.CountingRequestBody;
import com.example.mvp2.ui.main.utils.MIMEType;
import com.example.mvp2.ui.main.utils.ProgressRequestBody;

import java.io.File;

import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploadService extends JobIntentService implements ProgressRequestBody.UploadCallbacks {
    private static final String TAG = "FileUploadService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 102;
    Disposable mDisposable;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, FileUploadService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Download/Upload of file
         * The system or framework is already holding a wake lock for us at this point
         */

        // get file file here
        String mFilePath = intent.getStringExtra("mFilePath");
        if (mFilePath == null) {
            Log.e(TAG, "onHandleWork: Invalid file URI");
            return;
        }
   /*     RestApiService apiService = RetrofitInstance.getApiService();
        Flowable<Double> fileObservable = Flowable.create(emitter -> {
            apiService.onFileUpload( createMultipartBody(mFilePath, emitter)).blockingGet();
            emitter.onComplete();
        }, BackpressureStrategy.LATEST);

        mDisposable = fileObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> onProgress(progress), throwable -> onErrors(throwable), () -> onSuccess());*/

//---------new code
        File file = new File(mFilePath.trim());
        ProgressRequestBody fileBody = new ProgressRequestBody(file, "image", this);
        MultipartBody.Part filePart =

                MultipartBody.Part.createFormData("fileUpload", file.getName(), fileBody);

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<PojoResponse> pojoResponseCall = apiService.onFileUpload2(filePart);
        pojoResponseCall.enqueue(new Callback<PojoResponse>() {

            @Override
            public void onResponse(Call<PojoResponse> call, Response<PojoResponse> response) {
                Log.d("ResponseData", "" + response.body().getUrl());

            }

            @Override
            public void onFailure(Call<PojoResponse> call, Throwable t) {

            }
        });


    }


    private void onErrors(Throwable throwable) {
        //  sendBroadcastMeaasge("Error in file upload " + throwable.getMessage());
        Log.e(TAG, "onErrors: ", throwable);
    }

    private void onProgress(Double progress) {
        //   sendBroadcastMeaasge("Uploading in progress... " + (int) (100 * progress));
        Log.i(TAG, "onProgress: " + progress);
    }

    private void onSuccess() {
        //  sendBroadcastMeaasge("File uploading successful ");
        Log.i(TAG, "onSuccess: File Uploaded");
    }


    private RequestBody createRequestBodyFromFile(File file, String mimeType) {
        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    private RequestBody createRequestBodyFromText(String mText) {
        return RequestBody.create(MediaType.parse("text/plain"), mText);
    }


    /**
     * return multi part body in format of FlowableEmitter
     *
     * @param filePath
     * @param emitter
     * @return
     */
    private MultipartBody.Part createMultipartBody(String filePath, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return MultipartBody.Part.createFormData("fileUpload", file.getName(), createCountingRequestBody(file, MIMEType.IMAGE.value, emitter));
    }

    private RequestBody createCountingRequestBody(File file, String mimeType, FlowableEmitter<Double> emitter) {
        RequestBody requestBody = createRequestBodyFromFile(file, mimeType);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            double progress = (1.0 * bytesWritten) / contentLength;
            emitter.onNext(progress);
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        Log.d("percentage", "" + percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}

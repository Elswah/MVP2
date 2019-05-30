package com.example.mvp2.ui.main.views.upload;

import com.example.mvp2.ui.main.model.PojoResponse;

import retrofit2.Call;

public interface UploadInterface {


    interface View {
        void setStatus(String status);

        void selectFileFirst();

        void getResponse(PojoResponse response);

        void errorUploading(Call<PojoResponse> call, Throwable t);

        void setProgressPercent(int percent);

        void setProgressFinished();

        void showFullImageInFragment();
    }

    interface Presenter {
        void uploadBtnClicked(String status, String filePath);


        void imageClicked();
    }

    interface Interactor {

        void uploadImage(String status, String filePath, OnFinishedListener onFinishedListener);


        interface OnFinishedListener {
            void onFinished(PojoResponse obj);

            void onFailure(Call<PojoResponse> call, Throwable t);

            void onCancel();

        }

        interface OnProgressListener {
            void onProgressChange(int percent);

            void onProgressFinished();


        }

    }

}

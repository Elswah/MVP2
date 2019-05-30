package com.example.mvp2.ui.main.views.upload;

import android.util.Log;

import com.example.mvp2.ui.main.model.ModelImpl;
import com.example.mvp2.ui.main.model.PojoResponse;

import retrofit2.Call;

public class UploadActivityPresenter implements UploadInterface.Presenter, UploadInterface.Interactor.OnFinishedListener, UploadInterface.Interactor.OnProgressListener {
    private UploadInterface.View view;
    private UploadInterface.Interactor model;


    public UploadActivityPresenter(UploadInterface.View view) {
        this.view = view;

    }


    @Override
    public void uploadBtnClicked(String status, String filePath) {
        // this interface call method upload without know about logic about it
        model = new ModelImpl(this);
        if (view != null) {
            if (filePath.length() > 0) {
                Log.d("filepath", "" + filePath.trim());
                view.setStatus(status);
                model.uploadImage(status, filePath, this);
                Log.d("ss", "ssssss");
            } else {
                view.selectFileFirst();
            }


        }

    }

    @Override
    public void imageClicked() {
        if (view != null) {
            view.showFullImageInFragment();
        }
    }


    @Override
    public void onFinished(PojoResponse obj) {
        if (view != null) {
            view.getResponse(obj);
            view.setStatus("Done");
        }

    }

    @Override
    public void onFailure(Call<PojoResponse> call, Throwable t) {
        if (view != null) {
            view.errorUploading(call, t);
        }
    }

    @Override
    public void onCancel() {
        if (view != null) {

        }
    }


    @Override
    public void onProgressChange(int percent) {
        Log.d("aaaaa", "" + percent);
        if (view != null) {
            view.setProgressPercent(percent);
        }
    }

    @Override
    public void onProgressFinished() {
        if (view != null) {
            view.setProgressFinished();
        }
    }


}

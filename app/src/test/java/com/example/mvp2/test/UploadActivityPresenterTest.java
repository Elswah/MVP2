package com.example.mvp2.test;

import com.example.mvp2.ui.main.model.PojoResponse;
import com.example.mvp2.ui.main.views.upload.UploadActivityPresenter;
import com.example.mvp2.ui.main.views.upload.UploadInterface;

import org.junit.Assert;
import org.junit.Test;

import retrofit2.Call;

public class UploadActivityPresenterTest {

    @Test
    public void uploadBtnClicked() {
        //  Assert.assertEquals(1,2);
        //3 parts to test
        //given (initial condition)
        ActivityView activityView = new ActivityView();
        ActivityInteractor activityInteractor = new ActivityInteractor();

        //when (action to trigger)
        UploadActivityPresenter uploadActivityPresenter = new UploadActivityPresenter(activityView);
        uploadActivityPresenter.uploadBtnClicked("upload", "nax");

        //then (did it work or not)

        Assert.assertEquals(true, ((ActivityView) activityView).showMessage);
    }

    //________________________________________________ MANUAL MOCKING
    //manually creating the interface implementation
    //later use the Mocking library
    class ActivityView implements UploadInterface.View {
        boolean showMessage;


        @Override
        public void setStatus(String status) {

        }

        @Override
        public void selectFileFirst() {
            showMessage = true;
        }

        @Override
        public void getResponse(PojoResponse response) {

        }

        @Override
        public void errorUploading(Call<PojoResponse> call, Throwable t) {

        }

        @Override
        public void setProgressPercent(int percent) {

        }

        @Override
        public void setProgressFinished() {

        }

        @Override
        public void showFullImageInFragment() {

        }
    }

    class ActivityInteractor implements UploadInterface.Interactor {

        @Override
        public void uploadImage(String status, String filePath, OnFinishedListener onFinishedListener) {

        }
    }
}
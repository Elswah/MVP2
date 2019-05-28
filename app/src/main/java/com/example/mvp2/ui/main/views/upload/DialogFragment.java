package com.example.mvp2.ui.main.views.upload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mvp2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DialogFragment extends android.support.v4.app.DialogFragment {
    public static final String key = "bundle";
    Unbinder unbinder;
    @BindView(R.id.imageView)
    ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
// tested version image must be get from URl afterimage Uploaded
        View view = inflater.inflate(R.layout.image_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String path = (String) arguments.get(key);
            Glide.with(getActivity()).load(path).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.ic_launcher_background)).into(img);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unbind the view to free some memory
        unbinder.unbind();
    }
}

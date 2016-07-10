/*
 * Copyright (C) 2016 Oleg Kan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplaapliko.example.oauth2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.simplaapliko.example.oauth2.network.RestApiClient;
import com.simplaapliko.example.oauth2.network.response.User;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserFragment extends Fragment {

    private Subscription mSubscription;
    private TextView mUser;
    private ProgressDialog mProgressDialog;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    public UserFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initializeView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();
    }

    private void initializeView(View view) {
        mUser = (TextView) view.findViewById(R.id.user);
    }

    private void initialize() {
        showProgress(getString(R.string.loading));

        loadDataFromApi();
    }

    private void loadDataFromApi() {
        mSubscription = RestApiClient.githubApiService()
                .getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(RestApiClient.RESULT_SUCCESS)
                .map(userResult -> userResult.response().body())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        dismissProgress();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgress();
                        showMessage(error.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        mUser.setText(user.toString());
                    }
                });
    }

    private void showProgress(String message) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(android.R.string.cancel),
                (dialogInterface, i) -> {
                    mSubscription.unsubscribe();
                    dismissProgress();
                });
        mProgressDialog.show();
    }

    private void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}

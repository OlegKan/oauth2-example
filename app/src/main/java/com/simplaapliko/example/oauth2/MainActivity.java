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
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.simplaapliko.example.oauth2.network.RestApiClient;
import com.simplaapliko.example.oauth2.network.request.AuthorizationRequest;
import com.simplaapliko.example.oauth2.network.response.Authorization;
import com.simplaapliko.example.oauth2.storage.AppPreferences;
import com.simplaapliko.example.oauth2.storage.DiskStorage;
import com.simplaapliko.example.oauth2.util.EncryptionUtils;

import java.util.Collections;
import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        AuthFragment.OnAuthorizationListener {

    private ProgressDialog mProgressDialog;
    private Subscription mSubscription;
    Fragment mUserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            showAuthFragment();
            if (!TextUtils.isEmpty(AppPreferences.getToken())) {
                showUserFragment();
            }
        }
    }

    @Override
    public void logIn(String username, String password) {
        showProgress(getString(R.string.loading));
        createAuthorization(username, password);
    }

    @Override
    public void logOut(String username, String password) {
        showProgress(getString(R.string.loading));
        deleteAuthorization(username, password);
        detachFragment(mUserFragment);
    }

    private void showAuthFragment() {
        Fragment authFragment = AuthFragment.newInstance();
        replaceFragment(R.id.container, authFragment);
    }

    private void showUserFragment() {
        mUserFragment = UserFragment.newInstance();
        replaceFragment(R.id.user, mUserFragment);
    }

    private void replaceFragment(int containerViewId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerViewId, fragment)
                .commit();
    }

    private void detachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
    }

    private void createAuthorization(String username, String password) {
        String basicAuthorization = EncryptionUtils.generateBasicAuthorization(username, password);

        mSubscription = RestApiClient.githubApiService()
                .createAuthorization(basicAuthorization, getAuthorizationRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(authorizationResult -> authorizationResult.response().body())
                .subscribe(new SingleSubscriber<Authorization>() {
                    @Override
                    public void onSuccess(Authorization authorization) {
                        String token = authorization.getToken();
                        AppPreferences.setToken(token);
                        dismissProgress();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgress();
                        showMessage(error.getMessage());
                    }
                });
    }

    private void deleteAuthorization(String username, String password) {
        String basicAuthorization = EncryptionUtils.generateBasicAuthorization(username, password);
        int authorizationId = AppPreferences.getAuthorizationId();

        RestApiClient.githubApiService()
                .deleteAuthorization(basicAuthorization, authorizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Result<Object>>() {
                    @Override
                    public void onSuccess(Result<Object> value) {
                        AppPreferences.setAuthorizationId(-1);
                        AppPreferences.setToken("");
                        dismissProgress();
                        DiskStorage.deleteUser();
                    }

                    @Override
                    public void onError(Throwable error) {
                        showMessage(error.getMessage());
                    }
                });
    }

    private void showProgress(String message) {
        mProgressDialog = new ProgressDialog(this);
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private AuthorizationRequest getAuthorizationRequest() {
        List<String> scopes = Collections.singletonList("user:email");
        String note = "oauth2_example";
        AuthorizationRequest request = new AuthorizationRequest();
        request.setScopes(scopes);
        request.setNote(note);
        return request;
    }
}

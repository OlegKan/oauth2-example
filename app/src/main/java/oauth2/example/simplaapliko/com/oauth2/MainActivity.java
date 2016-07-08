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

package oauth2.example.simplaapliko.com.oauth2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;

import oauth2.example.simplaapliko.com.oauth2.network.RestApiClient;
import oauth2.example.simplaapliko.com.oauth2.network.request.AuthorizationRequest;
import oauth2.example.simplaapliko.com.oauth2.network.response.Authorization;
import oauth2.example.simplaapliko.com.oauth2.storage.AppPreferences;
import oauth2.example.simplaapliko.com.oauth2.util.EncryptionUtils;
import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        AuthFragment.OnLoginListener {

    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mRootLayout;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            showAuthFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void logIn(String username, String password) {
        showProgress(getString(R.string.loading));

        subscribe(username, password);
    }

    private void showAuthFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, AuthFragment.newInstance())
                .commit();
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
        mProgressDialog.dismiss();
    }

    private void subscribe(String username, String password) {

        String basicAuthorization = EncryptionUtils.generateBasicAuthorization(username, password);

        mSubscription = RestApiClient.githubApiService()
                .createAuthorization(basicAuthorization, getAuthorizationRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map((Func1<Result<Authorization>, Authorization>) authorizationResult -> null)
                .subscribe(new Subscriber<Authorization>() {
                    @Override
                    public void onCompleted() {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(Authorization authorization) {
                        String token = authorization.getToken();
                        AppPreferences.setToken(token);
                    }
                });
    }

    private void showMessage(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();
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

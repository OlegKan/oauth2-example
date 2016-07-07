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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AuthFragment extends Fragment {

    public interface OnLoginListener {
        void logIn(String username, String password);
    }

    private OnLoginListener mOnLoginListener;
    private TextView mUsername;
    private TextView mPassword;
    private Button mLogIn;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    public AuthFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnLoginListener = (OnLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLoginActionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        mUsername = (TextView) view.findViewById(R.id.username);
        mPassword = (TextView) view.findViewById(R.id.password);
        mLogIn = (Button) view.findViewById(R.id.log_in);

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAuthorization();
            }
        });
    }

    private void startAuthorization() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        mOnLoginListener.logIn(username, password);
    }
}

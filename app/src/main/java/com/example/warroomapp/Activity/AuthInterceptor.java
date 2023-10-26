package com.example.warroomapp.Activity;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private String authToken;

    public AuthInterceptor(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Create a new request with the "Authorization" header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Token " + authToken)
                .build();

        return chain.proceed(newRequest);
    }
}

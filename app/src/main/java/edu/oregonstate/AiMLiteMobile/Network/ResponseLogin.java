package edu.oregonstate.AiMLiteMobile.Network;

import retrofit.client.Response;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ResponseLogin {

    private String token;

    public String getToken() {
        return token;
    }

    public ResponseLogin(String token) {
        this.token = token;
    }
}

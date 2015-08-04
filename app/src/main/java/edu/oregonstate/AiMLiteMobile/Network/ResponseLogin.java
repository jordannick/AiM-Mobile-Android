package edu.oregonstate.AiMLiteMobile.Network;

import org.json.JSONObject;

import edu.oregonstate.AiMLiteMobile.Helpers.Utils;
import retrofit.mime.TypedInput;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ResponseLogin {
    private static final String TAG = "AiM_ResponseLogin";

    private String token;

    public String getToken() {
        return token;
    }

    public ResponseLogin(String token) {
        this.token = token;
    }

    public static Object parse(TypedInput body){

        ////Log.d(TAG, "CustomConverter TYPE MATCH: " + type.toString());
        String token = "";
        try {
            token =  (new JSONObject(Utils.fromStream(body.in()))).getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new ResponseLogin(token));
    }
}

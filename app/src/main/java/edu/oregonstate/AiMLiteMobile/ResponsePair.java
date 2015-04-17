package edu.oregonstate.AiMLiteMobile;


import org.json.JSONArray;

/**
 * Created by jordan_n on 8/19/2014.
 *
 * ResponsePair is used for returning JSON (or string) data from API calls,
 * paired with a custom status code to handle in different ways
 */
public class ResponsePair {

    public enum Status{
        NONE, NET_FAIL, AUTH_FAIL, SUCCESS, JSON_FAIL, NO_DATA;
    }

    public JSONArray jarray;

    public String returnedString;

    public Status status;

    public ResponsePair(final Status status, final JSONArray jArray) {
        this.status = status;
        this.jarray = jArray;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public Status getStatus(){
        return status;
    }

    public JSONArray getJarray() {
        return jarray;
    }

    public void setJarray(JSONArray jarray) {
        this.jarray = jarray;
    }

    public String getReturnedString() {
        return returnedString;
    }

    public void setReturnedString(String returnedString) {
        this.returnedString = returnedString;
    }
}

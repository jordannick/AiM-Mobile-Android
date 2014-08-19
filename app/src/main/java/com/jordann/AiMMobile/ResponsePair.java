package com.jordann.AiMMobile;

import org.json.JSONArray;

/**
 * Created by jordan_n on 8/19/2014.
 */
public class ResponsePair {

        //TODO: enumerate different statuses

        public String status;
        public JSONArray jarray;

        public ResponsePair(final String status, final JSONArray jarray) {
            this.status = status;
            this.jarray = jarray;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public JSONArray getJarray() {
            return jarray;
        }

        public void setJarray(JSONArray jarray) {
            this.jarray = jarray;
        }


}

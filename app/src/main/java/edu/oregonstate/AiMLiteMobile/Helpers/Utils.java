package edu.oregonstate.AiMLiteMobile.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jordan_n on 8/4/2015.
 */
public class Utils {

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        //String newLine = System.getProperty("line.separator");
        String line;
        while((line = reader.readLine()) != null){
            out.append(line);

        }
        return out.toString();
    }
}

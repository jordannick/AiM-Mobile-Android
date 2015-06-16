package edu.oregonstate.AiMLiteMobile.Network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by sellersk on 6/9/2015.
 */
public class CustomConverter implements Converter {
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String result = null;
        try {
            String text = fromStream(body.in());
            JSONObject obj = new JSONObject(text);
            result = obj.getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        //String newLine = System.getProperty("line.separator");
        String line;
        while((line = reader.readLine()) != null){
            out.append(line);

        }
        return out.toString();
    }


    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }
}
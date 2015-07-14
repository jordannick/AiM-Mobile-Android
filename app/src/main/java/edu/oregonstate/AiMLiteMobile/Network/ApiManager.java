package edu.oregonstate.AiMLiteMobile.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;

import edu.oregonstate.AiMLiteMobile.Network.ResponseLogin;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by sellersk on 6/10/2015.
 */
public class ApiManager {
    private static final String TAG = "AiM_ApiManager";

    private static final String API_URL = "http://api-test.facilities.oregonstate.edu/1.0";

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(API_URL)
            .setConverter(new CustomConverter())
            .setLogLevel(RestAdapter.LogLevel.BASIC)
            .build();

    private static final AimService AIM_SERVICE = REST_ADAPTER.create(AimService.class);

    public static AimService getService(){
        return AIM_SERVICE;
    }


    public interface AimService {
        // LOGIN
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/User/login/{username}")
        void loginUser(@Path("username") String username, @Field("password") String password, Callback<ResponseLogin> callback);

        // GET WORK ORDERS
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/getAll/{username}")  //DEBUG. ADDED B TO BREAK IT todo: undo
        void getWorkOrders(@Path("username") String username, @Field("token") String token, Callback<ResponseWorkOrders> callback);

        // GET NOTICES
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/getNotices/{username}")
        void getNotices(@Path("username") String username, @Field("token") String token, Callback<ResponseNotices> callback);

        // GET LAST UPDATED
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/getLastUpdated/{username}")
        void getLastUpdated(@Path("username") String username, @Field("token") String token, Callback<ResponseLastUpdated> callback);



        // ADD TIME
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/addTime")
        void addTime(@Field("username") String username, @Field("hours") String hours, @Field("workOrderPhaseId") String workOrderPhaseId, @Field("timeType") String timeType, @Field("timeStamp") String timeStamp, @Field("token") String token, Callback<String> callback);

        // ADD ACTION TAKEN
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/addActionTaken")
        void addActionTaken(@Field("username") String username, @Field("workOrderPhaseId") String workOrderPhaseId, @Field("actionTaken") String actionTaken, @Field("timeStamp") String timeStamp, @Field("token") String token, Callback<String> callback);

        // ADD NOTE
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/addNote")
        void addNote(@Field("username") String username, @Field("workOrderPhaseId") String workOrderPhaseId, @Field("note") String note, @Field("timeStamp") String timeStamp, @Field("token") String token, Callback<String> callback);

        // UPDATE STATUS
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/updateStatus")
        void updateStatus(@Field("username") String username, @Field("workOrderPhaseId") String workOrderPhaseId, @Field("newStatus") String newStatus, @Field("timeStamp") String timeStamp, @Field("token") String token, Callback<String> callback);

        // UPDATE SECTION
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST("/WorkOrder/updateSection")
        void updateSection(@Field("username") String username, @Field("workOrderPhaseId") String workOrderPhaseId, @Field("value") String value, @Field("timeStamp") String timeStamp, @Field("token") String token, Callback<String> callback);
    }


    private static class CustomConverter implements Converter{
        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            if (type == ResponseLogin.class){
                ////Log.d(TAG, "CustomConverter TYPE MATCH: " + type.toString());
                String token = "";
                try {
                    token =  (new JSONObject(fromStream(body.in()))).getString("token");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return (new ResponseLogin(token));
            } else if(type == ResponseWorkOrders.class){
                ////Log.d(TAG, "CustomConverter TYPE MATCH: " + type.toString());
                ArrayList<WorkOrder> workOrders = new ArrayList<>();
                JSONArray array = new JSONArray();
                try {
                    array = new JSONArray(fromStream(body.in()));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        WorkOrder wo = new WorkOrder();
                        wo.setBeginDate(obj.getString("beg_dt"));
                        wo.setEndDate(obj.getString("end_dt"));
                        wo.setCraftCode(obj.getString("craft_code"));
                        wo.setShop(obj.getString("shop"));
                        wo.setBuilding(obj.getString("building"));
                        wo.setLocationCode(obj.getString("location_code"));
                        wo.setDescription(obj.getString("description"));
                        wo.setCategory(obj.getString("category"));
                        wo.setPriority(obj.getString("pri_code"));
                        wo.setDateElements(obj.getString("ent_date"));
                        wo.setStatus(obj.getString("status_code"));
                        wo.setContactName(obj.getString("contact"));
                        wo.setDepartment(obj.getString("department"));
                        wo.setProposalPhase(String.format("%s-%s", obj.getString("proposal"), obj.getString("sort_code")));

                        if (obj.getString("section").equals("Daily Assignments")){
                            wo.setSection("Daily");
                        } else {
                            wo.setSection(obj.getString("section"));
                        }

                        JSONArray objNotes = obj.getJSONArray("notes");

                        ArrayList<Note> notes = new ArrayList<Note>();

                        if (objNotes.length() > 0){
                            //Log.d(TAG, "notes = " + objNotes);
                            Log.d(TAG, "WO with notes: "+ wo.getDescription());
                            for (int j = 0; j < objNotes.length(); j++) {
                                JSONObject objNote = objNotes.getJSONObject(j);
                                Log.d(TAG, "note: " + objNote.getString("notes"));

                                String rawDate = objNote.getString("edit_date");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US); //Expecting this format
                                Date parsedDate = format.parse(rawDate);

                                Note note = new Note(objNote.getString("notes"), objNote.getString("name"), parsedDate);
                                notes.add(note);
                            }

                            // Sort notes array in chronological order
                            Collections.sort(notes, new Comparator<Note>() {
                                @Override
                                public int compare(Note lhs, Note rhs) {
                                    if (lhs.getDate().before(rhs.getDate())){
                                        return 1;
                                    } else if (lhs.getDate().after(rhs.getDate())) {
                                        return -1;
                                    } else {
                                        return 0;
                                    }

                                }
                            });



                            wo.setNotes(notes);
                        }

                        JSONArray objTimeTypes = obj.getJSONArray("time_types");

                        ArrayList<String> timeTypes = new ArrayList<String>();

                        if (objTimeTypes.length() > 0){
                            for (int j = 0; j < objTimeTypes.length(); j++){
                                JSONObject objTimeType = objTimeTypes.getJSONObject(j);
                                String timeType = objTimeType.getString("time_type") + " - " + objTimeType.getString("description");
                                timeTypes.add(timeType);
                            }

                            // Sort time types array in alphabetical order
                            Collections.sort(timeTypes, String.CASE_INSENSITIVE_ORDER);

                            wo.setTimeTypes(timeTypes);
                        }




                        workOrders.add(wo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return (new ResponseWorkOrders(workOrders, array.toString()));
            } else if (type == ResponseNotices.class){
                ArrayList<Notice> notices = new ArrayList<>();
               // JSONArray array = new JSONArray();
                JSONObject obj = new JSONObject();
                try {
                    //array = new JSONArray(fromStream(body.in()));
                    obj = new JSONObject(fromStream(body.in()));
                    //TODO find out actual notice response

                    JSONObject objNewlyAssigned = (JSONObject)obj.get("newly_assigned");
                    ////Log.d(TAG, "obj newly: " + objNewlyAssigned.toString());


                    for (int i = 0; i < objNewlyAssigned.names().length();i++){
                        //Log.d(TAG, "obj test: " + objNewlyAssigned.names().getString(i));
                        String name = objNewlyAssigned.names().getString(i);
                        JSONObject objNotice = (JSONObject) objNewlyAssigned.get(name);
                        //Log.d(TAG, "obj test2: " + objNotice);
                        Notice notice = new Notice();
                        notice.setDescription(objNotice.getString("description"));
                        notice.setEditClerk(objNotice.getString("edit_clerk"));
                        //notice.setModified(new Date(objNotice.getString("modified")));
                        notice.setDateElements(objNotice.getString("modified"));
                        notice.setId(objNotice.getString("id"));
                        notice.setType(objNotice.getString("type"));
                        notices.add(notice);
                    }


                    //for (int i = 0; i < array.length(); i++) {
                    /*//Log.d(TAG, "notice obj: "+ obj.toString());
                    for (int i = 0; i < 2; i++) {
                        Notice notice = new Notice();
                        notice.setText(obj.getString("description"));
                        //notice.setDate(new Date(System.currentTimeMillis()));

                        notices.add(notice);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return (new ResponseNotices(notices, obj.toString()));
            }else if(type == ResponseLastUpdated.class){
                String dateString = null;
                Date date = null;
                try {
                    dateString = fromStream(body.in()).replaceAll("\"", "");

                    if(dateString.toLowerCase().equals("null")){
                        Log.d(TAG, "RequestLastUpdated: FOUND NULL");
                        return new ResponseLastUpdated("null", null);
                    }

                    //Convert date to ms
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

                    date = format.parse(dateString);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing");
                    e.printStackTrace();
                }
                return (new ResponseLastUpdated(dateString, date));
            }

            return null;
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
}

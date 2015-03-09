package it.urban.urbanapp.urbanapp2.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import it.urban.urbanapp.urbanapp2.EventoActivity;

/**
 * Created by alexander on 02/03/15.
 */



public class DataProvider {

    public static void getEvent(String day,EventoActivity eventoActivity){
        loadJson(day,eventoActivity);
        if(!eventoActivity.scaricato)
            downloadEvent(day, eventoActivity);
    }

    private static void downloadEvent(final String day,final EventoActivity eventoActivity){
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {

                final String BASE_URL = "http://uapp.sfcoding.com/";
                String checkUrl = BASE_URL + "getmodificationdate/?day=" +day;
                String downloadUrl = BASE_URL + "getnextevent/?day=" + day;

                String compareData = ConnectionHelper.getDataFromUrl(checkUrl);
                JSONObject savedJsonData = loadJsonFromFile(day,eventoActivity);

                Boolean isUpToDate = checkIsUpdated(compareData, savedJsonData);

                if(!isUpToDate){
                    String data = ConnectionHelper.getDataFromUrl(downloadUrl);
                    Log.d("DATA_______",data);
                    try {
                        if (data.equals("no connection")) {
                            return null;
                        }
                        if(data.equals("[]"))
                            return new JSONObject("{}");

                        JSONObject jObj = new JSONArray(data).getJSONObject(0);
                        DataProvider.saveJson(jObj, day, eventoActivity);
                        return jObj;

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    return null;
                }else{ eventoActivity.scaricato = true; cancel(true); }

                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject==null){
                    Toast.makeText(eventoActivity.getApplicationContext(), "No Connection", Toast.LENGTH_LONG).show();
                }
                else if (jsonObject.toString().equals("{}"))
                    Toast.makeText(eventoActivity.getApplicationContext(), "Event not yet Uploaded", Toast.LENGTH_LONG).show();

                else{
                    Toast.makeText(eventoActivity.getApplicationContext(), "Event Updated Successfully", Toast.LENGTH_LONG).show();
                    eventoActivity.scaricato = true;
                    //loadJson(day,eventoActivity);
                    eventoActivity.updateView(jsonObject);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }

    public static void loadJson(final String name, final EventoActivity eventoActivity) {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                return loadJsonFromFile(name, eventoActivity);
            }
            @Override
            protected void onPostExecute(JSONObject jsonObject){
                if(jsonObject==null){
                    Log.e("EVENTO VUOTO","================================");

                }
                eventoActivity.updateView(jsonObject);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }

    private static JSONObject loadJsonFromFile(String fileName, Context context) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            try {
                return new JSONObject(sb.toString());
            }catch (JSONException e){
                return null;
            }

        } catch (IOException e) {
            Log.e("loadJsonFromFile ", fileName + " " + e.toString());
            return null;
        }
    }

    public static void saveJson(final JSONObject jsonObject, final String name, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                saveJsonToFile(jsonObject, name, context);
                return null;
            }
        }.execute(null, null, null);
    }

    private static synchronized void saveJsonToFile(JSONObject jsonObject, String fileName, Context context) {
        try {
            String jsonString = jsonObject.toString();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("DataProvide", "IOException saveJsonToFile: " + fileName + " " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException saveJsonToFile: " + fileName + " " + e);
        }
    }

    private static boolean checkIsUpdated(String compareData, JSONObject savedJsonData){
        try{
            if(savedJsonData == null)
                return false;
            JSONObject compareDataJson = new JSONObject(compareData);
            if (compareDataJson.getInt("pk") != savedJsonData.getInt("pk") ||
                compareDataJson.getInt("ueID") != savedJsonData.getJSONObject("fields").getInt("ueID")){
                return false;
            }
            return true;

        }catch (JSONException e){
            return false;
        }

    }

    public static Boolean checkConection(EventoActivity eventoActivity) {
        Context context = eventoActivity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Toast.makeText(context, "Connettiti ad internet e riprova", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }
}

/*

class MakeItAsync extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {
        final String BASE_URL = "http://uapp.sfcoding.com/";
        String url = BASE_URL+"getnextevent/?day="+args[0];
        String data = ConnectionHelper.getDataFromUrl(url);
        Log.d("DATA_______",data);
        return data;
    }

    @Override
    protected void onPostExecute(String data){
            DataProvider dp = new DataProvider();
            dp.saveEvent(data);
    }
}*/
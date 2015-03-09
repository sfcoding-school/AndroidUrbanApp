package it.urban.urbanapp.urbanapp2.helper;

/**
 * Created by alexander on 02/03/15.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import it.urban.urbanapp.urbanapp2.EventoActivity;
import it.urban.urbanapp.urbanapp2.R;




class JsonParser {

    private InputStream is = null;
    private JSONObject jObj = null;
    private String json = "";

    // constructor
    public JsonParser() {
    }

    public JSONObject getJSONFromUrl(String url) {
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json).getJSONObject(0).getJSONObject("fields");
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;
    }

}

public class JSONParse extends AsyncTask<Void, Void, JSONObject> {

    public String urll;
    private EventoActivity eventActivity;
    private JSONObject response;

    public JSONParse(String url, EventoActivity act){
        eventActivity = act;
        urll = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        eventActivity.findViewById(R.id.innerlinear).setVisibility(View.GONE);


    }

    @Override
    protected JSONObject doInBackground(Void... args) {
        JsonParser jParser = new JsonParser();
        // Getting JSON from URL
        JSONObject json = jParser.getJSONFromUrl(urll);
        response = json;
        return json;

    }
    @Override
    protected void onPostExecute(JSONObject json) {
        eventActivity.findViewById(R.id.innerlinear).setVisibility(View.VISIBLE);
        TextView data = (TextView) eventActivity.findViewById(R.id.tvdataevento);
        TextView titolo = (TextView) eventActivity.findViewById(R.id.tvtitoloevento);
        TextView descr = (TextView) eventActivity.findViewById(R.id.descrizione);
        Log.d("+++++++++++++++",response.toString());

        try {
            eventActivity.scaricato = true;
            data.setText(response.getString("data"));
            titolo.setText(response.getString("titoloSerata"));
            descr.setText(response.getString("descr"));
        } catch (JSONException e){
            e.printStackTrace();
        }


    }
}

package it.urban.urbanapp.urbanapp2;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import it.urban.urbanapp.urbanapp2.helper.ConnectionHelper;


public class LoginActivity extends ActionBarActivity {

    private static ImageView foto_profilo = null;
    public String quale = "profilo";
    private int view_profilo = 0;
    private String TAG = "LoginActivity";
    private static String facebookUserName;
    private TextView lblLogOrName;
    SharedPreferences prefs;
    public static final String REG_USERNAME = "reg_username";
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    public final String REG_ID = "reg_id";
    Session session;

    public SharedPreferences getPreferences() {
        return getSharedPreferences("profilo", Context.MODE_PRIVATE);
    }

    private void savePreferences(String username_t, String id_fb_t) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_USERNAME, username_t);
        editor.putString(REG_ID, id_fb_t);
        editor.commit();

    }

    public static String getFacebookUserName(Context context) {
        if (facebookUserName != null)
            return facebookUserName;
        else {
            SharedPreferences prefs = context.getSharedPreferences("profilo", Context.MODE_PRIVATE);
            String name = prefs.getString(REG_USERNAME, "");
            if (name.isEmpty()) {
                Log.e("HELPER_FACEBOOK", "username facebook not found.");
                return null;
            } else {
                facebookUserName = name;
                return facebookUserName;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle data = getIntent().getExtras();
        String info;
        if (data != null) {
            info = data.getString("from_settings");
            view_profilo = Integer.parseInt(info);
        }

        foto_profilo = (ImageView) findViewById(R.id.imvProfile);

        prefs = getPreferences();
        session = Session.getActiveSession();

        lblLogOrName = (TextView) findViewById(R.id.lblLogOrName);
        if (getFacebookUserName(getApplicationContext()) != null && !session.getState().toString().equals("CREATED") && !session.isClosed()) {
            lblLogOrName.setText(getFacebookUserName(getApplicationContext()));
        }

        if (view_profilo == 1) {
            loadImageFromStorage("profile");
        }
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setOnErrorListener(new LoginButton.OnErrorListener() {

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "Error " + error.getMessage());
            }
        });

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        authButton.setReadPermissions(Arrays.asList("public_profile"));
        // session state call back eventname java
        authButton.setSessionStatusCallback(statusCallback);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        Log.d("ON ACT RESULT", "-----------------------------------------------------");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                Toast.makeText(cw, "Connettiti all'Internet", Toast.LENGTH_LONG).show();
                return;
            }

            if (session.isOpened()) {
                Log.i(TAG, "Access Token" + session.getAccessToken());
                Request.newMeRequest(session,
                        new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(final GraphUser user, Response response) {
                                Session sessione = session;
                                if (user != null) {
                                    Bundle params = new Bundle();
                                    params.putBoolean("redirect", false);
                                    params.putString("height", "400");
                                    params.putString("type", "normal");
                                    params.putString("width", "400");
                                    new Request(sessione, "/me/picture", params, HttpMethod.GET, new Request.Callback() {

                                        public void onCompleted(Response response) {
                                            try {

                                                String url = response.getGraphObject().getInnerJSONObject().getJSONObject("data").getString("url");
                                                getFacebookProfilePicture(url);
                                                userRegister(session, user);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    ).executeAsync();
                                    Log.i(TAG, "User ID " + user.getId());
                                    //Log.i(TAG, "Email " + user.asMap().get("email"));
                                    lblLogOrName.setText(user.getFirstName() + " " + user.getLastName());
                                    savePreferences(user.getFirstName() + " " + user.getLastName(), user.getId());

                                }
                            }
                        }).executeAsync();
                finish();
                //Intent main_act = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(main_act);
            } else if (session.isClosed()) {
                lblLogOrName.setText(R.string.login);
                foto_profilo.setVisibility(View.GONE);
            }
        }
    }

    private void loadImageFromStorage(String quale) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, "profile" + quale + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            foto_profilo.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveToInternalStorage(Bitmap bitmapImage, String quale) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile" + quale + ".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String S = directory.getAbsolutePath();
        Log.d("ttttttttttttttttt", directory.getAbsolutePath());
    }

    private void getFacebookProfilePicture(String url) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... args) {
                URL imageURL;
                Bitmap bitmap = null;

                try {
                    imageURL = new URL(args[0]);
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    saveToInternalStorage(bitmap, "profile");
                    foto_profilo.setImageBitmap(bitmap);
                }
            }
        }.execute(url);
    }


    private void userRegister(final Session session, final GraphUser user) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... args) {
                String RegisterBaseUrl = "http://uapp.sfcoding.com/newuser/";
                String token = session.getAccessToken();
                String userID = user.getId();
                String cellID = "000000000000000000000";
                String regResponse = ConnectionHelper.getDataFromUrl(RegisterBaseUrl + "?fbID=" + userID + "&idCell=" + cellID + "&fbToken=" + token);
                Log.i("UserRegister", regResponse);
                if (regResponse.equals("OK"))
                    return true;

                return false;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (!ok)
                    session.closeAndClearTokenInformation();
                Session.setActiveSession(null);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }



    public void onBackPressed() {

        if(session.isClosed()) {
            moveTaskToBack(true);
        }
        else {
            finish();
        }
   }


}
package it.urban.urbanapp.urbanapp2;

import it.urban.urbanapp.urbanapp2.helper.TypefaceHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;

import android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity {

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView myTextView = (TextView)findViewById(R.id.tvtitle);
        myTextView.setTypeface(TypefaceHelper.get(getApplicationContext(),"Randi.ttf"));
        myTextView.setText("Urban");

    }

    @Override
    public void onStart() {
        super.onStart();
        session = Session.getActiveSession();
        if (session == null) {
            session = new Session(this);
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this));
            }
        }
        if (!session.isOpened()) {
            Intent newact = new Intent(this, LoginActivity.class);
            startActivity(newact);

        }

        String token = getToken();

        Button friday = (Button) findViewById(R.id.friday);
        friday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEventActivity("6");
            }
        });

        Button saturday = (Button) findViewById(R.id.saturday);
        saturday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEventActivity("7");
            }
        });

        Button sunday = (Button) findViewById(R.id.bellaciao);
        sunday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEventActivity("1");
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_profile) {
            Intent newact = new Intent(this, LoginActivity.class);
            newact.putExtra("from_settings", "1");
            startActivity(newact);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getToken() {
        String token = session.getAccessToken();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("profilo", Context.MODE_PRIVATE);
        String reg_id = prefs.getString("reg_id", "");
        Log.e("REG_ID", reg_id);
        Log.e("TOKEN - getToken", token);
        return token;
    }

    public void goToEventActivity(String day){
        Intent newact = new Intent(this, EventoActivity.class);
        newact.putExtra("day", day);
        startActivity(newact);
        //return true;
    }
}


/*
class Typefaces {
    private static final String TAG = "Typefaces";

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
*/
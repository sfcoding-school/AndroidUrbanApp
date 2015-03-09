package it.urban.urbanapp.urbanapp2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import it.urban.urbanapp.urbanapp2.helper.ConnectionHelper;
import it.urban.urbanapp.urbanapp2.helper.DataProvider;
import it.urban.urbanapp.urbanapp2.helper.JSONParse;
import it.urban.urbanapp.urbanapp2.helper.MyBroadcastReceiver;
import it.urban.urbanapp.urbanapp2.helper.TypefaceHelper;



public class EventoActivity extends ActionBarActivity {
    public Boolean scaricato = false;
    String day;
    JSONObject eventoJson;
    Session session;
    String token;
    private PendingIntent pendingIntent;
    private static EventoActivity istanzaEventoActivity = null;


    public static EventoActivity getInstance(){
        if(istanzaEventoActivity == null)
            istanzaEventoActivity= new EventoActivity();
        return istanzaEventoActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.istanzaEventoActivity = this;

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            this.scaricato = savedInstanceState.getBoolean("scaricato");
        }
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
        String ciao = session.getAccessToken();
        this.token = ciao;
        /*session = Session.getActiveSession();

        this.token = session.getAccessToken();
        */
        setContentView(R.layout.activity_evento);
        TextView myTextView = (TextView) findViewById(R.id.tvtitle);
        myTextView.setTypeface(TypefaceHelper.get(getApplicationContext(), "Randi.ttf"));
        myTextView.setText("Urban");
        Intent intent = getIntent();
        Log.e("INTENT",intent.toString());
        Bundle data = getIntent().getExtras();
        day = data.getString("day");

        Button youtube = (Button) findViewById(R.id.youtube);

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/watch?v=NYw8GOydiMQ";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        final Button btninlista = (Button) findViewById(R.id.btninlista);
        btninlista.setOnClickListener(new MyOnClickListener(this));
        /*
        JSONParse jp = new JSONParse("http://uapp.sfcoding.com/getnextevent/?day=" + day, this);
        jp.execute();
        */
        //DataProvider.loadJson(day, this);
        DataProvider.getEvent(day, this);
    }

    public void seiInLista(Boolean ok){
            Button btninlista = (Button) findViewById(R.id.btninlista);
        if(ok) {
            btninlista.setText("SEI IN LISTA");
            btninlista.setEnabled(false);
            try {
                eventoJson.remove("prevenditeBool");
                eventoJson.getJSONObject("fields").put("prevenditeBool", -1);
                String ora = " "+eventoJson.getJSONObject("fields").getString("ora");
                String data = eventoJson.getJSONObject("fields").getString("data");
                Log.e("ogggetto jsson",eventoJson.toString());
                DataProvider.saveJson(eventoJson,day,this);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);

                try {
                    Date date = format.parse(data);
                    Calendar c = new GregorianCalendar();
                    c.setTime(date);
                    setAlarm(c);
                    Log.e("DATA : ",date.toString());
                } catch (ParseException e){
                    e.printStackTrace();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        else
            btninlista.setText("METTIMI IN LISTA");
     }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean("scaricato", this.scaricato);
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imv = (ImageView) findViewById(R.id.imageView);
        //int a = imv.getWidth();
        //int b = imv.getHeight();
        imv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int ID;
        if(day.equals("6")){ID = R.drawable.main_bg;}
        else{ID = R.drawable.urbansabato;}
        imv.setImageBitmap(decodeSampledBitmapFromResource(getResources(), ID, 150, 150));
    }

    @Override
    public void onStart() {
        super.onStart();
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void updateView(JSONObject jsonObject) {
        if (jsonObject!=null){
            findViewById(R.id.innerlinear).setVisibility(View.VISIBLE);
            TextView tvdata = (TextView) findViewById(R.id.tvdataevento);
            TextView tvtitolo = (TextView) findViewById(R.id.tvtitoloevento);
            TextView tvdescr = (TextView) findViewById(R.id.descrizione);
            Button inLista = (Button) findViewById(R.id.btninlista);
            TextView tvartista = (TextView) findViewById(R.id.artista);
            TextView tvPLista = (TextView) findViewById(R.id.tvprezzolista);
            TextView tvPIntero = (TextView) findViewById(R.id.tvprezzointero);
            try {
                this.eventoJson = jsonObject;
                int sonoInLista = eventoJson.getJSONObject("fields").getInt("prevenditeBool");
                int interoLista = eventoJson.getJSONObject("fields").getInt("listaBool");
                String artista = eventoJson.getJSONObject("fields").getString("artisti");
                int pLista = eventoJson.getJSONObject("fields").getInt("prezzoLista");
                int pIntero = eventoJson.getJSONObject("fields").getInt("prezzoIntero");
                String linkArtista = eventoJson.getJSONObject("fields").getString("linkLocandina");
                String linkYT = eventoJson.getJSONObject("fields").getString("linkVideo");
                String linkFB = eventoJson.getJSONObject("fields").getString("linkFBEvent");


                if (interoLista==0){
                    findViewById(R.id.linearlista).setVisibility(View.GONE);
                    findViewById(R.id.titoloprezzo).setVisibility(View.GONE);
                    inLista.setEnabled(false);
                    StringBuilder sb = new StringBuilder (String.valueOf("PREZZO UNICO "));
                    sb.append(pIntero); sb.append(" €"); inLista.setText(sb.toString());
                } else{
                    findViewById(R.id.linearlista).setVisibility(View.VISIBLE);
                    findViewById(R.id.titoloprezzo).setVisibility(View.VISIBLE);
                    StringBuilder sbLista = new StringBuilder (String.valueOf("In Lista\n"));
                    StringBuilder sbIntero = new StringBuilder (String.valueOf("Intero\n"));
                    sbIntero.append(pIntero); sbLista.append(pLista); sbIntero.append(" €"); sbLista.append(" €");
                    tvPLista.setText(sbLista.toString());
                    tvPIntero.setText(sbIntero.toString());
                    if(sonoInLista==-1) {
                        inLista.setText("SEI IN LISTA");
                        inLista.setEnabled(false);
                    } else {
                        inLista.setEnabled(true);
                        inLista.setText("METTIMI IN LISTA");
                    }
                }


                findViewById(R.id.artista).setOnClickListener(new LinkClickListener(this,linkArtista));
                findViewById(R.id.youtube).setOnClickListener(new LinkClickListener(this,linkYT));
                findViewById(R.id.facebook).setOnClickListener(new LinkClickListener(this,linkFB));
                JSONObject fields = jsonObject.getJSONObject("fields");
                tvartista.setText(artista);
                tvdata.setText(fields.getString("data"));
                tvtitolo.setText(fields.getString("titoloSerata"));
                tvdescr.setText(fields.getString("descr"));
            } catch (JSONException e){
                e.printStackTrace();
                }
        }else{

            findViewById(R.id.innerlinear).setVisibility(View.INVISIBLE);
            /*
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Event informations are not available yet! \n\nPlease check again later..");
            builder1.setTitle("Bad News! :( ");
            builder1.setCancelable(false);
            builder1.setIcon(android.R.drawable.ic_dialog_alert);
            builder1.setPositiveButton("Ok!",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();*/
        }
    }

    public String prepareDataForLista(){
        try {
            String baseUrl = "http://uapp.sfcoding.com/putuserinlista/";
            SharedPreferences prefs = this.getSharedPreferences("profilo", Context.MODE_PRIVATE);
            String ID = prefs.getString("reg_id", "");
            int eventoID = this.eventoJson.getInt("pk");
            String eventoString = String.valueOf(eventoID);
            String finalUrl = baseUrl + "?fbID=" + ID + "&fbToken=" + this.token + "&event=" + eventoString;
            return finalUrl;
        } catch (JSONException e){
            e.printStackTrace();
            return "";
        }
    }

    public static void putUserInlista(final EventoActivity eventoActivity, final String url){

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String risposta = ConnectionHelper.getDataFromUrl(url);
                if(risposta.equals("OK"))
                    return true;
                return false;
            }

            @Override
            protected void onPostExecute(Boolean ok){

                if(ok) {
                    Toast.makeText(eventoActivity, "Sei in lista!", Toast.LENGTH_LONG).show();
                    eventoActivity.seiInLista(ok);
                }
                else {
                    Toast.makeText(eventoActivity, "Ooops.. :( Riprova più tardi!", Toast.LENGTH_LONG).show();
                    eventoActivity.seiInLista(ok);

                }
            }
        }.execute(null,null,null);
    }


    public void setAlarm(Calendar calendar){

        calendar.set(Calendar.HOUR_OF_DAY,22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, cur_cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cur_cal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent myIntent = new Intent(EventoActivity.this, MyBroadcastReceiver.class);
        myIntent.putExtra("day",this.day);
        pendingIntent = PendingIntent.getBroadcast(EventoActivity.this, 0, myIntent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
    }
}


class LinkClickListener implements View.OnClickListener
{
    EventoActivity eventoActivity;
    String link;
    public LinkClickListener(EventoActivity act,String link) {
        this.eventoActivity = act;
        this.link = link;
    }
    @Override
    public void onClick(View v){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        eventoActivity.startActivity(i);
        }
    };

class MyOnClickListener implements View.OnClickListener
{
    EventoActivity eventoActivity;

    public MyOnClickListener(EventoActivity act) {
        this.eventoActivity = act;
    }

    @Override
    public void onClick(View v)
    {
        if(DataProvider.checkConection(eventoActivity)) {
            String url = eventoActivity.prepareDataForLista();
            EventoActivity.putUserInlista(eventoActivity, url);
            ((Button) eventoActivity.findViewById(R.id.btninlista)).setText("Ci Siamo Quasi..");
        }
    }
};


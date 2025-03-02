package com.example.randtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    String apiKey = "WZVJVU5KXlTNzfc0hCgCjDH2ZKS7r1xD";

    //urlPiece1+{to}+urlPiece2+{from}+urlPiece3+{amount}
    String urlPiece1 = "https://api.apilayer.com/currency_data/convert?to=";
    String urlPiece2 = "&from=";
    String urlPiece3 = "&amount=";

    EditText fromView;
    EditText toView;
    ImageButton convertBtn;
    ImageButton switchBtn;
    TextView amountView;
    Spinner fromSpinner;
    Spinner toSpinner;
    EditText amountEditor;

    HashMap<String, String> currencies = new HashMap<String, String>();

//    class JSONGetter extends AsyncTask<String, Void, String> {
//
//        StringBuilder result = new StringBuilder();
//        URL url;
//        HttpURLConnection urlConnection = null;
//
//        @Override
//        protected String doInBackground(String... urls) {
//
//            try {
//
//                url = new URL(urls[0]);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestProperty("apikey", apiKey);
//
//                InputStream inputStream = urlConnection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String data;
//
//                while ((data = reader.readLine()) != null) {
//
//                    result.append(data);
//
//                }
//
//                return result.toString();
//
//            } catch (Exception e) {
//                Log.i("Result with error", result.toString());
//                e.printStackTrace();
//            }
//
//
//            return "Done";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                JSONObject jsonRes = new JSONObject(result);
//                Log.i("Res", result);
//
//                amount = Double.parseDouble(jsonRes.getString("result"));
//                Log.i("Amount", "" + amount);
//
//                amountView.setText(String.valueOf(amount));
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromView = (EditText) findViewById(R.id.fromView);
        toView = (EditText) findViewById(R.id.toView);
        convertBtn = (ImageButton) findViewById(R.id.convertBtn);
        switchBtn = (ImageButton) findViewById(R.id.switchBtn);
        amountView = (TextView) findViewById(R.id.amountView);
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        amountEditor = (EditText) findViewById(R.id.amountEditor);

        setupCurrencyHash();

        spinnerSetup();

        convertBtn.setOnClickListener(view -> {
            updateUI();
        });
        switchBtn.setOnClickListener(view -> {
            swap();
        });
    }

    public void getAmount(String from, String to, double amount) {

        Thread thread = new Thread((Runnable) () -> {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urlPiece1 + to + urlPiece2 + from + urlPiece3 + amount);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("apikey", apiKey);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String data;

                while ((data = reader.readLine()) != null) {

                    result.append(data);

                }

                Log.i("Res", result.toString());
                JSONObject jsonRes = new JSONObject(result.toString());

                Double resAmount = Double.parseDouble(jsonRes.getString("result"));
                amountView.setText(String.valueOf(resAmount));

            }
            catch (Exception e) {
                Log.i("Result with error", result.toString());

                try {
                    JSONObject jsonRes = new JSONObject(result.toString());
                    showToast(jsonRes.getJSONObject("error").getString("info"));
                } catch (JSONException ex) {
                    e.printStackTrace();
                }

                e.printStackTrace();

            }
        });

        thread.start();

    }

    public void showToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public void setupCurrencyHash() {
        currencies.put("Canada", "CAD");
        currencies.put("USA", "USD");
        currencies.put("Russia", "RUB");
        currencies.put("South Africa", "ZAR");
        currencies.put("Switzerland", "CHF");
        currencies.put("UK", "GBP");
        currencies.put("Europe", "EUR");
        currencies.put("Japan", "JPY");
        currencies.put("China", "CNY");
        currencies.put("Singapore", "SGD");
        currencies.put("New Zealand", "NZD");
        currencies.put("Australia", "AUD");
        currencies.put("Kuwaiti", "KWD");
    }

    public void swap() {
        Editable from = fromView.getText();
        fromView.setText(toView.getText());
        toView.setText(from);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        CharSequence country = item.getTitle();

        Toast.makeText(getApplicationContext(), currencies.get(country), Toast.LENGTH_SHORT).show();

//        toView.setText(currencies.get(country));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        int i = 0;
        for(String key : currencies.keySet()) {
            menu.add(0, Menu.FIRST + i, Menu.NONE, key);
            i++;
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateUI() {

        String to = toView.getText().toString().trim();
        String from = fromView.getText().toString().trim();

        if(amountEditor.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter a value to be converted", Toast.LENGTH_SHORT).show();
            return;
        }

        double reqAmount = Double.parseDouble(amountEditor.getText().toString());

        if(to.equals("") || from.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter currencies to be converted", Toast.LENGTH_SHORT).show();
            return;
        }

        if(reqAmount == 0) {
            Toast.makeText(getApplicationContext(), "Enter amount to be converted", Toast.LENGTH_SHORT).show();
            return;
        }

//        requestJSON(from, to, reqAmount);

        getAmount(from, to, reqAmount);
    }

    public void spinnerSetup() {

        ArrayList<String> countries = new ArrayList<>(currencies.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                countries);

        toSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(adapter);

        Log.i("Spinner", toSpinner.toString());
        Log.i("Spinner", fromSpinner.toString());
        for(int i = 0; i < currencies.size(); i++) {
            Log.i("Adapter", toSpinner.getAdapter().getItem(i).toString());
        }

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String item = adapterView.getItemAtPosition(i).toString();

                    toView.setText(currencies.get(item));

                }
                catch (NullPointerException e) {
                    Log.i("Null Pointer", String.valueOf(i));
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String item = adapterView.getItemAtPosition(i).toString();

                    fromView.setText(currencies.get(item));

                }
                catch (NullPointerException e) {
                    Log.i("Null Pointer", String.valueOf(i));
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}

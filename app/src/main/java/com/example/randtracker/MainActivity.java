package com.example.randtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String apiKey = "WZVJVU5KXlTNzfc0hCgCjDH2ZKS7r1xD";

    //urlPiece1+{to}+urlPiece2+{from}+urlPiece3+{amount}
    String urlPiece1 = "https://api.apilayer.com/currency_data/convert?to=";
    String urlPiece2 = "&from=";
    String urlPiece3 = "&amount=";

    double amount;

    EditText fromView;
    EditText toView;
    ImageButton convertBtn;
    TextView amountView;
    Spinner fromSpinner;
    Spinner toSpinner;
    EditText amountEditor;

    HashMap<String, String> currencies = new HashMap<String, String>();

    class JSONGetter extends AsyncTask<String, Void, String> {

        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... urls) {

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("apikey", apiKey);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String data;

                while ((data = reader.readLine()) != null) {

                    result.append(data);

                }

                return result.toString();

            } catch (Exception e) {
                Log.i("Result with error", result.toString());
                e.printStackTrace();
            }


            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonRes = new JSONObject(result);
                Log.i("Res", result);

                amount = Double.parseDouble(jsonRes.getString("result"));
                Log.i("Amount", "" + amount);

                amountView.setText(String.valueOf(amount));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromView = (EditText) findViewById(R.id.fromView);
        toView = (EditText) findViewById(R.id.toView);
        convertBtn = (ImageButton) findViewById(R.id.convertBtn);
        amountView = (TextView) findViewById(R.id.amountView);
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        amountEditor = (EditText) findViewById(R.id.amountEditor);

        setupCurrencyHash();
        spinnerSetup();

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });



    }


    public void requestJSON(String from, String to, int amount) {
        JSONGetter jsonGetter = new JSONGetter();
        try {
            jsonGetter.execute(urlPiece1 + to + urlPiece2 + from + urlPiece3 + amount);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        CharSequence country = item.getTitle();

        Toast.makeText(getApplicationContext(), currencies.get(country), Toast.LENGTH_SHORT).show();

        //toView.setText(currencies.get(country));

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
        int reqAmount = Integer.parseInt(amountEditor.getText().toString());

        if(to.equals("") || from.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter currencies to be converted", Toast.LENGTH_SHORT).show();
            return;
        }

        if(reqAmount == 0) {
            Toast.makeText(getApplicationContext(), "Enter amount to be converted", Toast.LENGTH_SHORT).show();
            return;
        }

        requestJSON(from, to, reqAmount);


    }

    public void spinnerSetup() {

        ArrayList<String> countries = new ArrayList<>();
        countries.addAll(currencies.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);

        toSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(adapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                toView.setText(currencies.get(item));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                fromView.setText(currencies.get(item));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
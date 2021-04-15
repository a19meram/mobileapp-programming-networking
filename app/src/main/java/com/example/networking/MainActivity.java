package com.example.networking;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;

public class MainActivity extends AppCompatActivity {

    private TextView textView_info;
    private ArrayList<Mountain> mountainArrayList = new ArrayList<>();
    private ArrayAdapter<Mountain> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mountainArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,R.layout.list_item_textview, R.id.list_item_textview, mountainArrayList);
        new JsonTask().execute("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");
        final ListView my_listview = (ListView) findViewById(R.id.my_listview);
        my_listview.setAdapter(adapter);
        textView_info = findViewById(R.id.textview_info);
        Snackbar make = Snackbar.make(my_listview, "Hela vägen ", LENGTH_INDEFINITE);
        make.setAction("Open Toast", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = mountainArrayList.get(position).getName("name");
                        String location = mountainArrayList.get(position).getLocation("Location");
                        int size = mountainArrayList.get(position).getSize("size");

                        String meddelande = name + " is located in mountain range " + location + " and reaches " + size + " m above the sea level.";

                        Toast.makeText(MainActivity.this, meddelande, Toast.LENGTH_LONG).show();
                    }
                });
                textView_info.setVisibility(View.INVISIBLE);
            }
        });
        make.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !isCancelled()) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {

            try {
                adapter.clear();
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String location = jsonObject.getString("location");
                    int size = jsonObject.getInt("size");


                    mountainArrayList.add(new Mountain(name, size, location));
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TAG", json);
        }
    }
}

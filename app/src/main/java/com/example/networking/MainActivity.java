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

        ListView my_listview = (ListView) findViewById(R.id.my_listview);
        my_listview.setAdapter(adapter);
        new JsonTask().execute("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");
        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name = mountainArrayList.get(position).getName("name");
                String location = mountainArrayList.get(position).getLocation("Location");
                int size = mountainArrayList.get(position).getSize("size");

                String meddelande = name + " is located in mountain range " + location + " and reaches " + size + " m above the sea level.";

                Toast.makeText(MainActivity.this, meddelande, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;


        /*
        private ArrayList<String> mountainName = new ArrayList<String>();
        private ArrayList<String> mountainLocation = new ArrayList<String>();
        private ArrayList<Integer> mountainSize = new ArrayList<Integer>();*/

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

                /*
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item_textview, R.id.list_item_textview, mountainArrayList);
                final ListView my_listview = (ListView) findViewById(R.id.my_listview);
                my_listview.setAdapter(adapter);
                textView_info = findViewById(R.id.textview_info);
                final Snackbar snackbar = make(my_listview,"Open------------------------------>", LENGTH_INDEFINITE);
                snackbar.setAction("Open Toast", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                                Toast.makeText(getApplicationContext(), mountainName.get(i) + " is located in mountain range " + mountainLocation.get(i) + " and reaches " + mountainSize.get(i) + " m above the sea level.", Toast.LENGTH_LONG).show();
                                String name = mountainName.get(i);
                                mountainName.clear();
                                mountainName.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        textView_info.setVisibility(View.INVISIBLE);
                    }
                });
                snackbar.show();
*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TAG", json);
        }
    }
}

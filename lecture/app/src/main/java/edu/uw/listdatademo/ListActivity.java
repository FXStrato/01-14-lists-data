package edu.uw.listdatademo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Model
        String[] data = new String[99];
        for (int i = 99; i > 0; i--) {
            if (i == 1) data[99 - 1] = i + " bottle of beer on the wall";
            else data[99 - i] = i + " bottles of beer on the wall";
        }

        //String[] data = downloadMovieData("Interstellar");
        MovieTask task = new MovieTask();
        task.execute("Interstellar");

        //Controller
        adapter = new ArrayAdapter<String>(
                this, R.layout.list_item, R.id.txtItem, data);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

    public class MovieTask extends AsyncTask<String,Void,String[]> {

        protected String[] doInBackground(String... params) {
            String movie = params[0];

            //construct the url for the omdbapi API
            String urlString = "http://www.omdbapi.com/?s=" + movie + "&type=movie";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movies[] = null;

            try { //Attempt to try and make a connection to omdbapi

                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Receive the data, and prepare to read it line by line.
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                //While there is still lines to read
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                // In the buffer, replace various things to make it valid json
                String results = buffer.toString();
                results = results.replace("{\"Search\":[","");
                results = results.replace("]}","");
                results = results.replace("},", "},\n");
                Log.v("ListActivity", results);
                movies = results.split("\n");
            }
            catch (IOException e) {
                return null;
            }
            finally {
                //This is called at the very end, closes the url connection.
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                //Called at the end; closes the reader.
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                    }
                }
            }

            return movies;
        }
    }

    public static String[] downloadMovieData(String movie) {

        //construct the url for the omdbapi API
        String urlString = "http://www.omdbapi.com/?s=" + movie + "&type=movie";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movies[] = null;

        try { //Attempt to try and make a connection to omdbapi

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Receive the data, and prepare to read it line by line.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            //While there is still lines to read
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            // In the buffer, replace various things to make it valid json
            String results = buffer.toString();
            results = results.replace("{\"Search\":[","");
            results = results.replace("]}","");
            results = results.replace("},", "},\n");

            movies = results.split("\n");
        }
        catch (IOException e) {
            return null;
        }
        finally {
            //This is called at the very end, closes the url connection.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //Called at the end; closes the reader.
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e) {
                }
            }
        }

        return movies;
    }

    protected void onPostExecute(String[] movies){
        adapter.clear();
        for(String movie: movies) {
            adapter.add(movie);
        }
    }
}
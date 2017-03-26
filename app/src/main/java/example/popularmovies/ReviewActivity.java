package example.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {
    public static  ArrayList<String> auther = new ArrayList<String>();
    public static ArrayList<String> review = new ArrayList<String>();
    ListView reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        reviewList = (ListView)findViewById(R.id.listReview);
        new connect().execute();

    }
    public class connect extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            reviewList.setAdapter(new ReviewAdapter(getApplicationContext(),auther,review));

        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String trailerJsonString = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                URL url;
                Log.d("heyy",getIntent().getStringExtra("id"));
                String baseUrl = "http://api.themoviedb.org/3/movie/"+getIntent().getStringExtra("id")+"/reviews?api_key=";
                String apiKey = "e6f375a40042abd253b3b3601cf0f895";
                url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJsonString = buffer.toString();

                Log.d("json", trailerJsonString);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try{

                JSONObject page = new JSONObject(trailerJsonString);
                JSONArray result = page.getJSONArray("results");
                if(!auther.isEmpty()){
                    auther.clear();
                    review.clear();
                }

                for(int i = 0;i<result.length();i++){

                    JSONObject jsonObj = result.getJSONObject(i);
                    String content = jsonObj.getString("content");
                    String author = jsonObj.getString("author");
                    auther.add(author);
                    review.add(content);

                }

            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}

package example.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class DetailActivity extends AppCompatActivity {
    ImageView thumb ;
    TextView name , overview,release,rating  ;
    String baseImage = "http://image.tmdb.org/t/p/w185";
    Button btn ,reviews;
    ListView trailers;

    int counter=0;
    public static ArrayList<String> trailersID=new ArrayList<String>();
    ArrayList<String> trailerNames = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        thumb = (ImageView) findViewById(R.id.thumb);
        name = (TextView) findViewById(R.id.name);
        overview = (TextView) findViewById(R.id.overview);
        release = (TextView) findViewById(R.id.release);
        rating = (TextView) findViewById(R.id.rating);
        btn = (Button) findViewById(R.id.button);
        trailers = (ListView) findViewById(R.id.trailers);
        reviews = (Button) findViewById(R.id.reviews);
        new Trailers().execute();
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this,ReviewActivity.class);
                i.putExtra("id",getIntent().getStringExtra("ID"));
                startActivity(i);


            }
        });

        if(checkDB()){
            btn.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkDB())
                {
                    btn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    MainActivity.handler.add(getIntent().getStringExtra("name"));
                    counter++;


                } else {
                    btn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    MainActivity.handler.delete(getIntent().getStringExtra("name"));
                    counter++;

                }

            }
        });


        name.setText(getIntent().getStringExtra("name"));
        overview.setText(getIntent().getStringExtra("overview"));
        overview.setMovementMethod(new ScrollingMovementMethod());
        Picasso
                .with(getApplicationContext())
                .load(baseImage+getIntent().getStringExtra("path"))
                .fit() // will explain later
                .into(thumb);

        release.setText("Release Date : " + getIntent().getStringExtra("release"));
        rating.setText("Rating : "+getIntent().getStringExtra("rate")+" /10");

    }

    public boolean checkDB(){
        boolean nameFound = false;
        String[] result_columns = new String[] {
                DBHandler.COLOMN_NAME};
// Specify the where clause that will limit our results.
        String where = null;
// Replace these with valid SQL statements as necessary.
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        SQLiteDatabase db = MainActivity.handler.getWritableDatabase();
        Cursor cursor = db.query(DBHandler.TABLE_NAME,
                result_columns, where, whereArgs, groupBy, having, order);
        int index = cursor.getColumnIndexOrThrow(DBHandler.COLOMN_NAME);
        while (cursor.moveToNext()) {

            Log.d("hey",cursor.getString(index) + "");
            if(cursor.getString(index).equals(getIntent().getStringExtra("name"))){
                Log.d("ana gowa","la2eto");
                nameFound=true;
            }


        }
        return nameFound;
    }
   public class Trailers extends AsyncTask<Void,Void,Void>{

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           trailers.setAdapter(new TrailerAdapter(getApplicationContext(),trailersID,trailerNames));
           trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+trailersID.get(position)));
                   startActivity(i);
               }
           });

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
                    String baseUrl = "http://api.themoviedb.org/3/movie/"+getIntent().getStringExtra("ID")+"/videos?api_key=";
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
                if(!trailersID.isEmpty()){
                    trailersID.clear();
                    trailerNames.clear();
                }

                JSONObject page = new JSONObject(trailerJsonString);
                JSONArray result = page.getJSONArray("results");
                for(int i = 0;i<result.length();i++){
                    JSONObject jsonObj = result.getJSONObject(i);
                    String trailerKey = jsonObj.getString("key");
                    String trailerName=jsonObj.getString("name");
                    trailersID.add(trailerKey);
                    trailerNames.add(trailerName);

                }

            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}

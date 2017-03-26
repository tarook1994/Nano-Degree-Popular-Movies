package example.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
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


public class MainActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    GridView grid ;
    ArrayList<String> preParam = new ArrayList<String>();

    String baseImage = "http://image.tmdb.org/t/p/w185";
    public static String[] imageURLS;
    ArrayList<String> namesPOP = new ArrayList<String>();
    ArrayList<String> overviewsPOP = new ArrayList<String>();
    ArrayList<String> ratingPOP = new ArrayList<String>();
    ArrayList<String> releasesPOP = new ArrayList<String>();
    ArrayList<String> ID = new ArrayList<String >();

    ArrayList<String>namesPopAndTop= new ArrayList<String>();
    ArrayList<String>overviewPopAndTop= new ArrayList<String>();
    ArrayList<String>ratingPopAndTop= new ArrayList<String>();
    ArrayList<String>releasePopAndTop= new ArrayList<String>();
    ArrayList<String>IDPopAndTop= new ArrayList<String>();
    ArrayList<String>URLPopAndTop= new ArrayList<String>();

    ArrayList<String> IDfav = new ArrayList<String >();
    ArrayList<String> namesfav = new ArrayList<String>();
    ArrayList<String> overviewsfav = new ArrayList<String>();
    ArrayList<String> ratingfav = new ArrayList<String>();
    ArrayList<String> releasesfav = new ArrayList<String>();
    ArrayList<String> urlFav = new ArrayList<String>();
    ArrayList<String> Poptemp = new ArrayList<String>();

    static DBHandler handler ;
    boolean top , pop , fav;


    boolean isTablet=false;
    int counter =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ay7aga);
        top = false;
        pop = true;
        fav = false;
        if(findViewById(R.id.decider)==null){
            isTablet=false;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            isTablet=true;

        }


        new Connect().execute();
        grid = (GridView) findViewById(R.id.gridview);
        handler = new DBHandler(getApplicationContext());
    }

    public String[] listToArray(ArrayList<String> x){
        String[] lastArray = new String[x.size()];

        for(int i =0 ;i<x.size();i++){
            lastArray[i] = baseImage+x.get(i);
        }
        return lastArray;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public ArrayList<Integer> findDuplicates(ArrayList<String> name){
        ArrayList<Integer> duplicates= new ArrayList<Integer>();
        for(int i =0;i<name.size();i++){
            for(int j=i+1;j<name.size();j++ ){
                if(name.get(i).equals(name.get(j))){
                     duplicates.add(j);
                }
            }
        }
        if(duplicates.size()>0){
            return duplicates;
        }else {
            duplicates.add(1000);
        }
        return duplicates;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {


                ArrayList<Integer> favIndices= new ArrayList<Integer>();
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
                SQLiteDatabase db = handler.getWritableDatabase();
                Cursor cursor = db.query(DBHandler.TABLE_NAME,
                        result_columns, where, whereArgs, groupBy, having, order);
                int index = cursor.getColumnIndexOrThrow(DBHandler.COLOMN_NAME);

                while (cursor.moveToNext()) {
                    Log.d("esm el gowa el db",cursor.getString(index) + "");
                    for(int j=0;j<namesPopAndTop.size();j++){
                        if(cursor.getString(index).equals(namesPopAndTop.get(j))){
                            Log.d("hwa fe kam index beyrg3",""+index);
                            favIndices.add(j);
                        }
                    }
                }
                if(favIndices.size()!=0){
                    if(namesfav.size()>0){
                        namesfav.clear();
                        overviewsfav.clear();
                        ratingfav.clear();
                        releasesfav.clear();
                        IDfav.clear();
                        urlFav.clear();
                    }
                    for(int j =0;j<favIndices.size();j++){

                        namesfav.add(namesPopAndTop.get(favIndices.get(j)));
                        overviewsfav.add(overviewPopAndTop.get(favIndices.get(j)));
                        ratingfav.add(ratingPopAndTop.get(favIndices.get(j)));
                        releasesfav.add(releasePopAndTop.get(favIndices.get(j)));
                        IDfav.add(IDPopAndTop.get(favIndices.get(j)));
                        urlFav.add(URLPopAndTop.get(favIndices.get(j)));
                    }

                    ArrayList<Integer> duplicateIndex=findDuplicates(namesfav);

                    if(duplicateIndex.get(0)!=1000){
                        for(int i =0;i<duplicateIndex.size();i++){
                            namesfav.remove(duplicateIndex.get(i));
                            overviewsfav.remove(duplicateIndex.get(i));
                            releasesfav.remove(duplicateIndex.get(i));
                            ratingfav.remove(duplicateIndex.get(i));
                            IDfav.remove(duplicateIndex.get(i));
                            urlFav.remove(duplicateIndex.get(i));
                        }

                    }
                    imageURLS = listToArray(urlFav);


                    Log.d("hwa fe kam name" , namesfav.size()+"");
                    Log.d("hwa fe kam url", imageURLS.length+"");
                    grid.setAdapter(new ImageAdapter(MainActivity.this,imageURLS));
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(!isTablet){
                                Intent i = new Intent(MainActivity.this,DetailActivity.class);
                                i.putExtra("name",namesfav.get(position));
                                i.putExtra("overview", overviewsfav.get(position));
                                i.putExtra("release", releasesfav.get(position));
                                i.putExtra("rate",ratingfav.get(position));
                                i.putExtra("path",urlFav.get(position));
                                i.putExtra("ID",IDfav.get(position));
                                startActivity(i);
                            } else {

                                DetailsFragment fragment = new DetailsFragment();
                                Bundle bundle=new Bundle();
                                bundle.putString("name", namesfav.get(position));
                                bundle.putString("overview", overviewsfav.get(position));
                                bundle.putString("release", releasesfav.get(position));
                                bundle.putString("rate", ratingfav.get(position));
                                bundle.putString("path",urlFav.get(position));
                                bundle.putString("ID", IDfav.get(position));
                                fragment.setArguments(bundle);
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .addToBackStack(null)
                                        .replace(R.id.layout,fragment)
                                        .commit();

                            }
                        }
                    });


                } else {
                    Log.d("size" ,favIndices.size()+"");
                }


            fav=true;
            pop=false;
            top=false;


            return true;
        } else {
            if(id == R.id.action_pop){
                fav=false;
                pop=true;
                top=false;
                new Connect().execute();
                return true;

            } else {
                if(id == R.id.action_top){
                    fav=false;
                    pop=false;
                    top=true;
                    new Connect().execute();
                    return true;


                } else {
                    Log.d("hwa msh shyaf","msh shayhf");
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class Connect extends AsyncTask<Void,Void,Void>{

        public final String ID_JSON = "id";
        public final String TITLE = "title";
        public final String  OVERVIEW = "overview";
        public final String  RELEASEDATE = "release_date";
        public final String POSTERPATH  ="poster_path";
        public final String VOTING = "vote_average";


        private ProgressDialog pDialog;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing()){
                 pDialog.dismiss();
            }
            if(preParam!= null){
                imageURLS = listToArray(preParam);
                grid.setAdapter(new ImageAdapter(MainActivity.this,imageURLS));
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        if(!isTablet){
                            Intent i = new Intent(MainActivity.this,DetailActivity.class);
                            i.putExtra("name",namesPOP.get(position));
                            Log.d("heyha", namesPOP.get(position));
                            i.putExtra("overview", overviewsPOP.get(position));
                            i.putExtra("release", releasesPOP.get(position));
                            i.putExtra("rate",ratingPOP.get(position));
                            i.putExtra("path",preParam.get(position));
                            i.putExtra("ID",ID.get(position));
                            Log.d("path",preParam.get(position));

                            startActivity(i);
                        } else {
                            DetailsFragment fragment = new DetailsFragment();
                            Bundle bundle=new Bundle();
                            bundle.putString("name", namesPOP.get(position));
                            bundle.putString("overview", overviewsPOP.get(position));
                            bundle.putString("release", releasesPOP.get(position));
                            bundle.putString("rate", ratingPOP.get(position));
                            bundle.putString("path", preParam.get(position));
                            bundle.putString("ID", ID.get(position));
                            fragment.setArguments(bundle);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.layout,fragment)
                                    .commit();
                        }


                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),"You have no internet connection",Toast.LENGTH_LONG).show();
            }






        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String movieDB = null;



            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url2 = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7")
                URL url;
                if(pop){
                    String baseUrl = "http://api.themoviedb.org/3/movie/popular/?api_key=";
                    String apiKey = "e6f375a40042abd253b3b3601cf0f895";
                    url = new URL(baseUrl.concat(apiKey));
                } else {

                        String baseUrl = "http://api.themoviedb.org/3/movie/top_rated/?api_key=";
                        String apiKey = "e6f375a40042abd253b3b3601cf0f895";
                        url = new URL(baseUrl.concat(apiKey));


                }


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
                forecastJsonStr = buffer.toString();

                Log.d("json", forecastJsonStr);
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


                if(!preParam.isEmpty()){
                    preParam.clear();
                    overviewsPOP.clear();
                    namesPOP.clear();
                    releasesPOP.clear();
                    ratingPOP.clear();
                    ID.clear();
                }

                JSONObject page = new JSONObject(forecastJsonStr);
                JSONArray result = page.getJSONArray("results");
                for(int i = 0;i<result.length();i++){
                    JSONObject x = result.getJSONObject(i);
                    String description = x.getString(OVERVIEW);
                    String originalTitle =  x.getString(TITLE);
                    String release = x.getString(RELEASEDATE);
                    String id = x.getString(ID_JSON);
                    String voting = x.getString(VOTING);
                    String path = x.getString(POSTERPATH);
                    preParam.add(path);
                    overviewsPOP.add(description);
                    namesPOP.add(originalTitle);
                    releasesPOP.add(release);
                    ID.add(id);
                    ratingPOP.add(voting);
                    if(counter==0){
                        URLPopAndTop.add(path);
                        overviewPopAndTop.add(description);
                        namesPopAndTop.add(originalTitle);
                        releasePopAndTop.add(release);
                        IDPopAndTop.add(id);
                        ratingPopAndTop.add(voting);
                    }

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new TopAndPopOnce().execute();
                    }
                });







            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

    class TopAndPopOnce extends AsyncTask<Void,Void,Void>{
        public final String ID_JSON = "id";
        public final String TITLE = "title";
        public final String  OVERVIEW = "overview";
        public final String  RELEASEDATE = "release_date";
        public final String POSTERPATH  ="poster_path";
        public final String VOTING = "vote_average";

        @Override
        protected Void doInBackground(Void... params) {
            if(counter>0){
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String movieDB = null;



            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url2 = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7")
                    URL url;
                    String baseUrl = "http://api.themoviedb.org/3/movie/top_rated/?api_key=";
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
                forecastJsonStr = buffer.toString();

                Log.d("json", forecastJsonStr);
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




                JSONObject page = new JSONObject(forecastJsonStr);
                JSONArray result = page.getJSONArray("results");
                for(int i = 0;i<result.length();i++){
                    JSONObject x = result.getJSONObject(i);
                    String description = x.getString(OVERVIEW);
                    String originalTitle =  x.getString(TITLE);
                    String release = x.getString(RELEASEDATE);
                    String id = x.getString(ID_JSON);
                    String voting = x.getString(VOTING);
                    String path = x.getString(POSTERPATH);
                    boolean popAndTop=false;

                    for(int j =0;j<namesPopAndTop.size();j++){
                        if(originalTitle.equals(namesPopAndTop.get(j))){
                            popAndTop=true;
                        }
                    }
                    if(!popAndTop){
                        URLPopAndTop.add(path);
                        overviewPopAndTop.add(description);
                        namesPopAndTop.add(originalTitle);
                        releasePopAndTop.add(release);
                        IDPopAndTop.add(id);
                        ratingPopAndTop.add(voting);
                    }



                }
                counter++;






            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}

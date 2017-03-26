package example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView thumb ;
    TextView name , overview,release,rating  ;
    String baseImage = "http://image.tmdb.org/t/p/w185";
    Button btn ,reviews;
    ListView trailers;
    int counter=0;
    public static ArrayList<String> trailersID=new ArrayList<String>();
    public static ArrayList<String> trailerNames = new ArrayList<String>();


    private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        // Inflate the layout for this fragment
        thumb = (ImageView) view.findViewById(R.id.thumb);
        name = (TextView)  view.findViewById(R.id.name);
        overview = (TextView)  view.findViewById(R.id.overview);
        release = (TextView)  view.findViewById(R.id.release);
        rating = (TextView)  view.findViewById(R.id.rating);
        btn = (Button)  view.findViewById(R.id.button);
        trailers = (ListView)  view.findViewById(R.id.trailers);
        reviews = (Button) view.findViewById(R.id.reviews);




        new Trailers().execute();
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ReviewActivity.class);
                i.putExtra("id",getArguments().getString("ID"));
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
                    MainActivity.handler.add(getArguments().getString("name"));
                    counter++;


                } else {
                    btn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    MainActivity.handler.delete(getArguments().getString("name"));
                    counter++;

                }

            }
        });

        Log.d("ana fl fragment",getArguments().getString("name"));
        name.setText(getArguments().getString("name"));
        overview.setText(getArguments().getString("overview"));
        overview.setMovementMethod(new ScrollingMovementMethod());
        Picasso
                .with(getContext())
                .load(baseImage+getArguments().getString("path"))
                .fit() // will explain later
                .into(thumb);

        release.setText("Release Date : " + getArguments().getString("release"));
        rating.setText("Rating : "+getArguments().getString("rate")+" /10");
        return view;



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
            if(cursor.getString(index).equals(getArguments().getString("name"))){
                Log.d("ana gowa","la2eto");
                nameFound=true;
            }


        }
        return nameFound;
    }
    public class Trailers extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            trailers.setAdapter(new TrailerAdapter(getActivity().getApplicationContext(),trailersID,trailerNames));
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
                String baseUrl = "http://api.themoviedb.org/3/movie/"+getArguments().getString("ID")+"/videos?api_key=";
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
                    String trailerName = jsonObj.getString("name");
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

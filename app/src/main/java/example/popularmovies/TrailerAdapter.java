package example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<String>trailerNames = new ArrayList<String>();

    public TrailerAdapter(Context c,ArrayList<String> id ,ArrayList<String> names) {

        super(c, R.layout.movie_item);
        this.id=id;
        this.trailerNames =names;
        mContext = c;
        inflater = LayoutInflater.from(mContext);

    }

    public int getCount() {
        if(DetailActivity.trailersID.size()!=0){
            return DetailActivity.trailersID.size();

        } else {
            return DetailsFragment.trailersID.size();
        }
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.trailer_item, parent, false);

        }
        TextView trailer = (TextView)convertView.findViewById(R.id.trailer);
        trailer.setText(trailerNames.get(position));


        return convertView;
    }


}

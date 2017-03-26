package example.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private String[] x;


    public ImageAdapter(Context c,String[] x) {
        super(c, R.layout.movie_item, x);

        mContext = c;
        this.x = x;
        inflater = LayoutInflater.from( mContext);

    }

    public int getCount() {

        return MainActivity.imageURLS.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.movie_item, parent, false);

        }

        Log.d("hii",MainActivity.imageURLS.length+"");


        Picasso
                .with(mContext)
                .load(MainActivity.imageURLS[position])
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }

    // references to our images
    private Integer[] mThumbIds = {
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7
    };
}
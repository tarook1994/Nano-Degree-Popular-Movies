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

public class ReviewAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    ArrayList<String> author = new ArrayList<String>();
    ArrayList<String> content = new ArrayList<String>();

    public ReviewAdapter(Context c,ArrayList<String> author , ArrayList<String> content ) {

        super(c, R.layout.review_item);
        this.author = author;
        this.content = content;
        mContext = c;
        inflater = LayoutInflater.from(mContext);

    }

    public int getCount() {
        if(ReviewActivity.auther.size()==0){
            return 1;
        }

        return ReviewActivity.auther.size();
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
            convertView = inflater.inflate(R.layout.review_item, parent, false);

        }

        TextView cont = (TextView) convertView.findViewById(R.id.content);
        TextView auth = (TextView) convertView.findViewById(R.id.auther);
        if(author.size()==0){
            auth.setText("NO REVIEWS YET");
        } else {
            cont.setText(content.get(position));
            auth.setText(author.get(position));
        }



        return convertView;
    }


}

package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public ReviewAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent,false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if(holder.tvName == null){
            holder.tvName = view.findViewById(R.id.tvReviewListName);
            holder.tvDate = view.findViewById(R.id.tvReviewListDate);
            holder.Ratingbar = view.findViewById(R.id.tvReviewListRating);
        }

        holder.tvName.setText(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_NAME)));
        holder.tvDate.setText(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_DATE)));
        holder.Ratingbar.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_RATING))));
    }

    static class ViewHolder{
        public ViewHolder(){
            tvName = null;
            tvDate = null;
            Ratingbar = null;

        }

        TextView tvName;
        TextView tvDate;
        RatingBar Ratingbar;

    }
}


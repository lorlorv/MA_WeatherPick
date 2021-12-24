package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BookmarkAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public BookmarkAdapter(Context context, int layout, Cursor c) {
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

        if(holder.tvMarkName == null){
            holder.tvMarkName = view.findViewById(R.id.tvMarkName);
            holder.tvMarkPhone = view.findViewById(R.id.tvMarkPhone);
            holder.tvMarkAddress = view.findViewById(R.id.tvMarkAddress);
        }

        holder.tvMarkName.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_NAME)));
        if(BookmarkDBHelper.COL_PHONE == null){
            holder.tvMarkPhone.setText("전화번호 정보가 없습니다!");
        }
        else
            holder.tvMarkPhone.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE)));
        holder.tvMarkAddress.setText(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ADDRESS)));
    }

    static class ViewHolder{
        public ViewHolder(){
            tvMarkName = null;
            tvMarkPhone = null;
            tvMarkAddress = null;
        }

        TextView tvMarkName;
        TextView tvMarkPhone;
        TextView tvMarkAddress;

    }
}

package com.example.b10709008hw2;

import android.content.Context;
import android.database.Cursor;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.b10709008hw2.data.WaitlistContract;

import static android.content.Context.MODE_PRIVATE;

public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.GuestViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    public GuestListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }


    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.guest_list, parent, false);
        return new GuestViewHolder(view);
    }
    public void onBindViewHolder(GuestViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;
        String name = mCursor.getString(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME));
        int partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE));
        long id = mCursor.getLong(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry._ID));
        holder.nameTextView.setText(name);
        holder.partySizeTextView.setText(String.valueOf(partySize));
        String string=mContext.getSharedPreferences("ChangeColor",MODE_PRIVATE).getString("Color","");
        if(string.equals("blue")) {
            holder.partySizeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle));
        }else if(string.equals("green")){
            holder.partySizeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle2));
        }else{
            holder.partySizeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle1));
        }
        holder.itemView.setTag(id);
    }
    public void updateColor(){
        this.notifyDataSetChanged();
    }

    public int getItemCount() {
        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }
    class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView partySizeTextView;
        public GuestViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            partySizeTextView = (TextView) itemView.findViewById(R.id.party_size_text_view);
        }

    }
}

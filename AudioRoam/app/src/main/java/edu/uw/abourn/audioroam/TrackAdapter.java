package edu.uw.abourn.audioroam;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class TrackAdapter extends ArrayAdapter<Track> {
    ViewHolder holder;


    public TrackAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<Track> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.track_list_item, parent, false);
            holder = new ViewHolder();
            holder.artistTxt = (TextView) convertView.findViewById(R.id.artistName);
            holder.songTxt = (TextView) convertView.findViewById(R.id.songName);
            holder.commentTxt = (TextView) convertView.findViewById(R.id.comment);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.artistTxt.setText(track.artistName);
        holder.songTxt.setText(track.songName);
        holder.commentTxt.setText(track.comment);

        return convertView;
    }

    static class ViewHolder {
        TextView artistTxt;
        TextView songTxt;
        TextView commentTxt;
    }
}

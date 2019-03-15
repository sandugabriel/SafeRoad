package com.example.sandugabriel.saferoad;

import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by hasya on 2016-11-13.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private File[] mDataset;
    private View.OnClickListener mOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public TextView mFileDate;
        public TextView mFilename;
        public ImageView mFileThumbnail;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mFileDate = (TextView) v.findViewById(R.id.file_date);
            mFilename = (TextView) v.findViewById(R.id.file_name);
            mFileThumbnail = (ImageView) v.findViewById(R.id.file_thumb);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FileListAdapter(File[] dataset, View.OnClickListener onClickListener) {
        mDataset = dataset;
        mOnClickListener = onClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        v.setOnClickListener(mOnClickListener);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] parts = mDataset[position].getName().split("\\.");
        String dateString = parts[parts.length - 2];

        try {
            Date date = Session.DATE_FORMATTER.parse(dateString);
            holder.mFileDate.setText(date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mFilename.setText(mDataset[position].getName());

        holder.mFileThumbnail.setImageBitmap(
                ThumbnailUtils.createVideoThumbnail(
                        mDataset[position].getPath(),
                        MediaStore.Images.Thumbnails.MINI_KIND
                )
        );
    }

    @Override
    public int getItemCount() {
       // return mDataset.length;
        return 0;
    }
}

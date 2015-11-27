package com.devmoroz.moneyme.export.dropbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.FileThumbnailRequestHandler;
import com.dropbox.core.v2.DbxFiles;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DropboxFilesAdapter extends RecyclerView.Adapter<DropboxFilesAdapter.MetadataViewHolder>{

    private List<DbxFiles.Metadata> mFiles;
    private LayoutInflater inflater;
    private final Picasso mPicasso;
    private final Callback mCallback;

    public interface Callback {
        void onFolderClicked(DbxFiles.FolderMetadata folder);
        void onFileClicked(DbxFiles.FileMetadata file);
    }

    public void setFiles(List<DbxFiles.Metadata> files) {
        mFiles = files;
        notifyDataSetChanged();
    }

    public DropboxFilesAdapter(Context context,Picasso mPicasso, Callback callback) {
        this.inflater = LayoutInflater.from(context);
        this.mPicasso = mPicasso;
        this.mCallback = callback;
    }

    @Override
    public MetadataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dropbox_files_row,parent,false);
        MetadataViewHolder viewHolder = new MetadataViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MetadataViewHolder holder, int position) {
        holder.bind(mFiles.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mFiles.get(position).pathLower.hashCode();
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public class MetadataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTextView;
        private final ImageView mImageView;
        private DbxFiles.Metadata mItem;

        public MetadataViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.dropbox_image);
            mTextView = (TextView)itemView.findViewById(R.id.dropbox_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItem instanceof DbxFiles.FolderMetadata) {
                mCallback.onFolderClicked((DbxFiles.FolderMetadata) mItem);
            }  else if (mItem instanceof DbxFiles.FileMetadata) {
                mCallback.onFileClicked((DbxFiles.FileMetadata)mItem);
            }
        }

        public void bind(DbxFiles.Metadata item) {
            mItem = item;
            mTextView.setText(mItem.name);

            if (item instanceof DbxFiles.FileMetadata) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext = item.name.substring(item.name.indexOf(".") + 1);
                String type = mime.getMimeTypeFromExtension(ext);
                if (type != null && type.startsWith("image/")) {
                    mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri((DbxFiles.FileMetadata) item))
                            .placeholder(R.drawable.ic_image_black_36dp)
                            .error(R.drawable.ic_image_black_36dp)
                            .into(mImageView);
                } else {
                    mPicasso.load(R.drawable.ic_insert_drive_file_blue_36dp)
                            .noFade()
                            .into(mImageView);
                }
            } else if (item instanceof DbxFiles.FolderMetadata) {
                mPicasso.load(R.drawable.ic_folder_blue_36dp)
                        .noFade()
                        .into(mImageView);
            }
        }
    }

}

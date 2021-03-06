package org.naturenet.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a thumbnail view of images by URI, loaded by Picasso
 */
public class ImageGalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<Uri> mImages;
    private ArrayList<Integer> selectedImages;
    Picasso picasso;

    public ImageGalleryAdapter(Context c, List<Uri> images) {
        mContext = c;
        mImages = images;
        selectedImages = new ArrayList<>();
        picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(false);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check to see if the view is to be recycled
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_gv_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_iv);
        //Check to see if the current view we're trying to load isn't a selected image
        if(!selectedImages.contains(position))
            imageView.setBackgroundResource(0);
        else
            imageView.setBackgroundResource(R.drawable.border_selected_image);

        picasso.load(mImages.get(position))
                .error(R.drawable.no_image)
                .fit()
                .centerCrop()
                .into(imageView);

        return convertView;
    }

    /**
     * This method stores the index of the selected image within this adapter.
     * @param index - the index of the selected image.
     */
    public void addSelectedImage(int index){
        selectedImages.add(index);
    }

    /**
     * This method removes the index of the selected image from the adapter.
     * @param index - the index of the unselected image.
     */
    public void removeSelectedImage(int index){
        selectedImages.remove(Integer.valueOf(index));
        //call notifyDataSetChanged() so the views are refreshed and the de-selected image is reflected on the screen
        notifyDataSetChanged();
    }
}

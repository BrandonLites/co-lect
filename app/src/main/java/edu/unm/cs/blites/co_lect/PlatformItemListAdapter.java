package edu.unm.cs.blites.co_lect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Brandon on 5/4/2015.
 */
public class PlatformItemListAdapter extends RecyclerView.Adapter<PlatformItemListAdapter.myViewHolder> {

    public LayoutInflater inflater;
    List<PlatformItem> platformItems = Collections.emptyList();
    public final ImageLoader imageLoader;

    public PlatformItemListAdapter(Context context, List<PlatformItem> platformItems, ImageLoader imageLoader) {
        inflater = LayoutInflater.from(context);
        this.platformItems = platformItems;
        this.imageLoader = imageLoader;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.platform_list_single, parent, false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        PlatformItem currentItem = platformItems.get(position);

        String platformTitle = currentItem.getPlatformTitle();
        String platformId = currentItem.getPlatformId();
        int numberOfItems = currentItem.getNumOfItems();
        String imageUrl = currentItem.getImageUrl();

        holder.title.setText(platformTitle);

        if (numberOfItems == 1) {
            holder.numberOfItems.setText(numberOfItems + " item");
        }

        else holder.numberOfItems.setText(numberOfItems + " items");

        holder.myImageView.setImageUrl(imageUrl, imageLoader);
    }

    @Override
    public int getItemCount() {
        return platformItems.size();
    }

    /******************************************************************
     *
     */
    class myViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView myImageView;
        TextView title;
        TextView numberOfItems;

        public myViewHolder (View itemView) {
            super(itemView);

            myImageView = (NetworkImageView) itemView.findViewById(R.id.platform_image_frame);
            myImageView.setDefaultImageResId(R.drawable.no_image);
            title = (TextView) itemView.findViewById(R.id.platform_title);
            numberOfItems = (TextView) itemView.findViewById(R.id.platform_number);
        }
    }
}

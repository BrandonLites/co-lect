package edu.unm.cs.blites.co_lect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.myViewHolder> {

    public LayoutInflater inflater;
    List<Item> items = Collections.emptyList();
    public final ImageLoader imageLoader; //TODO: Added back in an imageloader in the constructor

    public ItemListAdapter(Context context, List<Item> items, ImageLoader imageLoader) {
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.imageLoader = imageLoader;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_single, parent, false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Item currentItem = items.get(position);

        String title = currentItem.getItemTitle();
        String platform = currentItem.getItemPlatform();
        String imageUrl = currentItem.getImageUrl();

        holder.title.setText(title);
        holder.platform.setText(platform);
        holder.myImageView.setImageUrl(imageUrl, imageLoader);

        //Log.i("ItemListAdaper", title);
        //NetworkImageView image = currentItem.getItemImage();

       // image.setImageUrl("http://thegamesdb.net/banners/boxart/thumb/original/front/2359" + "-1.jpg", imageLoader);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /******************************************************************
     *
     */
    class myViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView myImageView;
        TextView title;
        TextView platform;

        public myViewHolder (View itemView) {
            super(itemView);


            myImageView = (NetworkImageView) itemView.findViewById(R.id.image_frame);
            myImageView.setDefaultImageResId(R.drawable.nes_controller_small);
            title = (TextView) itemView.findViewById(R.id.title);
            platform = (TextView) itemView.findViewById(R.id.platform);
        }
    }
}

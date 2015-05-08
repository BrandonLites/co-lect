package edu.unm.cs.blites.co_lect;

import android.app.Activity;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ImageLoader mImageLoader;
    private RecyclerView recyclerView;
    private PlatformItemListAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private RequestQueue queue;
    private List<Item> collection;

    private int queueCount = 0;

    private List<PlatformItem> platformCollection;
    private HashSet<String> uniquePlatforms;

    private ProgressBar mProgress;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mProgress = (ProgressBar) findViewById(R.id.progress_spinner);

        queue = MySingleton.getInstance(this.getApplicationContext()).getmRequestQueue();
        collection = new ArrayList<>();
        platformCollection = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        String id = extras.getString("id");

        List<ParseObject> collectionQuery = queryUserCollection(id);

        if(collectionQuery != null) {
            for (ParseObject item : collectionQuery) {
                StringRequest itemRequest = getPayloadRequest(item);
                queueCount++;
                queue.add(itemRequest);
            }
        }

        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mImageLoader = MySingleton.getInstance(getApplicationContext()).getmImageLoader();

        recyclerView = (RecyclerView) findViewById(R.id.collection_list);
        recyclerView.setLayoutManager(layoutManager);



        Toast.makeText(this, "Name = " + name + ", id = " + id, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void printString(String response) {
        Log.i("Testing, ", response);
    }


    /**
     * Takes a given web URL and builds a StringRequest to be
     * added to a RequestQueue.  StringRequest and RequestQueue
     * are part of Android's Volley networking framework to handle
     * networked tasks in a much simpler way.
     *
     * @param url A web URL
     * @return    The StringRequest object to be added to a RequestQueue
     */
    private StringRequest buildUrlRequest(final String url) {

        // Create a ResponseListener
        Response.Listener<String> mResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // TODO : Do something with the actual response
                try {
                    Document xml = loadXmlFromString(response);
                    String title = xml.getElementsByTagName("GameTitle").item(0).getTextContent();
                    String platform = xml.getElementsByTagName("Platform").item(0).getTextContent();
                    int platformId = Integer.parseInt(xml.getElementsByTagName("PlatformId").item(0).getTextContent());

                    NodeList boxArtList = xml.getElementsByTagName("boxart");
                    String imageUrl;

                    if (boxArtList.getLength() > 1) {
                        imageUrl = boxArtList.item(1).getTextContent();
                    }

                    else {imageUrl = boxArtList.item(0).getTextContent(); }

                    Item collectionItem = buildItem(title, platform, platformId, imageUrl);
                    collection.add(collectionItem);
                    queueCount--;


                    //Item platformItem = buildPlatformItem(platformId, platform);
                    //platformCollection.add(platformItem);



                    Log.i("Queue Count =" , "" + queueCount);

                    // TODO : Fix this hack!!! Don't know why 17 is the magic number....
                    if(queueCount == 0) {

                        Log.i("Number of platforms", "" + collection.size());

                        for (Item myItem : collection) {
                            if(myItem.getItemPlatform().equals("NeoGeo")) Log.i("Number of platforms", myItem.getItemTitle());
                        }

                        List<PlatformItem> uniquePlatforms = getUniquePlatformList(collection);
                        //platformCollection = buildPlatformItems(uniquePlatforms);

                        mAdapter = new PlatformItemListAdapter(getApplicationContext(), uniquePlatforms, mImageLoader);
                        recyclerView.setAdapter(mAdapter);

                        mProgress.setVisibility(View.GONE);

                        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {

                               PlatformItem currentItem = mAdapter.platformItems.get(position);

                               List<Item> currentPlatformList = new ArrayList<>();

                                for (Item item : collection) {

                                    Log.i("Filter", "" + collection.size());

                                    if (item.getItemPlatform().equals(currentItem.getPlatformTitle())) {
                                        Log.i("Filter", item.getItemPlatform());
                                        currentPlatformList.add(item);
                                    }
                                }
                                Log.i("Filter", "" + currentPlatformList.size());


                                ItemListAdapter newAdapter = new ItemListAdapter(getApplicationContext(), currentPlatformList, mImageLoader);
                                recyclerView.setAdapter(newAdapter);

                                //Toast.makeText(getApplicationContext(), currentItem.getPlatformTitle(), Toast.LENGTH_LONG).show();
                            }
                        }));
                    }
                    //Log.i("Queue Size - ", "" + queueSet.size());

//                    if(queueSet.isEmpty()) {


//                    }

                } catch (Exception e) {
                    Log.e("ERROR", "Could not load response for " + url + " Error code - " + e);
                    queueCount--;
                }
            }
        };

        // Create an ErrorListener
        Response.ErrorListener mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("ERROR - ", volleyError.toString());
            }
        };

        // Build the request
        StringRequest request = new StringRequest(Request.Method.GET, url, mResponseListener, mErrorListener);
        return request;
    }

    private Document loadXmlFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private List<Item> makeData() {
        List<Item> syntheticItems = new ArrayList<>();

        for (int i = 2359; i < 2470; i ++) {

            NetworkImageView myFrame;

            Item currentItem = new Item();
            currentItem.setItemTitle("Test - " + i);
            currentItem.setItemPlatform("Platform - " + i);
            currentItem.setImageUrl("http://thegamesdb.net/banners/boxart/thumb/original/front/" + i + "-1.jpg");
            syntheticItems.add(currentItem);
        }

        return syntheticItems;
    }

    /**
     *
     * @param username
     * @return
     */
    private List<ParseObject> queryUserCollection(String username) {
        List<ParseObject> collectionItems = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("collection_item");
        query.whereEqualTo("username", username);
        query.addAscendingOrder("product_id");

        try {
            collectionItems = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return collectionItems;
    }

    /**
     *
     * @param item
     */
    private StringRequest getPayloadRequest(ParseObject item) {
        String productId = item.get("product_id").toString();

        StringRequest request = buildUrlRequest("http://thegamesdb.net/api/GetGame.php?id=" + productId);

        return request;
    }

    private Item buildItem(String title, String platform, int platformId, String imageUrl) {
        Item collectionItem = new Item();

        collectionItem.setItemTitle(title);
        collectionItem.setItemPlatform(platform);
        collectionItem.setItemPlatformId(platformId);
        collectionItem.setImageUrl("http://thegamesdb.net/banners/" + imageUrl);

        return collectionItem;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private List<PlatformItem> getUniquePlatformList(List<Item> collectionItems) {

        List<PlatformItem> uniquePlatforms = new ArrayList<>();
        List<String> uniquePlatformStrings = new ArrayList<>();

        int platformSizeCheck = 0;

        for (Item item : collectionItems) {
            PlatformItem newItem = new PlatformItem();

            if(uniquePlatformStrings.contains(item.getItemPlatform())) {

                for (PlatformItem p : uniquePlatforms) {
                    if (p.getPlatformTitle().equals(item.getItemPlatform())) p.setNumOfItems(p.getNumOfItems() + 1);
                }

                continue;
            }

            else {

                newItem.setPlatformId("" + item.getItemPlatformId());
                newItem.setPlatformTitle(item.getItemPlatform());
                newItem.setNumOfItems(1);
                newItem.setImageUrl("http://thegamesdb.net/banners/platform/consoleart/" + item.getItemPlatformId() + ".png");

                uniquePlatformStrings.add(item.getItemPlatform());
                uniquePlatforms.add(newItem);
            }
        }

        Collections.sort(uniquePlatforms, new Comparator<PlatformItem>() {
            @Override
            public int compare(PlatformItem t1, PlatformItem t2) {
                return t1.getPlatformTitle().compareTo(t2.getPlatformTitle());
            }
        });

        return uniquePlatforms;
    }
}

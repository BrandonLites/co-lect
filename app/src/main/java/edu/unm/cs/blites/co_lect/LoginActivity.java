package edu.unm.cs.blites.co_lect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import com.facebook.GraphRequest;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    private final String APP_ID = "FjVl4ZzEsV0gP6QU416sV9FaBlIlKNG2vKUuNX9N";
    private final String CLIENT_KEY = "N2K7lD281zrUmm8sydx595tGmQIqeCqk0AZ8lMz9";

    private String username = null;
    private String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeParse(this, APP_ID, CLIENT_KEY);
        setContentView(R.layout.activity_login);


        String[] perms = {"public_profile", "email", "user_friends"};
        List<String> permissions = Arrays.asList(perms);
        logIntoFacebook(this, permissions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * @param context
     * @param appId
     * @param clientKey
     */
    private void initializeParse(Context context, String appId, String clientKey) {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(context);

        Parse.initialize(context, appId, clientKey);
        ParseFacebookUtils.initialize(getApplicationContext());
    }

    /**
     *
     * @param activity
     * @param permissions
     */
    private void logIntoFacebook(final Activity activity, List<String> permissions) {

        LogInCallback mLoginCallback = new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null)
                    Log.e("LoginActivity", "Something went wrong with the login");

                else if (parseUser.isNew())
                    Log.i("LoginActivity", "User signed up and logged in through Facebook");

                else {
                    Log.d("LoginActivity", "User logged in through Facebook!");
                    fetchUserInfo(activity);
                    activity.finish();
                }
            }
        };

        ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, mLoginCallback);
    }

    /**
     *
     * @param context
     */
    private void fetchUserInfo(Context context) {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if(jsonObject != null) {
                    username = jsonObject.optString("name");
                    id = jsonObject.optString("id");

                    Intent intent = new Intent();
                    Bundle userInfo = new Bundle();
                    userInfo.putString("name", username);
                    userInfo.putString("id", id);
                    intent.putExtras(userInfo);
                    intent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}

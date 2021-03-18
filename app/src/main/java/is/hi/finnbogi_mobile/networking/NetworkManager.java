package is.hi.finnbogi_mobile.networking;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import is.hi.finnbogi_mobile.entities.User;

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static final String BASE_URL = "https://finnbogi-api.herokuapp.com/";

    private static NetworkManager mInstance;
    private static RequestQueue mQueue;
    private Context mContext;

    public static synchronized NetworkManager getInstance(Context context){
        if(mInstance == null) {
            mInstance = new NetworkManager(context);
        }
        return mInstance;
    }

    private NetworkManager(Context context) {
        mContext = context;
        mQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mQueue;
    }

    /**
     *
     * @param callback
     * @param path string of path after BASE_URL, e.g. BASE_URL/users/id -> path = "users/id"
     */
    public void GET(final NetworkCallback<String> callback, String path){
        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(path)
                .build().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error.toString());
            }
        });
        mQueue.add(request);
    }

    public void POST(final NetworkCallback<String> callback, String path, String requestBody){
        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(path)
                .build().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        mQueue.add(request);
    }

    public void loginPost(final NetworkCallback<User> callback, String path, String userName, String password) {
        Log.d(TAG, "inn í login network kalli: ");
        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(path)
                .build().toString();

        Log.d(TAG, "url: " + url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("userName", userName);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, postData.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "gekk upp " + response.toString());
                // TODO: API skilar öllum users þegar það er kallað á þetta url, það væri betra að fá bara userinn sem er verið að logga inn
                Gson gson = new Gson();
                User user = gson.fromJson(String.valueOf(response), User.class);
                callback.onSuccess(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "gekk ekki upp ");
                error.printStackTrace();
                callback.onFailure(error.toString());
            }
        });

        mQueue.add(jsonObjectRequest);
    }

    public void clearCache() {
        mQueue.getCache().clear();
    }

}

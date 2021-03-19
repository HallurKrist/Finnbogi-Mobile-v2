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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
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

    /**
     * Network kall til að reyna að innskrá notanda. Býr til User hlut ef það gekk.
     *
     * @param callback - callback fall
     * @param path - url á endpoint fyrir server kall
     * @param userName - notendanafn þess sem verið er að reyna að innskrá
     * @param password - lykilorð þess sem verið er að reyna að innskrá
     */
    public void loginPost(final NetworkCallback<User> callback, String path, String userName, String password) {
        Log.d(TAG, "inn í login network kalli: ");
        String url = Uri.parse(BASE_URL + "users")
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
                Log.d(TAG, "gekk upp að logga inn " + response.toString());
                // TODO: API skilar öllum users þegar það er kallað á þetta url, það væri betra að fá bara userinn sem er verið að logga inn
                Gson gson = new Gson();
                User user = gson.fromJson(String.valueOf(response), User.class);
                callback.onSuccess(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "gekk ekki upp að logga inn ");
                error.printStackTrace();
                callback.onFailure(error.toString());
            }
        });

        mQueue.add(jsonObjectRequest);
    }

    public void getUsers(final NetworkCallback<List<User>> callback, String path) {
        Log.d(TAG, "inn í getUsers network kalli: ");
        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(path)
                .build().toString();

        Log.d(TAG, "url: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "gekk upp að sækja users " + response.toString());
                Gson gson = new Gson();
                Type listType = new TypeToken<List<User>>(){}.getType();
                List<User> allUsers = gson.fromJson(String.valueOf(response), listType);
                callback.onSuccess(allUsers);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "gekk ekki upp að sækja users ");
                error.printStackTrace();
                callback.onFailure(error.toString());
            }
        });

        mQueue.add(jsonArrayRequest);
    }

    public void createShift(final NetworkCallback<Shift> callback, String path, LocalDateTime startTime, LocalDateTime endTime, int userId) {
        Log.d(TAG, "inn í getUsers network kalli: ");
        String url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(path)
                .build().toString();

        Log.d(TAG, "url: " + url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("startTime", startTime);
            postData.put("endTime", endTime);
            postData.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, postData.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "gekk upp að búa til vakt " + response.toString());
                        Gson gson = new Gson();
                        Shift shiftCreated = gson.fromJson(String.valueOf(response), Shift.class);
                        callback.onSuccess(shiftCreated);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "gekk ekki upp að búa til vakt ");
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

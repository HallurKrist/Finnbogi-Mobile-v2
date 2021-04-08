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
import com.android.volley.toolbox.HttpClientStack;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Fall sem gerir Get request á path og skilar streng response
     *
     * @param callback callback sem skilar response
     * @param path String[] {path1, path2} = BASE_URL/path1/path2
     */
    public void GET(final NetworkCallback<String> callback, String[] path) {

        // Make correct path
        Uri.Builder urlBuilder = Uri.parse(BASE_URL)
                .buildUpon();
        for (int i = 0; i < path.length; i++) {
            urlBuilder.appendPath(path[i]);
        }
        String url = urlBuilder.build().toString();

        // Send get request and handle response
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

    /**
     * Fall sem tekur gerir post request á path með request body og skilar json Streng við response
     *
     * @param callback callback sem skilar response
     * @param path String[] {path1, path2} = /path1/path2
     * @param requestBody String[][] {{key1 , value1}, {key2, value2}} = key1=value1&key2=value2
     */
    public void POST(final NetworkCallback<String> callback, String[] path, String[][] requestBody) {

        // Make path
        Uri.Builder urlBuilder = Uri.parse(BASE_URL)
                .buildUpon();
        for (int i = 0; i < path.length; i++) {
            urlBuilder.appendPath(path[i]);
        }
        String url = urlBuilder.build().toString();

        // Make correct request body ( key1=value1&key2=value2 ... )
        String requestBodyString = null;
        if (requestBody.length != 0) {
            requestBodyString = "";
            for (int i = 0; i < requestBody.length; i++) {
                if (i == 0) {
                    requestBodyString = requestBodyString + requestBody[i][0] + "=" + requestBody[i][1];
                } else {
                    requestBodyString = requestBodyString + "&" + requestBody[i][0] + "=" + requestBody[i][1];
                }
            }
        }
        String finalRequestBodyString = requestBodyString;
        Log.d(TAG, "request body string: " + finalRequestBodyString);

        // Make post request and send String response back
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);;
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
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return finalRequestBodyString == null ? null : finalRequestBodyString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalRequestBodyString, "utf-8");
                    return null;
                }
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    try {
                        responseString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        mQueue.add(request);
    }

    public void PATCH(final NetworkCallback<String> callback, String[] path, String[][] requestBody){

        // Make path
        Uri.Builder urlBuilder = Uri.parse(BASE_URL)
                .buildUpon();
        for (int i = 0; i < path.length; i++) {
            urlBuilder.appendPath(path[i]);
        }
        String url = urlBuilder.build().toString();

        // Make correct request body ( key1=value1&key2=value2 ... )
        String requestBodyString = null;
        if (requestBody.length != 0) {
            requestBodyString = "";
            for (int i = 0; i < requestBody.length; i++) {
                if (i == 0) {
                    requestBodyString = requestBodyString + requestBody[i][0] + "=" + requestBody[i][1];
                } else {
                    requestBodyString = requestBodyString + "&" + requestBody[i][0] + "=" + requestBody[i][1];
                }
            }
        }
        String finalRequestBodyString = requestBodyString;
        Log.d(TAG, "request body string: " + finalRequestBodyString);

        // Make patch request and send String response back
        StringRequest request = new StringRequest(
                Request.Method.PATCH, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);;
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
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return finalRequestBodyString == null ? null : finalRequestBodyString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalRequestBodyString, "utf-8");
                    return null;
                }
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    try {
                        responseString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }


        };
        mQueue.add(request);
    }

    public void DELETE(final NetworkCallback<String> callback, String[] path, String[][] requestBody){

        // Make path
        Uri.Builder urlBuilder = Uri.parse(BASE_URL)
                .buildUpon();
        for (int i = 0; i < path.length; i++) {
            urlBuilder.appendPath(path[i]);
        }
        String url = urlBuilder.build().toString();

        // Make correct request body ( key1=value1&key2=value2 ... )
        String requestBodyString = null;
        if (requestBody.length != 0) {
            requestBodyString = "";
            for (int i = 0; i < requestBody.length; i++) {
                if (i == 0) {
                    requestBodyString = requestBodyString + requestBody[i][0] + "=" + requestBody[i][1];
                } else {
                    requestBodyString = requestBodyString + "&" + requestBody[i][0] + "=" + requestBody[i][1];
                }
            }
        }
        String finalRequestBodyString = requestBodyString;
        Log.d(TAG, "request body string: " + finalRequestBodyString);

        // Make patch request and send String response back
        StringRequest request = new StringRequest(
                Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);;
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
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return finalRequestBodyString == null ? null : finalRequestBodyString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalRequestBodyString, "utf-8");
                    return null;
                }
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    try {
                        responseString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }


        };
        mQueue.add(request);
    }

}

package org.bsc.com.india.Interactor;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import org.bsc.com.india.util.AppController;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by sanjit on 26/12/17.
 */

public class InteractorImp implements org.bsc.com.india.Interactor.Interactor {
    @Override
    public void getData(final org.bsc.com.india.Interactor.Interactor.DataRetriveListener listener, String url) {

            StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response.toString());
                    listener.onSuccess(response.toString());

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    listener.onFaliure();

                }
            });
//            {
//                @Override
//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded; charset=UTF-8";
//                }
//
//            }


            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq,"string");

            // Cancelling request
            // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
        }

    }






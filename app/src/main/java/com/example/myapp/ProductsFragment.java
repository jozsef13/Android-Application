package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ProductsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        ListView mListView = (ListView) view.findViewById(R.id.listView);
        ArrayList<Product> products = new ArrayList<>();
        String api = "https://18.196.182.237/wp-json/wc/v3/products?consumer_key=ck_11bcb5451d6eff1a3090798d1196abd637189497&consumer_secret=cs_233e6be63236691fccbc4c2d61ae7546e6d2cd22&per_page=100";
        ProgressDialog pDialog;

        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Please wait while retrieving the data!");
        pDialog.setCancelable(false);
        pDialog.show();
        handleSSLHandshake();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, api, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0, size = response.length(); i < size; i++) {
                        JSONObject objectInArray = response.getJSONObject(i);
                        if (!objectInArray.getString("catalog_visibility").equals("hidden")) {
                            JSONObject imageObj = (JSONObject) objectInArray.getJSONArray("images").get(0);
                            JSONObject categoryObj = (JSONObject) objectInArray.getJSONArray("categories").get(0);
                            String description = objectInArray.getString("short_description").replaceAll("<p>", "").replaceAll("</p>", "")
                                    .replaceAll("<strong>", "").replaceAll("</strong>", "").replaceAll("\n", "").replaceAll("<p.*>", "");
                            Product product = new Product(objectInArray.getString("name"), imageObj.getString("src"),
                                    objectInArray.getString("price") + "$", "Category: " + categoryObj.getString("name"), description);
                            products.add(product);
                        }
                    }
                    ProductListAdapter adapter = new ProductListAdapter(getContext(), R.layout.adapter_view_layout, products);
                    mListView.setAdapter(adapter);
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error try again", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                System.out.println(error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Error while loading...", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });

        AppController.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        return view;
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}

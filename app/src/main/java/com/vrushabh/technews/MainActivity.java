package com.vrushabh.technews;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private DrawerLayout mDrawerLayout;
    public NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    String API_KEY = "4975058b5bb54c5d977fb57515cb8b0b"; // ### YOUE NEWS API HERE ###
    String NEWS_SOURCE = "Techcrunch";
    ListView listNews;
    ProgressBar loader;
    ImageView iv;
    TextView textView;
    private static int SPLASH_TIME_OUT =3000;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-6250812386412009~3341167800");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-6250812386412009/5201044387");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.open, R.string.close);
        navigationView =(NavigationView)findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.rateus) {
                    open();
                }
                if (id == R.id.technews) {
                    Toast.makeText(getApplicationContext(), "Hi welcome to the TechNews App", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        listNews = (ListView) findViewById(R.id.listNews);
        loader = (ProgressBar) findViewById(R.id.loader);
        listNews.setEmptyView(loader);
        textView = (TextView) findViewById(R.id.txt);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadNews newsTask = new DownloadNews();
            newsTask.execute();
        } else {

            textView.setVisibility(textView.VISIBLE);
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            if (Function.isNetworkAvailable(getApplicationContext())) {
                DownloadNews newsTask = new DownloadNews();
                newsTask.execute();
            }
        }

    }




    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            xml = Function.excuteGet("https://newsapi.org/v1/articles?source=" + NEWS_SOURCE + "&sortBy=top&apiKey=" + API_KEY, urlParameters);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (xml.length() > 10) { // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }

                ListNewsAdapter adapter = new ListNewsAdapter(MainActivity.this, dataList);
                listNews.setAdapter(adapter);

                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                        i.putExtra("url", dataList.get(+position).get(KEY_URL));
                        startActivity(i);
                    }
                });

            } else {

                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))

            return true;
        return super.onOptionsItemSelected(item);
    }

    public void open(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vrushabh.technews"));
        startActivity(browserIntent);
    }

}

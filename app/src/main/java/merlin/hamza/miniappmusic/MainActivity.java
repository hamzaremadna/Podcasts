package merlin.hamza.miniappmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnPodcastItemClickListener{

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LAST_FETCH_TIME = "lastFetchTime";
    private static final String KEY_JSON_DATA = "jsonData";

    private RecyclerView recyclerView;
    private PodcastAdapter adapter;
    private ProgressBar progressBar;

    private ArrayList<PodcastData> podcastList;
    private ArrayList<PodcastData> filteredPodcastList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.podcast_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        podcastList = new ArrayList<PodcastData>();
        filteredPodcastList = new ArrayList<PodcastData>();

        adapter = new PodcastAdapter(filteredPodcastList, this,this);

        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastFetchTime = prefs.getLong(KEY_LAST_FETCH_TIME, 0);
        String jsonData = prefs.getString(KEY_JSON_DATA, "");

        if (System.currentTimeMillis() - lastFetchTime < 24 * 60 * 60 * 1000) {
            parseJsonData(jsonData);
        } else {
            fetchJsonData();
        }

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search podcasts...");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredPodcastList.clear();
                for (PodcastData podcast : podcastList) {
                    if (podcast.getTitle().toLowerCase().contains(newText.toLowerCase().trim())) {
                        filteredPodcastList.add(podcast);
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }


    private void fetchJsonData() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://itunes.apple.com/us/rss/toppodcasts/limit=100/genre=1310/json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONObject feed = response.getJSONObject("feed");
                        JSONArray entries = feed.getJSONArray("entry");

                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            jsonArray.put(entry);
                        }

                        String jsonData = jsonArray.toString();

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putLong(KEY_LAST_FETCH_TIME, System.currentTimeMillis());
                        editor.putString(KEY_JSON_DATA, jsonData);
                        editor.apply();

                        parseJsonData(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                });

        queue.add(jsonObjectRequest);
    }


    private void parseJsonData(String jsonData) {
        try {
            JSONArray entries = new JSONArray(jsonData);

            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);

                String title = entry.getJSONObject("im:name").getString("label");
                String imageUrl = entry.getJSONArray("im:image").getJSONObject(2).getString("label");
                String author = entry.getJSONObject("im:artist").getString("label");
                String description = entry.getJSONObject("summary").getString("label");
                String id =  entry.getJSONObject("id").getString("label");

                PodcastData podcast = new PodcastData(title, imageUrl, author, description,id);
                podcastList.add(podcast);
                filteredPodcastList.add(podcast);
            }

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(PodcastData podcast) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            Intent intent = new Intent(this, DetallesActivity.class);
            intent.putExtra(DetallesActivity.EXTRA_TITLE, podcast.getTitle());
            intent.putExtra(DetallesActivity.EXTRA_IMAGE_URL, podcast.getImageUrl());
            intent.putExtra(DetallesActivity.EXTRA_AUTHOR, podcast.getAuthor());
            intent.putExtra(DetallesActivity.EXTRA_DESCRIPTION, podcast.getDescription());
            intent.putExtra(DetallesActivity.EXTRA_ID, podcast.getId());

            startActivity(intent);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
            });
        }).start();

    }

}





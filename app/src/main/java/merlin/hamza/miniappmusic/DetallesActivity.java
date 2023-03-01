package merlin.hamza.miniappmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class DetallesActivity extends AppCompatActivity implements OnEpisodeitemClickListener{

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_AUTHOR = "extra_author";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_ID = "extra_id";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LAST_FETCH_TIME = "lastEpisodeTime";
    private static final String KEY_JSON_DATA = "jsonDataEpisode";

    private List<PodcastEpisode> episodeList = new ArrayList<>();

    private RecyclerView recyclerView;
    private EpisodeAdapter adapter;

    private TextView titleTextView;
    private ImageView imageView;
    private TextView authorTextView;
    private TextView descriptionTextView;
    private String imageUrl;
    private String author;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        titleTextView = findViewById(R.id.title_text_view);
        imageView = findViewById(R.id.image_view);
        authorTextView = findViewById(R.id.author_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
         imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
         author = intent.getStringExtra(EXTRA_AUTHOR);
         description = intent.getStringExtra(EXTRA_DESCRIPTION);
        String id = intent.getStringExtra(EXTRA_ID);

        titleTextView.setText(title);
        Picasso.get().load(imageUrl).into(imageView);
        authorTextView.setText(author);
        descriptionTextView.setText(description);

        recyclerView = findViewById(R.id.episode_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EpisodeAdapter(episodeList,this);
        recyclerView.setAdapter(adapter);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastFetchTime = prefs.getLong(KEY_LAST_FETCH_TIME, 0);
        String jsonData = prefs.getString(KEY_JSON_DATA, "");
        if (System.currentTimeMillis() - lastFetchTime < 24 * 60 * 60 * 1000) {
            parseJsonData(jsonData);
        } else {
            fetchEpisodes(extractPodcastId(id));
        }

    }

    private void fetchEpisodes(String podcastId) {
        String url = "https://itunes.apple.com/lookup?id="+podcastId+"&entity=podcastEpisode&limit=9";
        AsyncTask.execute(() -> {
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject episodeObj = jsonArray.getJSONObject(i);
                        String title = episodeObj.getString("trackName");
                        String releaseDate = episodeObj.getString("releaseDate");
                        String Urlpod = episodeObj.getString("episodeUrl");

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
                        try {
                            Date date = inputFormat.parse(releaseDate);
                            releaseDate = outputFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String duration = episodeObj.getString("trackTimeMillis");
                        String dur = duration;
                        duration = String.format(Locale.getDefault(), "%d H %02d MN",
                                TimeUnit.MILLISECONDS.toHours(Long.parseLong(duration)),
                                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration)) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Long.parseLong(duration))));

                        PodcastEpisode episode = new PodcastEpisode(title, releaseDate, duration,Urlpod,dur);
                        episodeList.add(episode);
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    Log.e("DetallesActivity", "HTTP connection error: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void parseJsonData(String jsonData) {
        try {
            JSONArray entries = new JSONArray(jsonData);

            for (int i = 1; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                JSONObject episodeObj = entries.getJSONObject(i);

                String title = episodeObj.getString("trackName");
                String releaseDate = episodeObj.getString("releaseDate");
                String Urlpod = episodeObj.getString("episodeUrl");

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");

                try {
                    Date date = inputFormat.parse(releaseDate);
                    releaseDate = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String duration = episodeObj.getString("trackTimeMillis");
                String dur = duration;
                duration = String.format(Locale.getDefault(), "%d H %02d MN",
                        TimeUnit.MILLISECONDS.toHours(Long.parseLong(duration)),
                        TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration)) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Long.parseLong(duration))));

                PodcastEpisode episode = new PodcastEpisode(title, releaseDate, duration,Urlpod,dur);
                episodeList.add(episode);
            }

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String extractPodcastId(String url) {

        Pattern pattern = Pattern.compile("id(\\d+)\\?");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String id = matcher.group(1);
            url = id;
        }

        return url;
    }


    @Override
    public void onItemClick(PodcastEpisode episode) {
        Intent intent = new Intent(this, DetallesCapituloActivity.class);

        intent.putExtra(DetallesCapituloActivity.EXTRA_TITLE, episode.getTitle());
        intent.putExtra(DetallesCapituloActivity.EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(DetallesCapituloActivity.EXTRA_DESCRIPTION, description);
        intent.putExtra(DetallesCapituloActivity.EXTRA_AUTHOR, author);
        intent.putExtra(DetallesCapituloActivity.EXTRA_PODCAST_URL, episode.getPubUrl());

        intent.putExtra(DetallesCapituloActivity.EXTRA_RELEASE_DATE, episode.getPubDate());
        intent.putExtra(DetallesCapituloActivity.EXTRA_DURATION, episode.getDur());
        startActivity(intent);
    }
}
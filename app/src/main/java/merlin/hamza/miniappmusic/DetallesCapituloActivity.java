package merlin.hamza.miniappmusic;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

public class DetallesCapituloActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTextView, authorTextView, descriptionTextView,durationTextView,progressTextView;
    private Button playButton,previousButton,nextButton;
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private boolean isMediaPlayerPrepared = false;

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private boolean isPrepared = false;
    private String podcastUrl;

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_AUTHOR = "extra_author";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_DURATION = "extra_duration";
    public static final String EXTRA_RELEASE_DATE = "extra_releaseDate";
    public static final String EXTRA_PODCAST_URL = "extra_podcast_url";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_capitulo);

        imageView = findViewById(R.id.podcast_image);
        titleTextView = findViewById(R.id.podcast_title);
        authorTextView = findViewById(R.id.podcast_author);
        descriptionTextView = findViewById(R.id.podcast_description);


        durationTextView = findViewById(R.id.track_duration_textview);
        progressTextView = findViewById(R.id.time_progression_textview);
        playButton = findViewById(R.id.player_button);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        seekBar = findViewById(R.id.seek_bar);
        progressBar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        String author = intent.getStringExtra(EXTRA_AUTHOR);
        String duration = intent.getStringExtra(EXTRA_DURATION);

        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        podcastUrl = intent.getStringExtra(EXTRA_PODCAST_URL);
        initializeMediaPlayer();
        titleTextView.setText(title);
        durationTextView.setText(duration);
        Picasso.get().load(imageUrl).into(imageView);
        authorTextView.setText(author);
        descriptionTextView.setText(description);

        titleTextView.setOnClickListener(v -> {
            finish();
        });

        imageView.setOnClickListener(v -> {
            finish();
        });
        initializeMediaPlayer();
    }


    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        seekBar.setMax(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setBackgroundResource(R.drawable.ic_play);
                } else {
                    progressBar.setVisibility(v.VISIBLE);
                    if (isPrepared) {
                        progressBar.setVisibility(v.GONE);
                        mediaPlayer.start();
                        playButton.setBackgroundResource(R.drawable.ic_pause);
                        updateSeekBar();
                    } else {
                        try {
                            mediaPlayer.setDataSource(podcastUrl);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    progressBar.setVisibility(v.GONE);
                                    isPrepared = true;
                                    mp.start();
                                    playButton.setBackgroundResource(R.drawable.ic_pause);
                                    seekBar.setMax(mediaPlayer.getDuration());
                                    updateSeekBar();
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    isPrepared = false;
                                    return false;
                                }
                            });
                            mediaPlayer.prepareAsync();
                            playButton.setBackgroundResource(R.drawable.ic_pause);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

}
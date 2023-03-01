package merlin.hamza.miniappmusic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private OnEpisodeitemClickListener listener;

    private List<PodcastEpisode> episodeList;

    public EpisodeAdapter(List<PodcastEpisode> episodeList,OnEpisodeitemClickListener listener) {
        this.episodeList = episodeList;
        this.listener = listener;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PodcastEpisode episode = episodeList.get(position);
        holder.titleTextView.setText(episode.getTitle());
        holder.dateTextView.setText(episode.getPubDate());
        holder.durationTextView.setText(episode.getDuration());
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        public TextView dateTextView;
        public TextView durationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            durationTextView = itemView.findViewById(R.id.duration_text_view);
               itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                PodcastEpisode podcast = episodeList.get(position);
                listener.onItemClick(podcast);
            }
        }
    }
}
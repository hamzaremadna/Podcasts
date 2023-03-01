package merlin.hamza.miniappmusic;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.ViewHolder> implements Filterable {

    private OnPodcastItemClickListener listener;

    private ArrayList<PodcastData> podcastList;
    private ArrayList<PodcastData> filteredList;
    private Context context;

    public PodcastAdapter(ArrayList<PodcastData> podcastList, Context context,OnPodcastItemClickListener listener) {
        this.podcastList = podcastList;
        this.filteredList = podcastList;
        this.context = context;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PodcastData podcast = podcastList.get(position);

        holder.titleTextView.setText(podcast.getTitle());
        holder.authorTextView.setText(podcast.getAuthor());
        holder.descriptionTextView.setText(podcast.getDescription());

        Picasso.get()
                .load(podcast.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (filteredList == null) {
            return 0;
        } else {
            return filteredList.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();

                ArrayList<PodcastData> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = podcastList;
                } else {
                    for (PodcastData podcast : podcastList) {
                        if (podcast.getTitle().toLowerCase().contains(query.toLowerCase()) || (podcast.getAuthor().toLowerCase().contains(query.toLowerCase()))){
                            filtered.add(podcast);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((ArrayList<PodcastData>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView authorTextView;
        public TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                PodcastData podcast = podcastList.get(position);
                listener.onItemClick(podcast);
            }
        }
    }
}
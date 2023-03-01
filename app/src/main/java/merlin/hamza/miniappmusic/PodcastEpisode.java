package merlin.hamza.miniappmusic;

public class PodcastEpisode {
    private String title;
    private String pubDate;
    private String duration;
    private String pubUrl;
    private String dur;



    public PodcastEpisode(String title, String pubDate, String duration,String pubUrl,String dur) {
        this.title = title;
        this.pubDate = pubDate;
        this.duration = duration;
        this.pubUrl = pubUrl;
        this.dur = dur;

    }

    public String getTitle() {
        return title;
    }
    public String getPubUrl() {
        return pubUrl;
    }
    public String getDur(){return dur; }

    public void setDur(String dur) {
        this.dur = dur;
    }

    public void setPubUrl(String pubUrl) {
        this.pubUrl = pubUrl;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDuration() {
        return duration;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
package merlin.hamza.miniappmusic;


public class PodcastData{

    private String title;
    private String imageUrl;
    private String author;
    private String description;
    private String id;

    public PodcastData(String title, String imageUrl, String author, String description,String id) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
        this.id = id;
    }

        public String getTitle() {
            return title;
        }
    public String getId() {
        return id;
    }
    public void setId(String id){this.id = id;}

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

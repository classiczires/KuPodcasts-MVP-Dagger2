package ke.topcast.model.data;

public class Podcast {
    private String title = "";
    private String description = "";
    private String duration = "";
    private String programBuilder = "";
    private String narrators = "";
    private String sku = "";
    private String imageUrl = "";
    private String podcastUrl = "";

    public Podcast(String title, String description, String imageUrl, String podcastUrl, String sku, String duration, String programBuilder, String narrators) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.podcastUrl = podcastUrl;
        this.sku = sku;
        this.duration = duration;
        this.programBuilder = programBuilder;
        this.narrators = narrators;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getPodcastUrl() {
        return podcastUrl;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProgramBuilder() {
        return programBuilder;
    }

    public void setProgramBuilder(String programBuilder) {
        this.programBuilder = programBuilder;
    }

    public String getNarrators() {
        return narrators;
    }

    public void setNarrators(String narrators) {
        this.narrators = narrators;
    }
}

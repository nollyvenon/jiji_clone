package data;

public class Finds {

    private String id;
    private String price;
    private String description;
    private String finderName;
    private String category;
    private String title;
    private String benefit;
    private String timeLeft;
    private String status;
    private String createdAt;
    private String auth;
    private String bidEnd;

    public Finds(String id, String price, String description, String title,
                 String category, String benefit, String createdAt, String bidEnd, String auth) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.category = category;
        this.title = title;
        this.benefit = benefit;
        this.createdAt = createdAt;
        this.auth = auth;
        this.bidEnd = bidEnd;
    }

    public String getBidEnd() {
        return bidEnd;
    }

    public String getAuth() {
        return auth;
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getFinderName() {
        return finderName;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getBenefit() {
        return benefit;
    }

    public String getTimeLeft() {
        return timeLeft;
    }
}
package data;

public class Review {

    private String jobTitle;
    private String price;
    private String rating;
    private String clientName;
    private int clientImage;
    private String date;
    private String description;

    public Review(String jobTitle, String price, String rating, String clientName, int clientImage, String date, String description) {
        this.jobTitle = jobTitle;
        this.price = price;
        this.rating = rating;
        this.clientName = clientName;
        this.clientImage = clientImage;
        this.date = date;
        this.description = description;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getPrice() {
        return price;
    }

    public float getRating() {
        return Float.parseFloat(rating);
    }

    public String getClientName() {
        return clientName;
    }

    public int getClientImage() {
        return clientImage;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

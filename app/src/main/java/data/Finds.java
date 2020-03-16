package data;

public class Finds {

    private String price;
    private String description;
    private String finderName;
    private String category;
    private String title;
    private String promotion;
    private String timeLeft;

    public Finds(String price, String description, String finderName, String category, String title, String promotion, String timeLeft) {
        this.price = price;
        this.description = description;
        this.finderName = finderName;
        this.category = category;
        this.title = title;
        this.promotion = promotion;
        this.timeLeft = timeLeft;
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

    public String getPromotion() {
        return promotion;
    }

    public String getTimeLeft() {
        return timeLeft;
    }
}
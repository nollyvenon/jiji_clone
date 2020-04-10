package data;

public class SearchData {

    private String id;
    private String findId;
    private String adId;
    private String image;
    private String title;

    private String auth;
    private String type;
    private String price;
    private String description;
    private String finderName;
    private String category;
    private String benefit;
    private String timeLeft;
    private String status;
    private String createdAt;

    public String getBidEnd() {
        return bidEnd;
    }

    public void setBidEnd(String bidEnd) {
        this.bidEnd = bidEnd;
    }

    private String bidEnd;

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getAuth() {
        return auth;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFindId(String findId) {
        this.findId = findId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getFindId() {
        return findId;
    }

    public String getAdId() {
        return adId;
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
package data;

public class Ads {

    private String description;
    private String companyName;
    private String location;
    private String rating;
    private String review;
    private int profileImage;
    private String title;
    private String price;
    private String benefit;
    private String views;
    private String likes;

    public Ads(String description, String title, String price, String views, String likes) {
        this.description = description;
        this.title = title;
        this.price = price;
        this.views = views;
        this.likes = likes;
    }

    public Ads(String companyName, String rating, String review, int profileImage) {
        this.companyName = companyName;
        this.rating = rating;
        this.review = review;
        this.profileImage = profileImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getRating() {
        return Float.parseFloat(rating);
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return "N" + price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
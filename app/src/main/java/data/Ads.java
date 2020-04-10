package data;

public class Ads {

    private String id;
    private String findId;
    private String adId;
    private String description;
    private String companyName;
    private String location;
    private String rating;
    private String review;
    private String image;
    private String title;
    private String price;
    private String benefit;
    private String views;
    private String likes;
    private String liked;
    private String category;
    private String status;
    private String createdAt;
    private String auth;
    private String businessName;

    public Ads(String description, String title, String price, String views, String likes, String id) {
        this.description = description;
        this.title = title;
        this.price = price;
        this.views = views;
        this.likes = likes;
        this.id = id;
    }

    public Ads(String businessName, String rating, String review, String image, String id) {
        this.businessName = businessName;
        this.rating = rating;
        this.review = review;
        this.image = image;
        this.id = id;
    }


    public String getFindId() {
        return findId;
    }

    public String getAdId() {
        return adId;
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

    public String getDescription() {
        return description;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getStatus() {
        return status;
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

    public String getRating() {
        return rating;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
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

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }
}
package data;

public class Proposal {

    private int profileImage;
    private String username;
    private String rating;
    private String reviewCount;
    private String description;
    private String benefit;

    public Proposal(int profileImage, String username, String rating, String reviewCount, String description, String benefit) {
        this.profileImage = profileImage;
        this.username = username;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.description = description;
        this.benefit = benefit;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public String getUsername() {
        return username;
    }

    public float getRating() {
        return Float.parseFloat(rating);
    }

    public String getReviewCount() {
        return "(" + reviewCount + " reviews)";
    }

    public String getDescription() {
        return description;
    }

    public String getBenefit() {
        return benefit;
    }
}

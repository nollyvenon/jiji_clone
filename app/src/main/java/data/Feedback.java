package data;

public class Feedback {

    private String username;
    private String rating;
    private String feedback;
    private String createdAt;
    private String userId;
    private String adId;
    private String status;
    private String auth;
    private String title;
    private String profileImage;
    private String finderId;

    public Feedback(String username, String rating, String feedback, String createdAt, String userId,
                    String adId, String status, String auth, String title, String profileImage, String finderId) {
        this.username = username;
        this.rating = rating;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.userId = userId;
        this.adId = adId;
        this.status = status;
        this.auth = auth;
        this.title = title;
        this.profileImage = profileImage;
        this.finderId = finderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFinderId() {
        return finderId;
    }

    public void setFinderId(String finderId) {
        this.finderId = finderId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }
}

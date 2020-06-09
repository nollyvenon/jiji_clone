package data;

public class Proposal {

    private String profileImage;
    private String businessName;
    private String rating;
    private String reviewCount;
    private String description;
    private String benefit;
    private String findId;
    private String finderId;
    private String adId;
    private String id;
    private Boolean status;
    private String auth;
    private String awardedId;
    private String fFinderId;

    public Proposal(String profileImage, String businessName, String rating, String reviewCount,
                    String description, String benefit, String id, String findId, String adId,
                    String auth, String finderId, String awardedId, String fFinderId) {
        this.profileImage = profileImage;
        this.businessName = businessName;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.description = description;
        this.benefit = benefit;
        this.id = id;
        this.findId = findId;
        this.finderId = finderId;
        this.adId = adId;
        this.auth = auth;
        this.awardedId = awardedId;
        this.fFinderId = fFinderId;
    }

    public String getFindId() {
        return findId;
    }

    public String getFinderId() {
        return finderId;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getAuth() {
        return auth;
    }

    public String getAdId() {
        return adId;
    }

    public String getId() {
        return id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public String getDescription() {
        return description;
    }

    public String getBenefit() {
        return benefit;
    }

    public String getAwardedId() {
        return awardedId;
    }

    public void setAwardedId(String awardedId) {
        this.awardedId = awardedId;
    }

    public String getfFinderId() {
        return fFinderId;
    }

    public void setfFinderId(String fFinderId) {
        this.fFinderId = fFinderId;
    }
}

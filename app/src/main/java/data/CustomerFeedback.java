package data;

public class CustomerFeedback {

    String username;
    String rating;
    String feedback;
    String date;

    public CustomerFeedback(String username, String rating, String feedback, String date) {
        this.username = username;
        this.rating = rating;
        this.feedback = feedback;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getRating() {
        return rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getDate() {
        return date;
    }

}

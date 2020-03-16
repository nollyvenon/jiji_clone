package data;

public class Messages {

    private String sender;
    private String date;
    private String messageID;
    private String content;

    public Messages(String sender, String date, String messageID, String content) {
        this.sender = sender;
        this.date = date;
        this.messageID = messageID;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getContent() {
        return content;
    }

}

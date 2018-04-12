package team15.capstone2.pbs.models;

public class NotificationModel {
    private int notification_id;
    private String date;
    private String text;
    private int client_id;

    public NotificationModel() {
    }

    public NotificationModel(int notification_id, String time, String text, int client_id) {

        this.notification_id = notification_id;
        this.date = time;
        this.text = text;
        this.client_id = client_id;
    }

    public int getNotification_id() {

        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getTime() {
        return date;
    }

    public void setTime(String time) {
        this.date = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}

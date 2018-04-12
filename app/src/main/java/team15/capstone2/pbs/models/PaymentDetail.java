package team15.capstone2.pbs.models;

public class PaymentDetail {
    private int paymentDetails_id;
    private String timestamp;
    private float changed_amount;
    private int payment_id;
    private int booking_detail_id;

    public PaymentDetail() {
    }

    public PaymentDetail(int paymentDetails_id, String timestamp, float changed_amount, int payment_id, int booking_detail_id) {

        this.paymentDetails_id = paymentDetails_id;
        this.timestamp = timestamp;
        this.changed_amount = changed_amount;
        this.payment_id = payment_id;
        this.booking_detail_id = booking_detail_id;
    }

    public int getPaymentDetails_id() {

        return paymentDetails_id;
    }

    public void setPaymentDetails_id(int paymentDetails_id) {
        this.paymentDetails_id = paymentDetails_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getChanged_amount() {
        return changed_amount;
    }

    public void setChanged_amount(float changed_amount) {
        this.changed_amount = changed_amount;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public int getBooking_detail_id() {
        return booking_detail_id;
    }

    public void setBooking_detail_id(int booking_detail_id) {
        this.booking_detail_id = booking_detail_id;
    }
}

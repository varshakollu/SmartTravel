package com.example.varsha.smarttravel;

/**
 * Created by Varsha on 4/26/2017.
 */

public class Coupon {
    private String CouponID;
    private String Date;
    private String Title;
    private String Status;
    private String RedemptionLocation;

    public String getCouponID() {
        return CouponID;
    }

    public void setCouponID(String couponID) {
        CouponID = couponID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRedemptionLocation() {
        return RedemptionLocation;
    }

    public void setRedemptionLocation(String redemptionLocation) {
        RedemptionLocation = redemptionLocation;
    }
}

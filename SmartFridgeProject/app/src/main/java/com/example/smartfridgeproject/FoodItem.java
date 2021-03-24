package com.example.smartfridgeproject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FoodItem {
    private int mImageResource;
    private String foodName;
    private Date entryDate;
    private Date expiryDate;
    private int foodStatus;
    private boolean showMenu;
    private String imageURl;


    public FoodItem(int image, String food, Date entry, Date expiry, String imageURl){
        mImageResource=image;
        foodName=food;
        entryDate=entry;
        expiryDate=expiry;
        foodStatus=R.drawable.ic_good;
        showMenu=false;
        this.imageURl = imageURl;
    }

    public void changeText1(String text) {
        foodName = text;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getFoodName() {
        return foodName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public int getFoodStatus() {
        return foodStatus;
    }

    public boolean isShowMenu() { return showMenu; }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public void setFoodStatus(int foodStatus) {
        this.foodStatus = foodStatus;
    }

    public void setShowMenu(boolean showMenu) { this.showMenu=showMenu; }

    public String getImageURl() {
        return imageURl;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }
}
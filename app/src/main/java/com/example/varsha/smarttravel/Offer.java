package com.example.varsha.smarttravel;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Varsha on 4/23/2017.
 */

public class Offer {

    public String uid;
    public String text;
    public String name;
    public String photoUrl;
    public String offerKey;
    public int likeCount = 0;
    public Map<String, Boolean> likesGivenBy = new HashMap<>();

    public Offer() {
    }

    public Offer(String uid, String text, String name, String photoUrl, String offerKey, Map likesGivenBy) {
        this.uid = uid;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.offerKey = offerKey;
        this.likesGivenBy = likesGivenBy;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("text", text);
        result.put("photoUrl", photoUrl);
        result.put("offerKey", offerKey);
        result.put("likeCount", likeCount);
        result.put("likesGivenBy", likesGivenBy);

        return result;
    }
}
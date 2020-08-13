package com.wilchrist.poegraph;

import java.io.Serializable;

public class Poem implements Serializable {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String userId;

    public Poem(String id, String title, String content, String imageUrl) {
        this.setId(id);
        this.setTitle(title);
        this.setContent(content);
        this.setImageUrl(imageUrl);
    }

    public Poem() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

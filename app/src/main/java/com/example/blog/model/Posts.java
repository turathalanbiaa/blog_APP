package com.example.blog.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Posts implements Parcelable {
    int id,rate,views,status,category_id;
    String user_id,title,content,image,tags;
    String created_at;
    String username,commentsCount;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    String category_name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    String profilePic;

    public Posts(){}


    protected Posts(Parcel in) {
        id = in.readInt();
        rate = in.readInt();
        views = in.readInt();
        status = in.readInt();
        user_id = in.readString();
        title = in.readString();
        content = in.readString();
        image = in.readString();
        tags = in.readString();
        category_id = in.readInt();
        created_at=in.readString();
        username=in.readString();
        profilePic=in.readString();
        category_name=in.readString();
        commentsCount=in.readString();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(rate);
        parcel.writeInt(views);
        parcel.writeInt(status);
        parcel.writeString(user_id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(image);
        parcel.writeString(tags);
        parcel.writeInt(category_id);
        parcel.writeString(created_at);
        parcel.writeString(username);
        parcel.writeString(profilePic);
        parcel.writeString(category_name);
        parcel.writeString(commentsCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Posts> CREATOR = new Creator<Posts>() {
        @Override
        public Posts createFromParcel(Parcel in) {
            return new Posts(in);
        }

        @Override
        public Posts[] newArray(int size) {
            return new Posts[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


}

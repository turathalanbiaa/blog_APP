package com.example.blog;

public class URLs {
    private String defaultProfilePic="https://alkafeelblog.edu.turathalanbiaa.com/aqlam/image/000000.png";

//    private String baseUrl="http://192.168.9.110:8000/api/";
    private String baseUrl="http://blog-api.turathalanbiaa.com/api/";

    private String route;
    private String categories="cat";
    private String sendPost="addposts";
    private String sendComment="addcomment";
    private String postsFeed ="posttpagination";
    private String commentsFeed="commentpagination";
    private String profileInfo="profile";
    private String login="loginuser";
    private String register="registerusers";
    private String users="userpagination";
    private String postByUserId="PosttPaginationByUserId";
    private String deletePost="deletepost";
    private String deleteComment="deletecomment";
    private String incViews="updateviews";

    private String updatePost="updatepost";
    private String updateProfile="updateprofile";
    private String searchForUsers="getSearchResults";
    private String fbLoginInDb="registerbyfacebook";

    private String imagePath="https://alkafeelblog.edu.turathalanbiaa.com/aqlam/image/";
//    private String imagePath="http://blog-api.turathalanbiaa.com/";
//    private String imagePath="http://192.168.9.110/blog/public/images/";






    public URLs(){}

    public String getUrl(String route)
    {
        return baseUrl+route;
    }

    public String getCategoriesUrl()
    {
        return baseUrl+categories;
    }

    public String getPostsFeedUrl()
    {
        return baseUrl+ postsFeed;
    }

    public String getCommentsFeedUrl()
    {
        return baseUrl+commentsFeed;
    }

    public String getSendPostUrl()
    {
        return baseUrl+sendPost;
    }

    public String getIncViewsUrl()
    {
        return baseUrl+incViews;
    }



    public String getNextPageUrl(String route, int pageNumber)
    {
        return baseUrl+route+"?page="+pageNumber;
    }

    public String getDefaultProfilePic() {
        return defaultProfilePic;
    }

    public String getImagePath(String pic)
    {
        return imagePath+pic;
    }

    public String getCategories() {
        return categories;
    }

    public String getSendPost() {
        return sendPost;
    }

    public String getPostsFeed() {
        return postsFeed;
    }

    public String getCommentsFeed() {
        return commentsFeed;
    }

    public String getIncViews() {
        return incViews;
    }

    public String getLogin() {
        return login;
    }

    public String getProfileInfo() {
        return profileInfo;
    }

    public String getRegister() {
        return register;
    }

    public String getSendComment() {
        return sendComment;
    }
    public String getPostByUserId() {
        return postByUserId;
    }

    public String getUsers() {
        return users;
    }
    public String getDeletePost() {
        return deletePost;
    }

    public String getDeleteComment() {
        return deleteComment;
    }

    public String getImagePath() {
        return imagePath;
    }
    public String getUpdatePost() {
        return updatePost;
    }

    public String getUpdateProfile() {
        return updateProfile;
    }

    public String getSearchForUsers() {
        return searchForUsers;
    }

    public String getFbLoginInDb() {
        return fbLoginInDb;
    }
}

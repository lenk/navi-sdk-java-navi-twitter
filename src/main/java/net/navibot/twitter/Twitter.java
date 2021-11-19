package net.navibot.twitter;

import net.navibot.sdk.utils.SimpleHttpUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.IOException;

public class Twitter {
    private final String bearer;

    public Twitter(String bearer) {
        this.bearer = bearer;
    }

    public Tweet getLatestTweet(HttpClient client, String username) throws IOException, TwitterException {
        User user = getUserId(client, username);

        JSONObject json = get(client, user.id + "/tweets?tweet.fields=created_at&expansions=author_id&user.fields=created_at&max_results=5");

        if (json.has("errors")) {
            throwVerboseError(json);
        }

        return Tweet.from(json.optJSONArray("data").getJSONObject(0));
    }

    private User getUserId(HttpClient client, String username) throws TwitterException, IOException {
        JSONObject json = get(client, "/by?usernames=" + username + "&tweet.fields=author_id");

        if (json.has("errors")) {
            throwVerboseError(json);
        }

        return User.from(json);
    }

    private void throwVerboseError(JSONObject json) throws TwitterException {

        if (json.has("errors")) {
            String detail = getErrorMessage(json);

            if (detail == null) {
                throw new TwitterException("An unknown issue has occurred while finding user!");
            }

            if ("unauthorized".equalsIgnoreCase(detail)) {
                throw new TwitterException("There was an issue accessing Twitter, please contact the developers of this plugin!");
            }

            if ("forbidden".equalsIgnoreCase(detail)) {
                throw new TwitterException("The user you're trying to search for is banned from Twitter!");
            }

            if (detail.startsWith("Could not find user")) {
                throw new TwitterException("The user you're trying to search for does not exist!");
            }

            if (detail.startsWith("Sorry, you are not authorized")) {
                throw new TwitterException("The user you're trying to search for has their account set to private!");
            }

            throw new TwitterException("An issue has occurred while finding user, please try again later!");
        }

    }

    private String getErrorMessage(JSONObject json) {
        return json.getJSONArray("errors").getJSONObject(0).getString("detail");
    }

    private JSONObject get(HttpClient client, String path) throws IOException {
        HttpGet get = new HttpGet("https://api.twitter.com/2/users/" + path);
        get.setHeader("Authorization", "Bearer " + bearer);

        return new JSONObject(SimpleHttpUtils.getContent(client, get));
    }

    public static class Tweet {
        private String text;
        private String id;
        private String createdAt;
        private String authorId;

        public static Tweet from(JSONObject json) {
            Tweet tweet = new Tweet();

            tweet.text = json.optString("text");
            tweet.createdAt = json.optString("created_at");
            tweet.authorId = json.optString("author_id");
            tweet.id = json.optString("id");

            return tweet;
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public String getAuthorId() {
            return authorId;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static class User {
        private String username;
        private String id;

        public static User from(JSONObject json) {
            JSONObject data = json.optJSONArray("data").getJSONObject(0);

            User user = new User();
            user.username = data.optString("username");
            user.id = data.optString("id");

            return user;
        }
    }
}

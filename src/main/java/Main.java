import net.navibot.sdk.NaviPlugin;
import net.navibot.sdk.Trigger;
import net.navibot.sdk.data.Message;
import net.navibot.sdk.data.Response;
import net.navibot.twitter.Twitter;
import net.navibot.twitter.TwitterBearer;
import net.navibot.twitter.TwitterException;
import org.apache.http.client.HttpClient;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import java.io.IOException;
import java.util.HashMap;

public class Main extends Plugin {

    public Main(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * root class that'll be processed for custom triggers,
     * IT'S NOT RECOMMENDED YOU MOVE THIS CLASS, build around and in it
     * but for best functionality - leave it in place!
     */

    @Trigger(keyword = "!twitter", description = "View the latest twitter for a user!")
    @Extension
    public static class MyPlugin implements NaviPlugin {
        private final Twitter twitter = new Twitter(TwitterBearer.BEARER);

        /**
         * insert all of your code here for the incoming message invoking your trigger
         * you can create http requests subject to review and many other fun stuff to respond with
         * responses can be made up of both a card and a text if provided or one or the other
         *
         * @param message incoming message
         * @return your response
         */
        public Response onMessage(Message message, HttpClient client, HashMap<String, String> storage) {
            try {
                return new Response(null, twitter.getLatestTweet(client, message.body().substring(9)).getText());
            } catch (TwitterException twitterException) {
                return new Response(null, twitterException.getMessage());
            } catch (Exception io) {
                io.printStackTrace();
                return new Response(null, "There was an error establishing a connection!");
            }
        }
    }
}
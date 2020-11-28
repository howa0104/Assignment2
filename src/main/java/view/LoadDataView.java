/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.ValidationException;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;
import logic.SubredditLogic;
import reddit.DeveloperAccount;
import reddit.wrapper.AccountWrapper;
import reddit.wrapper.CommentSort;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;
import reddit.wrapper.SubredditWrapper;

/**
 *
 * @author jmlgz
 */
@WebServlet(name = "LoadDataView", urlPatterns = {"/LoadDataView"})
public class LoadDataView extends HttpServlet {

    private String errorMessage = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
    
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoadDataView</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");

            out.println("Subreddit NAME:");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", SubredditLogic.NAME);
            out.println("<br>");

            SubredditLogic subredditLogic = LogicFactory.getFor("Subreddit");
            List<Subreddit> subreddits = subredditLogic.getAll();
            out.println("Subreddit:");
            out.println("<select>");
            for (Subreddit sr : subreddits) {
                out.printf("<option value=\"\">%s</option>", sr.getName(), SubredditLogic.NAME);
            }
            out.println("</select><br>");
            out.println("<br><input type=\"submit\" name=\"load\" value=\"Load\">");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String clientID = "TpjqQ0if5bGngg";
        String clientSecret = "2sg2p_fpxZCdKP0Xea2eWeJp-bw";
        String redditUser = "howa0104";
        String algonquinUser = "howa0104";

        DeveloperAccount devop = new DeveloperAccount()
                .setClientID(clientID)
                .setClientSecret(clientSecret)
                .setRedditUser(redditUser)
                .setAlgonquinUser(algonquinUser);

        RedditWrapper scrap = new RedditWrapper();

        scrap.authenticate(devop).setLogger(false);

        if (request.getParameter("load") != null) {
            
            try {
                PostLogic postLogic = LogicFactory.getFor("Post");
                RedditAccountLogic redditAccountLogic = LogicFactory.getFor("RedditAccount");
                SubredditLogic subredditLogic = LogicFactory.getFor("Subreddit");
                CommentLogic commentLogic = LogicFactory.getFor("Comment");

                String subredditName = request.getParameter(SubredditLogic.NAME);
                scrap.configureCurentSubreddit(subredditName, 1, SubSort.BEST);

                SubredditWrapper subredditWrapper = scrap.getCurrentSubreddit();
                Subreddit subreddit = subredditLogic.getSubredditWithName(subredditWrapper.getName());
                if (subreddit == null) {
                    Map<String, String[]> map = new HashMap<>();
                    map.put(SubredditLogic.NAME, new String[]{subredditWrapper.getName()});
                    map.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(subredditWrapper.getSubscribers())});
                    map.put(SubredditLogic.URL, new String[]{subredditWrapper.getReletiveUrl()});
                    subreddit = subredditLogic.createEntity(map);
                    subredditLogic.add(subreddit);
                }

                final Subreddit finalSubreddit = subreddit;
                Consumer<PostWrapper> saveData = (PostWrapper postWrapper) -> {
                    
                    if (postWrapper.isPinned()) {
                        return;
                    }

                    AccountWrapper accountWrapper = postWrapper.getAuthor();

                    RedditAccount redditAccount = redditAccountLogic.getRedditAccountWithName(accountWrapper.getName());
                    
                    if (redditAccount == null) {
                        Map<String, String[]> map = new HashMap<>();
                        map.put(RedditAccountLogic.NAME, new String[]{accountWrapper.getName()});
                        map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(accountWrapper.getCommentKarma())});
                        map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(accountWrapper.getLinkKarma())});
                        map.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(accountWrapper.getCreated())});
                        redditAccount = redditAccountLogic.createEntity(map);
                        redditAccountLogic.add(redditAccount);
                    }

                    Post post = postLogic.getPostWithUniqueId(postWrapper.getUniqueID());
                    
                    if (post == null) {
                        Map<String, String[]> map = new HashMap<>();
                        map.put(PostLogic.UNIQUE_ID, new String[]{postWrapper.getUniqueID()});
                        map.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(postWrapper.getCommentCount())});
                        map.put(PostLogic.CREATED, new String[]{postLogic.convertDateToString(postWrapper.getCreated())});
                        map.put(PostLogic.POINTS, new String[]{Integer.toString(postWrapper.getVoteCount())});
                        map.put(PostLogic.TITLE, new String[]{postWrapper.getTitle()});
                        post = postLogic.createEntity(map);
                        post.setRedditAccountId(redditAccount);
                        post.setSubredditId(finalSubreddit);
                        postLogic.add(post);
                    }
                    final Post finalPost = post;

                    postWrapper.configComments(1, 1, CommentSort.CONFIDENCE);
                    postWrapper.processComments(commentWrapper -> {
                        
                        if (commentWrapper.isPinned() || commentWrapper.getDepth() == 0) {
                            return;
                        }

                        AccountWrapper accountWrapperUnique = commentWrapper.getAuthor();
                        RedditAccount redditAccountUnique = redditAccountLogic.getRedditAccountWithName(accountWrapperUnique.getName());
                        
                        if (redditAccountUnique == null) {
                            Map<String, String[]> map = new HashMap<>();
                            map.put(RedditAccountLogic.NAME, new String[]{accountWrapperUnique.getName()});
                            map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(accountWrapperUnique.getCommentKarma())});
                            map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(accountWrapperUnique.getLinkKarma())});
                            map.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(accountWrapperUnique.getCreated())});
                            redditAccountUnique = redditAccountLogic.createEntity(map);
                            redditAccountLogic.add(redditAccountUnique);
                        }

                        Comment comment = commentLogic.getCommentWithUniqueId(commentWrapper.getUniqueID());
                        
                        if (comment == null) {
                            Map<String, String[]> map = new HashMap<>();
                            map.put(CommentLogic.CREATED, new String[]{commentLogic.convertDateToString(commentWrapper.getCreated())});
                            int isReply = commentWrapper.getReplyCount() == 0 ? 0 : 1;
                            map.put(CommentLogic.IS_REPLY, new String[]{Integer.toString(isReply)});
                            map.put(CommentLogic.POINTS, new String[]{Integer.toString(commentWrapper.getVotes())});
                            map.put(CommentLogic.REPLYS, new String[]{Integer.toString(commentWrapper.getReplyCount())});
                            map.put(CommentLogic.TEXT, new String[]{commentWrapper.getText()});
                            map.put(CommentLogic.UNIQUE_ID, new String[]{commentWrapper.getUniqueID()});
                            comment = commentLogic.createEntity(map);
                            comment.setPostId(finalPost);
                            comment.setRedditAccountId(redditAccountUnique);
                            commentLogic.add(comment);
                        }
                    });
                };

                scrap.requestNextPage().proccessCurrentPage(saveData);
                processRequest(request, response);
            } catch (ValidationException e) {
                errorMessage = e.getMessage();
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Load Real Data";
    }

}

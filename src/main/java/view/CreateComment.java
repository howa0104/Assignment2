package view;

import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;

/**
 *
 * @author Shariar (Shawn) Emami
 */
@WebServlet( name = "CreateComment", urlPatterns = { "/CreateComment" } )
public class CreateComment extends HttpServlet {

    private String errorMessage = null;

    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Comment</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            
                out.println( "Reddit Account ID:<br>" );
                out.printf( "<input type=\"number\" name=\"%s\" value=\"\"><br>", CommentLogic.REDDIT_ACCOUNT_ID );
                out.println( "<br>" );

                out.println( "Post ID:<br>" );
                out.printf( "<input type=\"number\" name=\"%s\" value=\"\"><br>", CommentLogic.POST_ID );
                out.println( "<br>" );

                out.println( "Unique ID:<br>" );
                out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.UNIQUE_ID );
                out.println( "<br>" );

                out.println( "Text:<br>" );
                out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.TEXT );
                out.println( "<br>" );

                out.println( "Created:<br>" );
                out.printf( "<input type=\"datetime-local\" name=\"%s\" value=\"\"><br>", CommentLogic.CREATED );
                out.println( "<br>" );


                out.println( "Points:<br>" );
                out.printf( "<input type=\"number\" name=\"%s\" value=\"\"><br>", CommentLogic.POINTS );
                out.println( "<br>" );

                out.println( "Replys:<br>" );
                out.printf( "<input type=\"number\" name=\"%s\" value=\"\"><br>", CommentLogic.REPLYS );
                out.println( "<br>" );

                out.println( "Is Reply:<br>" );
                out.printf( "<input type=\"number\" name=\"%s\" value=\"\"><br>", CommentLogic.IS_REPLY );
                out.println( "<br>" );
            
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if( errorMessage != null && !errorMessage.isEmpty() ){
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }

    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
    }

    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    static int connectionCount = 0;

    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        log( "POST: Connection=" + connectionCount );

        CommentLogic aLogic = LogicFactory.getFor("Comment" );
        PostLogic pLogic = LogicFactory.getFor("Post");
        RedditAccountLogic rLogic = LogicFactory.getFor("RedditAccount");
          
               
        String uniqueId = request.getParameter( CommentLogic.UNIQUE_ID );
        String redditAccountId = request.getParameter( CommentLogic.REDDIT_ACCOUNT_ID );
        String postId = request.getParameter( CommentLogic.POST_ID );
        
        boolean redditAccountExists = false;
        boolean postIdExists = false;
        
        List<RedditAccount> redditAccounts = rLogic.getAll();
        List<Post> posts = pLogic.getAll();
        
        if(!redditAccountId.equals("")){
            for(int i=0;i<redditAccounts.size();i++){
                
                if(redditAccounts.get(i).getId()==Integer.valueOf(redditAccountId)){
                    redditAccountExists = true;        
                    i = redditAccounts.size();
                }
            }
        }
        
        if(!postId.equals("")){
           for(int i=0;i<posts.size();i++){
                if(posts.get(i).getId()==Integer.valueOf(postId)){
                    postIdExists = true;
                    i = posts.size();
                }
            } 
        }
 
        if(aLogic.getCommentWithUniqueId(uniqueId)==null && redditAccountExists==true && postIdExists==true){
            try{
                Comment comment = aLogic.createEntity(request.getParameterMap());
                comment.setRedditAccountId(rLogic.getWithId(Integer.valueOf(redditAccountId)));
                comment.setPostId(pLogic.getWithId(Integer.valueOf(postId)));
                aLogic.add(comment);
            }catch(Exception ex){
                errorMessage = ex.getMessage();
            }
        }else if(redditAccountExists==false){
            errorMessage = "RedditAccount ID: \"" + redditAccountId + "\" does not exists";
        }else if(postIdExists==false){
            errorMessage = "Post ID: \"" + postId + "\" does not exists";
        }else{
            errorMessage = "Unique ID: \"" + uniqueId + "\" already exists";
        }
        
        if( request.getParameter( "add" ) != null ){
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            response.sendRedirect( "CommentTable" );
        }
    }
    @Override
    public String getServletInfo() {
        return "Create a Comment Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
}

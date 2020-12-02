package logic;

import common.ValidationException;
import dal.CommentDAL;
import dal.RedditAccountDAL;
import entity.Comment;
import entity.RedditAccount;
import static java.lang.Boolean.parseBoolean;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;


/**
 *
 * @author Shariar (Shawn) Emami
 */
public class CommentLogic extends GenericLogic<Comment, CommentDAL> {

    public static final String REPLYS = "replys";
    public static final String IS_REPLY = "is_reply";
    public static final String POINTS = "points";
    public static final String CREATED = "created";
    public static final String TEXT = "text";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public static final String POST_ID = "post_id";

    CommentLogic() {
        super( new CommentDAL() );
    }

    @Override
    public List<Comment> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public Comment getWithId( int id ) {
        return get( () -> dal().findById( id ) );
    }

    
    public Comment getCommentWithUniqueId( String uniqueId ) {
        return get( () -> dal().findByUniqueId(uniqueId ) );
    }

    public List<Comment> getCommentsWithText( String text ) {
        return get( () -> dal().findByText(text) );
    }
    
    public List<Comment> getCommentsWithPoints( int points ) {
        return get( () -> dal().findByPoints(points ) );
    }
    
    public List<Comment> getCommentsWithReplys( int replys ) {
        return get( () -> dal().findByPoints(replys ) );
    }
    
    public List<Comment> getCommentsWithIsReplys( boolean isReply ) {
        return get( () -> dal().findByIsReply(isReply ) );
    }
   

    @Override
    public Comment createEntity( Map<String, String[]> parameterMap ) {

        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );

        Comment entity = new Comment();

        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };
   
        String text = parameterMap.get( TEXT )[ 0 ];
        String date = parameterMap.get( CREATED )[ 0 ];
        String points = parameterMap.get( POINTS )[ 0 ];
        String replys = parameterMap.get( REPLYS )[ 0 ];
        String uniqueId = parameterMap.get( UNIQUE_ID)[ 0 ];
        String isReply = parameterMap.get(IS_REPLY)[0];
        
        validator.accept(text, 999 );
        validator.accept(date,50);
        validator.accept(points,100);
        validator.accept(replys,100);
        validator.accept(uniqueId,15);
        validator.accept(isReply, 1);
        
        
        //set values on entity
        entity.setText(text );
        entity.setCreated(convertStringToDate(date) );
        entity.setPoints(Integer.valueOf(points) );
        entity.setReplys(Integer.valueOf(replys));
        entity.setUniqueId(uniqueId);
        entity.setIsReply(Integer.valueOf(isReply)!=0);

        return entity;
    }
  
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Reddit_Account_ID","Post_ID","Unique_ID", "Text", "Created", "Points", "Replys", "IsReply" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, REDDIT_ACCOUNT_ID, POST_ID, UNIQUE_ID, TEXT, CREATED, POINTS, REPLYS, IS_REPLY );
    }

    @Override
    public List<?> extractDataAsList( Comment e ) {
        return Arrays.asList( e.getId(), e.getRedditAccountId(), e.getPostId(), e.getUniqueId(), e.getText(), e.getCreated(), e.getPoints(), e.getReplys(), e.getIsReply());
    }
}

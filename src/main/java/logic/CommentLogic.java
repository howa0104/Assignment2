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

    /**
     * create static final variables with proper name of each column. this way you will never manually type it again,
     * instead always refer to these variables.
     *
     * by using the same name as column id and HTML element names we can make our code simpler. this is not recommended
     * for proper production project.
     */
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
        //do not create any logic classes in this method.

        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Comment entity = new Comment();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
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
        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        //String name, int linkPoints, int commentPoints, Date created
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

    /**
     * this method is used to send a list of all names to be used form table column headers. by having all names in one
     * location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnCodes and extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Reddit_Account_ID","Post_ID","Unique_ID", "Text", "Created", "Points", "Replys", "IsReply" );
    }

    /**
     * this method returns a list of column names that match the official column names in the db. by having all names in
     * one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnNames and extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, REDDIT_ACCOUNT_ID, POST_ID, UNIQUE_ID, TEXT, CREATED, POINTS, REPLYS, IS_REPLY );
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList( Comment e ) {
        return Arrays.asList( e.getId(), e.getRedditAccountId(), e.getPostId(), e.getUniqueId(), e.getText(), e.getCreated(), e.getPoints(), e.getReplys(), e.getIsReply());
    }
}

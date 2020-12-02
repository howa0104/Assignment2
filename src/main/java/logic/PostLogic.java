package logic;

import common.ValidationException;
import dal.PostDAL;
import entity.Post;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author geeli
 */
public class PostLogic extends GenericLogic<Post,PostDAL>{
    public static final String CREATED = "created";
    public static final String TITLE = "title";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String POINTS = "points";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public static final String SUBREDDIT_ID = "subreddit_id";
    
    
    PostLogic() {
        super(new PostDAL());
    }
    
    @Override
    public List<Post> getAll(){
        return get(()-> dal().findAll());
    }
    
    @Override
    public Post getWithId( int id){
        return get(()->dal().findById(id));
    }
    
    public Post getPostWithUniqueId( String uniqueId){
        return get(()->dal().findByUniqueId( uniqueId));
    }
    
    public List<Post> getPostWithPoints( int points){
        return get(()->dal().findByPoints(points));
    }
    
    public List<Post> getPostsWithCommentCount(int commentCount){
        return get(()->dal().findByCommentCount(commentCount));
    }
    
    public List<Post> getPostsWithAuthorID(int id){
        return get(()->dal().findByAuthor(id));
    }
    
    public List<Post> getPostsWithTitle(String title){
        return get(()->dal().findByTitle(title));
       
    }
    public List<Post> getPostsWithCreated(Date created){
        return get(()->dal().findByCreated(created));
    }
    @Override
    public Post createEntity(Map<String, String[]> parameterMap) {
       Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Post entity = new Post();

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
        String uniqueid = parameterMap.get( UNIQUE_ID )[ 0 ];
        String points = parameterMap.get( POINTS )[ 0 ];
        String commentcount = parameterMap.get( COMMENT_COUNT )[ 0 ];
        String id = parameterMap.get( ID )[ 0 ];
        String title = parameterMap.get( TITLE )[ 0 ];
        String created = parameterMap.get( CREATED )[ 0 ];

        //validate the data
        validator.accept( uniqueid, 10 );
        validator.accept( title, 255 );

        //set values on entity
        entity.setUniqueId( uniqueid );
        entity.setPoints( Integer.parseInt(points) );
        entity.setCommentCount( Integer.parseInt(commentcount) );
        entity.setId( Integer.parseInt(id) );
        entity.setTitle( title );
        entity.setCreated( convertStringToDate(created) );

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
        return Arrays.asList( "ID", "UniqueID", "Points", "CommentCount", "Title", "Created" );
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
        return Arrays.asList( ID, UNIQUE_ID, POINTS, COMMENT_COUNT, TITLE, CREATED );
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
    public List<?> extractDataAsList( Post e ) {
        return Arrays.asList( e.getId(), e.getUniqueID(), e.getPoints(), e.getCommentCount(),e.getTitle(),e.getCreated() );
    }
}

        
        
        
        
        
        
        
        
     
    
package logic;

import common.ValidationException;
import dal.RedditAccountDAL;
import entity.RedditAccount;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import common.ComponentExamples;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;


/**
 *
 * @author Shariar (Shawn) Emami
 */
public class RedditAccountLogic extends GenericLogic<RedditAccount, RedditAccountDAL> {

    /**
     * create static final variables with proper name of each column. this way you will never manually type it again,
     * instead always refer to these variables.
     *
     * by using the same name as column id and HTML element names we can make our code simpler. this is not recommended
     * for proper production project.
     */
    public static final String COMMENT_POINTS = "comment_points";
    public static final String LINK_POINTS = "link_points";
    public static final String CREATED = "created";
    public static final String NAME = "name";
    public static final String ID = "id";

    RedditAccountLogic() {
        super( new RedditAccountDAL() );
    }

    @Override
    public List<RedditAccount> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public RedditAccount getWithId( int id ) {
        return get( () -> dal().findById( id ) );
    }

    
    public RedditAccount getRedditAccountWithName( String name ) {
        return get( () -> dal().findByName(name ) );
    }

    public List<RedditAccount> getRedditAccountsWithLinkPoints( int linkPoints ) {
        return get( () -> dal().findByLinkPoints( linkPoints) );
    }

    public List<RedditAccount> getRedditAccountWithCommentPoints( int commentPoints ) {
        return get( () -> dal().findByCommentPoints(commentPoints ) );
    }


    public List<RedditAccount> getRedditAccountsWithCreated( Date created ) {
        return get( () -> dal().findByCreated(created ) );
    }


    @Override
    public RedditAccount createEntity( Map<String, String[]> parameterMap ) {
        //do not create any logic classes in this method.

        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        RedditAccount entity = new RedditAccount();

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
        String name = parameterMap.get( NAME )[ 0 ];
        int linkPoints = Integer.parseInt(parameterMap.get( LINK_POINTS )[ 0 ]);
        int commentPoints = Integer.parseInt(parameterMap.get( COMMENT_POINTS )[ 0 ]);
        String date = parameterMap.get( CREATED )[ 0 ];
        
        validator.accept( name, 45 );

        //set values on entity
        entity.setName(name );
        entity.setLinkPoints(linkPoints );
        entity.setCommentPoints(commentPoints );
        entity.setCreated(convertStringToDate(date));

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
        return Arrays.asList( "ID", "Name", "LinkPoints", "CommentPoints", "Created" );
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
        return Arrays.asList( ID, NAME, LINK_POINTS, COMMENT_POINTS, CREATED );
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
    public List<?> extractDataAsList( RedditAccount e ) {
        return Arrays.asList( e.getId(), e.getName(), e.getLinkPoints(), e.getCommentPoints(), e.getCreated());
    }
}

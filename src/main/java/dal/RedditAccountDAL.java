package dal;

import entity.RedditAccount;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public class RedditAccountDAL extends GenericDAL<RedditAccount> {

    public RedditAccountDAL() {
        super( RedditAccount.class );
    }

    @Override
    public List<RedditAccount> findAll() {
        return findResults( "RedditAccount.findAll", null );
    }

    @Override
    public RedditAccount findById( int id ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        return findResult( "RedditAccount.findById", map );
    }

    public RedditAccount findByName( String name ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        return findResult( "RedditAccount.findByName", map );
    }

    public List<RedditAccount> findByLinkPoints( int points ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "linkPoints", points );
        return findResults( "RedditAccount.findByLinkPoints", map );
    }
    
    public List<RedditAccount> findByCommentPoints( int commentPoints ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "commentPoints", commentPoints );
        return findResults( "RedditAccount.findByLinkPoints", map );
    }
    
    public List<RedditAccount> findByCreated( Date created ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "created", created );
        return findResults( "RedditAccount.findByLinkPoints", map );
    }
    
}

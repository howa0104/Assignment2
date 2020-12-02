/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.Post;

/**
 *
 * @author geeli
 */
public class PostDAL extends GenericDAL<Post>{
    
    public PostDAL() {
        super( Post.class);
    }
    //findAll() : List<Post>
    public List<Post> findAll(){
        return findResults( "Post.findAll", null);
    }
    //findById(id : int) : Post
    public Post findById( int id){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult( "Post.findById", map);
    }
    //findByUniqueId(uniqueId : String) : Post
    public Post findByUniqueId( String uniqueId){
        Map<String, Object> map = new HashMap<>();
        map.put("uniqueId", uniqueId);
        return findResult( "Post.findByUniqueId", map);
    }
    //findByPoints(points : int) : List<Post>
    public List<Post> findByPoints(int points){
        Map<String, Object> map = new HashMap<>();
        map.put("points", points);
        return findResults( "Post.findByPoints", map);
    }
    //findByCommentCount(commentCount : int) : List<Post>
    public List<Post> findByCommentCount( int commentCount){
        Map<String, Object> map = new HashMap<>();
        map.put("commentCount", commentCount);
        return findResults( "Post.findByCommentCount", map);
    }
    //findByTitle(title : String) : List<Post>
    public List<Post> findByTitle(String title){
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        return findResults( "Post.findByTitle", map);
    }
    //findByCreated(created : Date) : List<Post>
    public List<Post> findByCreated(Date created){
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults( "Post.findByCreated", map);
    }
    
    //+findByAuthor(id : int) : List<Post>
     public List<Post> findByAuthor( int id){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResults( "Post.findByAuthor", map);
    }
}

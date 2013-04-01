package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CommentMapper {

  @Insert("INSERT INTO comments(rnrId, authorId, commentText) " +
    "VALUES (#{rnrId}, #{authorId}, #{commentText})")
  int insert(Comment comment);

  @Select("SELECT * FROM comments WHERE rnrId = #{rnrId} ORDER BY createdDate")
  ArrayList<Comment> getAllCommentsByRnrId(Integer rnrId);
}

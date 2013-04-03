package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CommentMapper {

  @Insert("INSERT INTO comments(rnrId, authorId, commentText) " +
    "VALUES (#{rnrId}, #{author.id}, #{commentText})")
  int insert(Comment comment);

  @Select("SELECT * FROM comments WHERE rnrId = #{rnrId} ORDER BY createdDate")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "author.id", column = "authorId")
  })

  List<Comment> getByRnrId(Integer rnrId);
}

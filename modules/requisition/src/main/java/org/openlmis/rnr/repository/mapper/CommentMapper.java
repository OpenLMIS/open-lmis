/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the Comment entity to corresponding representation in database.
 */

@Repository
public interface CommentMapper {

  @Insert("INSERT INTO comments(rnrId, commentText, createdBy, modifiedBy) " +
    "VALUES (#{rnrId}, #{commentText}, #{author.id}, #{author.id})")
  int insert(Comment comment);

  @Select("SELECT * FROM comments WHERE rnrId = #{rnrId} ORDER BY createdDate")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "author.id", column = "createdBy")
  })
  List<Comment> getByRnrId(Long rnrId);
}

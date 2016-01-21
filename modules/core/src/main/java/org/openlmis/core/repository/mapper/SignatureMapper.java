package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Signature;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureMapper {

  @Insert("INSERT INTO signatures(type, text, modifiedBy, createdBy) VALUES " +
    "(#{type}, #{text}, #{modifiedBy}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insertSignature(Signature signature);

  @Select("SELECT type, text FROM signatures WHERE id = #{id}")
  Signature getById(Long id);

}
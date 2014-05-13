package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.equipment.domain.Donor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonorMapper {

  @Select("SELECT * from donors order by shortName")
  List<Donor> getAll();

  @Insert("INSERT into donors (shortName, longName, createdBy, createdDate, modifiedBy, modifiedDate) values (#{shortName}, #{longName}, #{createdBy},NOW(), #{modifiedBy}, NOW()) ")
  void insert(Donor donor);

  @Update("UPDATE donors SET shortName = #{shortName}, longName = #{longName}, modifiedBy = #{modifiedBy}, modifiedDate = NOW() where id = #{id}")
  void update(Donor donor);

}

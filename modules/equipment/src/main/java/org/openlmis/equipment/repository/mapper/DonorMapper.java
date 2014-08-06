package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.Donor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonorMapper {

  @Select("SELECT * from donors order by shortName, longName")
  List<Donor> getAll();

  @Select("Select * from donors AS d " +
      "LEFT JOIN (SELECT primaryDonorId AS id, Count(*) donationCount FROM facility_program_equipments fpi " +
      "          GROUP  BY primaryDonorId) AS x " +
      "          ON d.id = x.id " +
      " Order By d.shortName, d.longName")
  @Results(value = {
      @Result(property = "donationCount",column = "countOfDonations")
  })
  List<Donor> getAllWithDetails();

  @Insert("INSERT INTO donors" +
      "(code, shortName, longName, createdBy, modifiedBy, modifiedDate) " +
      "values (#{code}, #{shortName}, #{longName}, #{createdBy}, #{modifiedBy}, #{modifiedDate}) ")
  @Options(useGeneratedKeys = true)
  Integer insert(Donor donor);

  @Update("UPDATE donors " +
      "SET shortName = #{shortName}, longName =  #{longName}, code = #{code}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
      "WHERE id = #{id}")
  void update(Donor donor);

  @Delete("DELETE FROM donors WHERE ID = #{id}")
  void remove(@Param(value = "id") Long id);

  @Select("SELECT id, code, shortName, longName, modifiedBy, modifiedDate " +
      "FROM donors WHERE id = #{id}")
  Donor getById(Long id);

  @Select("SELECT * FROM donors where LOWER(code) = LOWER(#{code})")
  Donor getByCode(String code);
}
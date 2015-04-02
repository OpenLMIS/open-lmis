package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.Countries;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CountriesMapper {
    @Select("SELECT * FROM countries ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "longName", property = "longName"),
            @Result(column = "isoCode2", property = "isoCode2"),
            @Result(column = "isoCode3", property = "isoCode3")
    })
    List<Countries> loadAllList();
    @Insert({"INSERT INTO countries",
            "( name,longName,isoCode2,isoCode3, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{name},#{longName},#{isoCode2},#{isoCode3} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(Countries countries);

    @Select("SELECT * FROM countries where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "longName", property = "longName"),
            @Result(column = "isoCode2", property = "isoCode2"),
            @Result(column = "isoCode3", property = "isoCode3")
    })
    Countries getById(Long id);
    @Update("UPDATE countries " +
            "   SET name= #{name}," +
            " longName=#{longName}, " +
            "isoCode2=#{isoCode2}, " +
            "isoCode3=#{isoCode3}, " +
            "modifieddate=#{modifiedDate}, " +
            "modifiedby=#{modifiedBy} " +
            " WHERE id=#{id};")
    void update(Countries countries);

    @Delete("DELETE from countries " +
            " WHERE id=#{id};")
    void delete(Countries countries);
    @Select(value = "SELECT * FROM countries WHERE LOWER(name) LIKE '%'|| LOWER(#{param}) ||'%'")
    List<Countries> searchForCountriesList(String param);
}

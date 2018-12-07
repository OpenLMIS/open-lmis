package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Service;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceMapper {

    @Select("SELECT * from services")
    List<Service> getAll();

    @Select("SELECT * from services where id = #{id}")
    List<Service> getById(Long id);

}

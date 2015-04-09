
package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.smt.Donor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Deprecated
public interface DonorMapper2 {

    @Select("SELECT * from donors order by shortName, longName")
    List<Donor> getAll();

    @Select("SELECT id, code, shortName, longName, modifiedBy, modifiedDate " +
            "FROM donors WHERE id = #{id}")
    Donor getById(Long id);

}

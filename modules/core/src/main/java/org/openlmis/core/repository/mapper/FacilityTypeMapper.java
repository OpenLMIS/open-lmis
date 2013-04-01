package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.FacilityType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/1/13
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface FacilityTypeMapper {

    @Select("SELECT * FROM facility_types ORDER BY displayOrder")
    List<FacilityType> getAllTypes();
}

package org.openlmis.rnr.mapper;

import org.openlmis.rnr.domain.RequisitionGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionGroupMapper {


    int save(RequisitionGroup requisitionGroup);

}

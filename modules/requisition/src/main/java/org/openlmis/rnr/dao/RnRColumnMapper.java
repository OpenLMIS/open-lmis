package org.openlmis.rnr.dao;

import org.openlmis.rnr.domain.RnRColumn;

import java.util.List;

public interface RnRColumnMapper {
    List<RnRColumn> fetchAllMasterRnRColumns();
}

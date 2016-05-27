package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.service.CMMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestCmmService {

  @Autowired
  private CMMService cmmService;

  @Transactional
  public void updateCMMsForFacility(final List<CMMEntry> cmmEntries, final Long facilityId, final Long userId) {
    List<CMMEntry> entries = FluentIterable.from(cmmEntries).transform(new Function<CMMEntry, CMMEntry>() {
      @Override
      public CMMEntry apply(CMMEntry input) {
        input.setFacilityId(facilityId);
        input.setCreatedBy(userId);
        input.setModifiedBy(userId);
        return input;
      }
    }).toList();
    cmmService.updateCMMEntries(entries);
  }
}

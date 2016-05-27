package org.openlmis.stockmanagement.service;

import com.google.common.collect.FluentIterable;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.repository.CMMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CMMService {

  @Autowired
  private CMMRepository repository;

  public void updateCMMEntries(List<CMMEntry> cmmEntries) {
    for (CMMEntry entry: cmmEntries) {
      repository.createOrUpdate(entry);
    }
  }
}

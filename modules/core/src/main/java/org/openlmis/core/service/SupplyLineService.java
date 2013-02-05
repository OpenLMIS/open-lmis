package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class SupplyLineService {


  private SupplyLineRepository supplyLineRepository;

  @Autowired
  public SupplyLineService(SupplyLineRepository supplyLineRepository) {
    this.supplyLineRepository = supplyLineRepository;
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return supplyLineRepository.getSupplyLineBy(supervisoryNode, program);
  }
}

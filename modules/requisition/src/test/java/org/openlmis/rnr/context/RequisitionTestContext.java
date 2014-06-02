/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.context;


import org.junit.Ignore;
import org.openlmis.core.context.CoreTestContext;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.rnr.repository.mapper.RequisitionStatusChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;

import static org.openlmis.core.builder.ProcessingPeriodBuilder.MODIFIED_BY;


@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Ignore
public class RequisitionTestContext extends CoreTestContext {

  @Autowired
  private RequisitionMapper requisitionMapper;

  @Autowired
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;

  protected Rnr insertRequisition(ProcessingPeriod period, Program program, RnrStatus status, Boolean emergency, Facility facility, SupervisoryNode supervisoryNode, Date modifiedDate) {
    Rnr rnr = new Rnr(facility, program, period, emergency, MODIFIED_BY, 1L);
    rnr.setStatus(status);
    rnr.setEmergency(emergency);
    rnr.setModifiedDate(modifiedDate);
    rnr.setSubmittedDate(new Date(111111L));
    rnr.setProgram(program);
    rnr.setSupplyingDepot(facility);
    requisitionMapper.insert(rnr);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    rnr.setSupervisoryNodeId(supervisoryNode.getId());
    requisitionMapper.update(rnr);

    return rnr;
  }
}

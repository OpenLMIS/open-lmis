/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.context;


import com.natpryce.makeiteasy.MakeItEasy;
import org.junit.Ignore;
import org.openlmis.core.builder.*;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;


@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Ignore
public class CoreTestContext extends AbstractTransactionalJUnit4SpringContextTests {
  @Autowired
  protected ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Autowired
  private SupplyLineMapper supplyLineMapper;

  protected Program insertProgram() {
    Program program = make(MakeItEasy.a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);
    return program;
  }

  protected ProcessingPeriod insertPeriod(String name,
                                          ProcessingSchedule processingSchedule,
                                          Date periodStartDate,
                                          Date periodEndDate) {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()), with(startDate, periodStartDate),
      with(endDate, periodEndDate),
      with(ProcessingPeriodBuilder.name, name)));

    processingPeriodMapper.insert(processingPeriod);
    return processingPeriod;
  }

  protected void insertProduct(String productCode) {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode)));
    productMapper.insert(product);
  }

  protected ProcessingSchedule insertProcessingSchedule() {
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);
    return processingSchedule;
  }

  protected Facility insertFacility() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    return facility;
  }

  protected SupervisoryNode insertSupervisoryNode(String code, String name, Facility facility) {
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode,
      with(SupervisoryNodeBuilder.code, code),
      with(SupervisoryNodeBuilder.name, name),
      with(SupervisoryNodeBuilder.facility, facility)));

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }

  protected SupplyLine insertSupplyLine(Facility facility, SupervisoryNode supervisoryNode, Program program) {
    SupplyLine supplyLine = make(a(defaultSupplyLine,
      with(SupplyLineBuilder.facility, facility),
      with(SupplyLineBuilder.supervisoryNode, supervisoryNode),
      with(SupplyLineBuilder.program, program)));

    supplyLineMapper.insert(supplyLine);
    return supplyLine;
  }
}

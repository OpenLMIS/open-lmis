/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SupplyLineServiceTest {

  @Mock
  private SupplyLineRepository supplyLineRepository;

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeAndProgram() {
    SupplyLineService supplyLineService = new SupplyLineService(supplyLineRepository);
    Program program = new Program();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    SupplyLine supplyLine = new SupplyLine();
    when(supplyLineRepository.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = supplyLineService.getSupplyLineBy(supervisoryNode, program);

    verify(supplyLineRepository).getSupplyLineBy(supervisoryNode, program);
    assertThat(returnedSupplyLine, is(supplyLine));
  }
}

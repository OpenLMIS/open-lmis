/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
public class EpiUseDTOTest {

  @Test
  public void shouldReturnEpiUse() throws Exception {

    Long modifiedBy = 2L;
    EpiUseLineItemDTO epiUseLineItemDTO = mock(EpiUseLineItemDTO.class);
    List<EpiUseLineItemDTO> epiUseLineItemDTOList = asList(epiUseLineItemDTO);
    EpiUseDTO epiUseDTO = new EpiUseDTO(epiUseLineItemDTOList);
    epiUseDTO.setModifiedBy(modifiedBy);
    EpiUseLineItem epiUseLineItem = new EpiUseLineItem();
    when(epiUseLineItemDTO.transform()).thenReturn(epiUseLineItem);

    EpiUse epiUse = epiUseDTO.transform();

    assertThat(epiUse.getLineItems(), is(asList(epiUseLineItem)));
    verify(epiUseLineItemDTO).setModifiedBy(modifiedBy);
  }
}

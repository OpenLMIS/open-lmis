/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@Category(UnitTests.class)
public class BudgetLineItemDTOTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldPopulateBudgetLineItemDTO() throws Exception {
    List<String> fieldsInOneRow = asList("HIV", "F10", "2013-12-10", "345.45", "My good notes");
    Collection<EDIFileColumn> columns = new ArrayList<EDIFileColumn>() {{
      add(new EDIFileColumn("facilityCode", "header.facility.code", true, true, 2, ""));
      add(new EDIFileColumn("programCode", "header.program.code", true, true, 1, ""));
    }};

    BudgetLineItemDTO budgetLineItemDTO = BudgetLineItemDTO.populate(fieldsInOneRow, columns);

    assertEquals("F10", budgetLineItemDTO.getFacilityCode());
    assertEquals("HIV", budgetLineItemDTO.getProgramCode());
    assertEquals(null, budgetLineItemDTO.getPeriodStartDate());
    assertEquals(null, budgetLineItemDTO.getAllocatedBudget());
    assertEquals(null, budgetLineItemDTO.getNotes());
  }

  @Test
  public void shouldThrowErrorIfFacilityCodeIsMissing() throws Exception {
    BudgetLineItemDTO lineItemDTO = new BudgetLineItemDTO("", "P10", "10-12-2013", "32.67", "Notes");
    exception.expect(DataException.class);
    exception.expectMessage("error.mandatory.fields.missing");

    lineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfProgramCodeIsMissing() throws Exception {
    BudgetLineItemDTO lineItemDTO = new BudgetLineItemDTO("F10", "", "10-12-2013", "32.67", "Notes");
    exception.expect(DataException.class);
    exception.expectMessage("error.mandatory.fields.missing");

    lineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsMissing() throws Exception {
    BudgetLineItemDTO lineItemDTO = new BudgetLineItemDTO("F10", "P10", "10-12-2013", "", "Notes");
    exception.expect(DataException.class);
    exception.expectMessage("error.mandatory.fields.missing");

    lineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfPeriodDateIsMissing() throws Exception {
    BudgetLineItemDTO lineItemDTO = new BudgetLineItemDTO("F10", "P10", "", "32.67", "Notes");
    exception.expect(DataException.class);
    exception.expectMessage("error.mandatory.fields.missing");

    lineItemDTO.checkMandatoryFields();
  }
}

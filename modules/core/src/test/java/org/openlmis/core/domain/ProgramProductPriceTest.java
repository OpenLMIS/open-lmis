/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.*;

@Category(UnitTests.class)
public class ProgramProductPriceTest {

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfPricePerDosageIsNegativeOnValidation() throws Exception {
    ProgramProduct programProduct = mock(ProgramProduct.class);
    doNothing().when(programProduct).validate();
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("-1"), "source");
    expectException.expect(DataException.class);
    expectException.expectMessage("programProductPrice.invalid.price.per.dosage");
    programProductPrice.validate();
  }

  @Test
  public void shouldValidateProgramProductWhenValidatingProgramProductPrice() throws Exception {
    ProgramProduct programProduct = mock(ProgramProduct.class);
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.validate();
    verify(programProduct).validate();
  }
}

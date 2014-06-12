/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.db.categories.UnitTests;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DosageUnitRepositoryTest {

  @Mock
  private DosageUnitMapper duMapper;

  private DosageUnitRepository duRep;
  private DosageUnit du;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    duRep = new DosageUnitRepository(duMapper);
    du = new DosageUnit();
    du.setCode("some code");
    du.setDisplayOrder(1);
  }

  @Test
  public void shouldThrowExceptionIfInsertingDuplicateCode() {
    when(duMapper.getByCode("some code")).thenReturn(du);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.duplicate.dosage.unit.code");

    duRep.insert(du);
  }

  @Test
  public void shouldThrowExceptionIfInsertingInvalidObject() {
    DosageUnit badDu = new DosageUnit();
    badDu.setCode("blah"); // forget to set display order
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.missing");

    duRep.insert(badDu);
  }
}

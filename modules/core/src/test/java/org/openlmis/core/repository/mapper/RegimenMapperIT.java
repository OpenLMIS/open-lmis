/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.core.builder.RegimenBuilder.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class RegimenMapperIT {

  @Autowired
  RegimenMapper mapper;

  Regimen regimen;

  @Before
  public void setUp() throws Exception {
    regimen = make(a(defaultRegimen));
  }

  @Test
  public void shouldInsertARegimen() throws Exception {

    mapper.insert(regimen);

    assertNotNull(regimen.getId());
  }

  @Test
  public void shouldGetAllRegimenForAProgramOrderByCategoryAndDisplayOrder(){
    Long progId = 1l;
    Regimen adultRegimen1 = make(a(defaultRegimen, with(regimenCode,"CODE_1"), with(displayOrder, 1), with(programId, progId)));
    mapper.insert(adultRegimen1);
    Regimen adultRegimen2 = make(a(defaultRegimen, with(regimenCode,"CODE_2"), with(displayOrder, 2), with(programId, progId)));
    mapper.insert(adultRegimen2);
    RegimenCategory regimenCategory = new RegimenCategory("PAEDIATRICS", "Paediatrics", 2);
    regimenCategory.setId(2l);
    Regimen paediatricsRegimen1 = make(a(defaultRegimen, with(regimenCode,"CODE_4"), with(displayOrder, 1), with(category, regimenCategory), with(programId, progId)));
    mapper.insert(paediatricsRegimen1);
    Regimen paediatricsRegimen2 = make(a(defaultRegimen, with(regimenCode,"CODE_3"), with(displayOrder, 2), with(category, regimenCategory), with(programId, progId)));
    mapper.insert(paediatricsRegimen2);


    List<Regimen> regimens = mapper.getByProgram(progId);

    assertThat(regimens.size(), is(4));
    assertThat(regimens.get(0).getCode(), is("CODE_1"));
    assertThat(regimens.get(1).getCode(), is("CODE_2"));
    assertThat(regimens.get(2).getCode(), is("CODE_4"));
    assertThat(regimens.get(3).getCode(), is("CODE_3"));
  }

  @Test
  public void shouldUpdateRegimen() throws Exception {
    mapper.insert(regimen);
    regimen.setName("Regimen");
    Regimen regimen2 = make(a(defaultRegimen, with(regimenCode, "new regimen code")));
    mapper.insert(regimen2);

    mapper.update(regimen);

    List<Regimen> updatedRegimens = mapper.getByProgram(regimen.getProgramId());

    assertThat(updatedRegimens, hasItem(regimen));
    assertThat(updatedRegimens, hasItem(regimen2));
  }

  @Test
  public void shouldDeleteAllRegimensForProgram() throws Exception {

    Long progId = 1l;
    Regimen adultRegimen1 = make(a(defaultRegimen, with(regimenCode,"CODE_1"), with(displayOrder, 1), with(programId, progId)));
    mapper.insert(adultRegimen1);
    Regimen adultRegimen2 = make(a(defaultRegimen, with(regimenCode,"CODE_2"), with(displayOrder, 2), with(programId, progId)));
    mapper.insert(adultRegimen2);

    mapper.deleteByProgramId(progId);

    List<Regimen> result = mapper.getByProgram(progId);

    assertThat(result.size(), is(0));

  }
}

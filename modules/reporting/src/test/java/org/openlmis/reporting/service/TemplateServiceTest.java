/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.repository.TemplateRepository;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class TemplateServiceTest {

  @Mock
  TemplateRepository repository;

  @InjectMocks
  TemplateService service;

  @Test
  public void shouldInsertReport() throws Exception {
    Template template = Mockito.spy(new Template());

    service.insert(template);

    Mockito.verify(repository).insert(template);
  }


}

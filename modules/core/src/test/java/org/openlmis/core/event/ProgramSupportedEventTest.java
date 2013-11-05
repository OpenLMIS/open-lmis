/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.event;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(DateTime.class)
@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
public class ProgramSupportedEventTest {

  @Mock
  ProgramSupportedEventDTO programSupportedEventDTO;

  @Test
  public void shouldCreateProgramSupportedEvent() throws Exception {

    mockStatic(DateTime.class);
    DateTime dateTime = new DateTime();
    when(programSupportedEventDTO.getSerializedContents()).thenReturn("serializedContents");
    when(DateTime.now()).thenReturn(dateTime);

    ProgramSupportedEvent programSupportedEvent = new ProgramSupportedEvent(programSupportedEventDTO);

    assertThat(programSupportedEvent.getTitle(), is("Programs Supported"));
    assertNotNull(programSupportedEvent.getUuid());
    assertThat(programSupportedEvent.getCategory(), is("programs-supported"));
    assertThat(programSupportedEvent.getTimeStamp(), is(dateTime));
    assertThat(programSupportedEvent.getContents(), is("serializedContents"));
    verify(programSupportedEventDTO).getSerializedContents();
  }
}

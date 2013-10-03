/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.matchers;

import org.hamcrest.Matcher;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatcher;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

@Category(UnitTests.class)
public class Matchers {

  public static Matcher<Program> programMatcher(final Long id) {
    return new ArgumentMatcher<Program>() {
      @Override
      public boolean matches(Object argument) {
        Program program = (Program) argument;
        return program.getId().equals(id);
      }
    };
  }

  public static Matcher<ProcessingPeriod> periodMatcher(final Long id) {
    return new ArgumentMatcher<ProcessingPeriod>() {
      @Override
      public boolean matches(Object argument) {
        ProcessingPeriod period = (ProcessingPeriod) argument;
        return period.getId().equals(id);
      }
    };
  }

  public static Matcher<Facility> facilityMatcher(final Long id) {
    return new ArgumentMatcher<Facility>() {
      @Override
      public boolean matches(Object argument) {
        Facility facility = (Facility) argument;
        return facility.getId().equals(id);
      }
    };
  }

  public static ArgumentMatcher<DataException> dataExceptionMatcher(final String code, final String... params) {
    return new ArgumentMatcher<DataException>() {
      @Override
      public boolean matches(Object argument) {
        DataException dataException = (DataException) argument;
        if (!dataException.getOpenLmisMessage().getCode().equals(code)) return false;
        for (int index = 0; index < params.length; index++) {
          if (!dataException.getOpenLmisMessage().getParams()[index].equals(params[index])) return false;
        }
        return true;
      }
    };
  }
}

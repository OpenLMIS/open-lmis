/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

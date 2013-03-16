package org.openlmis.core.matchers;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;

public class Matchers {

  public static Matcher<Program> programMatcher(final int id) {
    return new ArgumentMatcher<Program>() {
      @Override
      public boolean matches(Object argument) {
        Program program = (Program) argument;
        return program.getId().equals(id);
      }
    };
  }

  public static Matcher<ProcessingPeriod> periodMatcher(final int id) {
    return new ArgumentMatcher<ProcessingPeriod>() {
      @Override
      public boolean matches(Object argument) {
        ProcessingPeriod period = (ProcessingPeriod) argument;
        return period.getId().equals(id);
      }
    };
  }

  public static Matcher<Facility> facilityMatcher(final int id) {
    return new ArgumentMatcher<Facility>() {
      @Override
      public boolean matches(Object argument) {
        Facility facility = (Facility) argument;
        return facility.getId().equals(id);
      }
    };
  }
}

package org.openlmis.distribution.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.ReasonForNotVisiting;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.dto.Reading;

import java.util.Date;

public final class ReadingParser {
  private static final Multimap<Class, String> POSITIVE_INTS = HashMultimap.create();

  static {
    POSITIVE_INTS.put(AdultCoverageLineItem.class, "healthCenterTetanus1");
    POSITIVE_INTS.put(AdultCoverageLineItem.class, "outreachTetanus1");
    POSITIVE_INTS.put(AdultCoverageLineItem.class, "healthCenterTetanus2To5");
    POSITIVE_INTS.put(AdultCoverageLineItem.class, "outreachTetanus2To5");

    POSITIVE_INTS.put(ChildCoverageLineItem.class, "healthCenter11Months");
    POSITIVE_INTS.put(ChildCoverageLineItem.class, "outreach11Months");
    POSITIVE_INTS.put(ChildCoverageLineItem.class, "healthCenter23Months");
    POSITIVE_INTS.put(ChildCoverageLineItem.class, "outreach23Months");

    POSITIVE_INTS.put(EpiInventoryLineItem.class, "existingQuantity");
    POSITIVE_INTS.put(EpiInventoryLineItem.class, "spoiledQuantity");
    POSITIVE_INTS.put(EpiInventoryLineItem.class, "deliveredQuantity");

    POSITIVE_INTS.put(EpiUseLineItem.class, "stockAtFirstOfMonth");
    POSITIVE_INTS.put(EpiUseLineItem.class, "stockAtEndOfMonth");
    POSITIVE_INTS.put(EpiUseLineItem.class, "received");
    POSITIVE_INTS.put(EpiUseLineItem.class, "loss");
    POSITIVE_INTS.put(EpiUseLineItem.class, "distributed");
    POSITIVE_INTS.put(EpiUseLineItem.class, "numberOfStockoutDays");

    POSITIVE_INTS.put(OpenedVialLineItem.class, "openedVials");

    POSITIVE_INTS.put(RefrigeratorReading.class, "lowAlarmEvents");
    POSITIVE_INTS.put(RefrigeratorReading.class, "highAlarmEvents");

    POSITIVE_INTS.put(VaccinationFullCoverage.class, "femaleHealthCenter");
    POSITIVE_INTS.put(VaccinationFullCoverage.class, "femaleOutreach");
    POSITIVE_INTS.put(VaccinationFullCoverage.class, "maleHealthCenter");
    POSITIVE_INTS.put(VaccinationFullCoverage.class, "maleOutreach");
  }

  private ReadingParser() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  public static <T> T parse(Reading property, Class owner, String propertyName, Class<T> propertyType) {
    if (propertyType.isAssignableFrom(String.class)) {
      return (T) property.getEffectiveValue();
    }

    if (propertyType.isAssignableFrom(Boolean.class)) {
      return (T) property.parseBoolean();
    }

    if (propertyType.isAssignableFrom(Float.class)) {
      return (T) property.parseFloat();
    }

    if (propertyType.isAssignableFrom(Date.class)) {
      return (T) property.parseDate();
    }

    if (propertyType.isAssignableFrom(ReasonForNotVisiting.class)) {
      return (T) property.parseReasonForNotVisiting();
    }

    if (propertyType.isAssignableFrom(Integer.class)) {
      if (POSITIVE_INTS.containsEntry(owner, propertyName)) {
        return (T) property.parsePositiveInt();
      }

      return (T) property.parseInt();
    }

    throw new IllegalArgumentException("Unsupported type " + propertyType);
  }
}

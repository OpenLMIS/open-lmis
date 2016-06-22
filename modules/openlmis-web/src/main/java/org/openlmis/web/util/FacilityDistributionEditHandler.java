package org.openlmis.web.util;

import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.dto.Reading;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyType;
import static org.openlmis.distribution.util.DomainFieldMapping.fieldMapping;
import static org.openlmis.distribution.util.ReadingParser.parse;

public class FacilityDistributionEditHandler {

  public FacilityDistributionEditResults check(FacilityDistribution original, FacilityDistributionDTO replacement) {
    FacilityDistributionEditResults results = new FacilityDistributionEditResults(original.getFacilityId());

    try {
      checkProperties(results, null, null, original, replacement);
      return results;
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private void checkProperties(FacilityDistributionEditResults results, Object parent, String parentProperty, Object original, Object replacement) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class originalClass = original.getClass();
    Class replacementClass = replacement.getClass();

    PropertyDescriptor[] originalDescriptors = getPropertyDescriptors(originalClass);

    for (PropertyDescriptor originalDescriptor : originalDescriptors) {
      String originalPropertyName = originalDescriptor.getName();
      String replacementPropertyName = fieldMapping(replacementClass, originalPropertyName);

      Class originalPropertyType = originalDescriptor.getPropertyType();
      Class replacementPropertyType = getPropertyType(replacement, replacementPropertyName);

      Object originalProperty = getProperty(original, originalPropertyName);
      Object replacementProperty = getProperty(replacement, replacementPropertyName);

      if (replacementProperty instanceof Reading) {
        Reading reading = (Reading) replacementProperty;
        Reading previous = reading.getOriginal();

        Object previousValue = parse(previous, originalClass, originalPropertyName, originalPropertyType);
        Object newValue = parse(reading, originalClass, originalPropertyName, originalPropertyType);

        String addictional = getAddictional(parent, original);

        if (previousValue.equals(originalProperty)) {
          // a user works on current version of the given property
          results.allow(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue, addictional);
          continue;
        }

        // a user works on different version of the given property
        results.deny(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue, addictional);
        continue;
      }

      if (isDTO(replacementPropertyType)) {
        checkProperties(results, original, originalPropertyName, originalProperty, replacementProperty);
      }
    }
  }

  private boolean isDTO(Class clazz) {
    String name = clazz.getPackage().getName();
    return !clazz.equals(Reading.class) && name.startsWith("org.openlmis.distribution.dto");
  }

  private String getAddictional(Object parent, Object original) {
    if (original instanceof RefrigeratorReading) {
      return ((RefrigeratorReading) original).getRefrigerator().getSerialNumber();
    }

    if (original instanceof RefrigeratorProblem && parent instanceof RefrigeratorReading) {
      return ((RefrigeratorReading) parent).getRefrigerator().getSerialNumber();
    }

    if (original instanceof EpiInventoryLineItem) {
      return ((EpiInventoryLineItem) original).getProductName();
    }

    if (original instanceof EpiUseLineItem) {
      return ((EpiUseLineItem) original).getProductGroup().getName();
    }

    if (original instanceof ChildCoverageLineItem) {
      return ((ChildCoverageLineItem) original).getVaccination();
    }

    if (original instanceof AdultCoverageLineItem) {
      return ((AdultCoverageLineItem) original).getDemographicGroup();
    }

    if (original instanceof OpenedVialLineItem) {
      return ((OpenedVialLineItem) original).getProductVialName();
    }

    return null;
  }

}

package org.openlmis.web.util;

import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.Facilitator;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.dto.Reading;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.apache.commons.lang.reflect.FieldUtils.getField;
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

      if (originalPropertyName.equals("class")) {
        continue;
      }

      if (original instanceof FacilityDistribution && originalPropertyName.equals("facility")) {
        continue;
      }

      Class originalPropertyType = originalDescriptor.getPropertyType();

      Object originalProperty = getProperty(original, originalPropertyName);
      Object replacementProperty = getProperty(replacement, replacementPropertyName);

      if (replacementProperty instanceof Reading) {
        Reading reading = (Reading) replacementProperty;
        Reading previous = reading.getOriginal();

        Object previousValue = parse(previous, originalClass, originalPropertyName, originalPropertyType);
        Object newValue = parse(reading, originalClass, originalPropertyName, originalPropertyType);

        if (Objects.equals(previousValue, newValue)) {
          // no change
          continue;
        }

        String addictional = getAddictional(parent, original);

        if (Objects.equals(previousValue, originalProperty)) {
          // a user works on current version of the given property
          results.allow(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue, addictional);
          continue;
        }

        // a user works on different version of the given property
        results.deny(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue, addictional);
        continue;
      }

      if (isDTO(replacementClass, replacementPropertyName)) {
        if (originalProperty instanceof List) {
          List originalList = (List) originalProperty;
          List replacementList = (List) replacementProperty;

          for (int i = 0; i < originalList.size(); ++i) {
            checkProperties(results, original, originalPropertyName, originalList.get(i), replacementList.get(i));
          }
        } else {
          if (original instanceof FacilityVisit && (originalPropertyName.equals("confirmedBy") || originalPropertyName.equals("verifiedBy")) && originalProperty == null) {
            originalProperty = new Facilitator();
          }

          checkProperties(results, original, originalPropertyName, originalProperty, replacementProperty);
        }
      }
    }
  }

  private boolean isDTO(Class replacementClass, String replacementPropertyName) {
    Field field = getField(replacementClass, replacementPropertyName, true);
    Class<?> clazz = field.getType();

    if (field.getType().isAssignableFrom(List.class)) {
      ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      clazz = (Class<?>) genericType.getActualTypeArguments()[0];
    }

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

package org.openlmis.web.util;

import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.dto.OpenedVialLineItemDTO;
import org.openlmis.distribution.dto.Reading;
import org.openlmis.distribution.dto.RefrigeratorReadingDTO;
import org.openlmis.distribution.dto.VaccinationFullCoverageDTO;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyType;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FacilityDistributionEditHandler {
  private static final Map<Class, Map<String, String>> DOMAIN_TO_DTO_MAP = new HashMap<>();
  private static final Map<Class, List<String>> POSITIVE_INTS = new HashMap<>();

  static {
    Map<String, String> refrigeratorReadingDTO = new HashMap<>();
    refrigeratorReadingDTO.put("problem", "problems");

    Map<String, String> vaccinationFullCoverageDTO = new HashMap<>();
    vaccinationFullCoverageDTO.put("femaleHealthCenter", "femaleHealthCenterReading");
    vaccinationFullCoverageDTO.put("femaleOutreach", "femaleMobileBrigadeReading");
    vaccinationFullCoverageDTO.put("maleHealthCenter", "maleHealthCenterReading");
    vaccinationFullCoverageDTO.put("maleOutreach", "maleMobileBrigadeReading");

    Map<String, String> openedVialLineItemDTO = new HashMap<>();
    openedVialLineItemDTO.put("openedVials", "openedVial");

    DOMAIN_TO_DTO_MAP.put(RefrigeratorReadingDTO.class, refrigeratorReadingDTO);
    DOMAIN_TO_DTO_MAP.put(VaccinationFullCoverageDTO.class, vaccinationFullCoverageDTO);
    DOMAIN_TO_DTO_MAP.put(OpenedVialLineItemDTO.class, openedVialLineItemDTO);
  }

  static {
    POSITIVE_INTS.put(AdultCoverageLineItem.class, Arrays.asList("healthCenterTetanus1", "outreachTetanus1", "healthCenterTetanus2To5", "outreachTetanus2To5"));
    POSITIVE_INTS.put(ChildCoverageLineItem.class, Arrays.asList("healthCenter11Months", "outreach11Months", "healthCenter23Months", "outreach23Months"));
    POSITIVE_INTS.put(EpiInventoryLineItem.class, Arrays.asList("existingQuantity", "spoiledQuantity", "deliveredQuantity"));
    POSITIVE_INTS.put(EpiUseLineItem.class, Arrays.asList("stockAtFirstOfMonth", "stockAtEndOfMonth", "received", "loss", "distributed"));
    POSITIVE_INTS.put(OpenedVialLineItem.class, Collections.singletonList("openedVials"));
    POSITIVE_INTS.put(RefrigeratorReading.class, Arrays.asList("lowAlarmEvents", "highAlarmEvents"));
    POSITIVE_INTS.put(VaccinationFullCoverage.class, Arrays.asList("femaleHealthCenter", "femaleOutreach", "maleHealthCenter", "maleOutreach"));
  }

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
      String replacementPropertyName = propertyMap(replacementClass, originalPropertyName);

      Class originalPropertyType = originalDescriptor.getPropertyType();
      Class replacementPropertyType = getPropertyType(replacement, replacementPropertyName);

      Object originalProperty = getProperty(original, originalPropertyName);
      Object replacementProperty = getProperty(replacement, replacementPropertyName);

      if (replacementProperty instanceof Reading) {
        Reading reading = (Reading) replacementProperty;
        Reading previous = reading.getOriginal();

        Object previousValue = getValue(previous, originalClass, originalPropertyName, originalPropertyType);
        Object newValue = getValue(reading, originalClass, originalPropertyName, originalPropertyType);

        if (previousValue.equals(originalProperty)) {
          // a user works on current version of the given property
          results.allow(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue);
          continue;
        }

        // a user works on different version of the given property
        results.deny(parent, parentProperty, original, originalPropertyName, originalProperty, previousValue, newValue);
        continue;
      }

      if (isDTO(replacementPropertyType)) {
        checkProperties(results, original, originalPropertyName, originalProperty, replacementProperty);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getValue(Reading property, Class parent, String name, Class<T> type) {
    if (type.isAssignableFrom(String.class)) {
      return (T) property.getEffectiveValue();
    }

    if (type.isAssignableFrom(Boolean.class)) {
      return (T) property.parseBoolean();
    }

    if (type.isAssignableFrom(Float.class)) {
      return (T) property.parseFloat();
    }

    if (type.isAssignableFrom(Date.class)) {
      return (T) property.parseDate();
    }

    if (type.isAssignableFrom(Integer.class)) {
      List<String> positiveProperties = POSITIVE_INTS.get(parent);

      if (null != positiveProperties && positiveProperties.contains(name)) {
        return (T) property.parsePositiveInt();
      }

      return (T) property.parseInt();
    }

    throw new IllegalArgumentException("Unsupported type " + type);
  }

  private boolean isDTO(Class clazz) {
    String name = clazz.getPackage().getName();
    return !clazz.equals(Reading.class) && name.startsWith("org.openlmis.distribution.dto");
  }

  private String propertyMap(Class clazz, String propertyName) {
    Map<String, String> map = DOMAIN_TO_DTO_MAP.get(clazz);
    String newName = null != map ? map.get(propertyName) : null;
    return isBlank(newName) ? propertyName : newName;
  }

}

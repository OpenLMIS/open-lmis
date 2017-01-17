package org.openlmis.web.util;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.Facilitator;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.dto.DistributionRefrigeratorsDTO;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.dto.Reading;
import org.openlmis.distribution.repository.DistributionRefrigeratorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.apache.commons.lang.reflect.FieldUtils.getField;
import static org.openlmis.distribution.util.DomainFieldMapping.fieldMapping;
import static org.openlmis.distribution.util.ReadingParser.parse;

@Component
public class FacilityDistributionEditHandler {

  @Autowired
  private RefrigeratorService refrigeratorService;

  @Autowired
  private DistributionRefrigeratorsRepository distributionRefrigeratorsRepository;

  public boolean modified(FacilityDistributionDTO dto) {
    try {
      return isPropertyModified(dto);
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private boolean isPropertyModified(Object bean) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class beanClass = bean.getClass();
    PropertyDescriptor[] descriptors = getPropertyDescriptors(beanClass);
    boolean modified = false;

    for (PropertyDescriptor descriptor : descriptors) {
      String name = descriptor.getName();

      if (omit(bean, name)) {
        continue;
      }

      Object value = getProperty(bean, name);

      if (value instanceof Reading) {
        Reading reading = (Reading) value;
        Reading original = reading.getOriginal();

        if (null != original && Objects.equals(original.getValue(), reading.getValue()) && Objects.equals(original.getNotRecorded(), reading.getNotRecorded())) {
          // no change
          continue;
        }

        modified = true;
      }

      // if given bean is modified, we don't have to go deeper
      if (modified) {
        break;
      }

      if (isDTO(beanClass, name)) {
        value = removeNullReference(bean, name, value);

        if (value instanceof List) {
          List list = (List) value;

          for (Object element : list) {
            modified = isPropertyModified(element);

            // if one of the element in the list was modified, we don't have to check another
            if (modified) {
              break;
            }
          }
        } else {
          modified = isPropertyModified(value);
        }
      }

      if (modified) {
        break;
      }
    }

    return modified;
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
      String replacementPropertyName = fieldMapping(replacementClass, originalPropertyName);

      if (omit(original, originalPropertyName)) {
        continue;
      }

      Class originalPropertyType = originalDescriptor.getPropertyType();

      Object originalProperty = getProperty(original, originalPropertyName);
      Object replacementProperty = getProperty(replacement, replacementPropertyName);

      if (replacementProperty instanceof Reading) {
        Reading reading = (Reading) replacementProperty;
        Reading previous = reading.getOriginal();

        Object previousValue = parse(previous, originalClass, originalPropertyName, originalPropertyType);
        Object newValue = null == previous ? null : parse(reading, originalClass, originalPropertyName, originalPropertyType);

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
        originalProperty = removeNullReference(original, originalPropertyName, originalProperty);

        if (originalProperty instanceof List) {
          if (parent instanceof FacilityDistribution && original instanceof DistributionRefrigerators && replacement instanceof DistributionRefrigeratorsDTO) {
            checkRefrigerators(results, (FacilityDistribution) parent, (DistributionRefrigerators)original, (DistributionRefrigeratorsDTO) replacement);
          } else {
            List originalList = (List) originalProperty;
            List replacementList = (List) replacementProperty;

            for (int i = 0; i < originalList.size(); ++i) {
              checkProperties(results, original, originalPropertyName, originalList.get(i), replacementList.get(i));
            }
          }
        } else {
          checkProperties(results, original, originalPropertyName, originalProperty, replacementProperty);
        }
      }
    }
  }

  private boolean isDTO(Class beanClass, String propertyName) {
    Field field = getField(beanClass, propertyName, true);
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

  private Object removeNullReference(Object bean, String propertyName, Object propertyValue) {
    if (null == propertyValue) {
      if (bean instanceof FacilityVisit && ("confirmedBy".equals(propertyName) || "verifiedBy".equals(propertyName))) {
        return new Facilitator();
      }

      if (bean instanceof RefrigeratorReading && "problem".equals(propertyName)) {
        RefrigeratorReading refrigeratorReading = (RefrigeratorReading) bean;
        return new RefrigeratorProblem(refrigeratorReading.getId());
      }

      if (bean instanceof DistributionRefrigeratorsDTO && "readings".equals(propertyName)) {
        return new ArrayList<>();
      }
    }

    return propertyValue;
  }

  private boolean omit(Object bean, String propertyName) {
    return "class".equals(propertyName)
            || bean instanceof FacilityDistribution && "facility".equals(propertyName)
            || bean instanceof FacilityDistributionDTO && ("distributionId".equals(propertyName) || "modifiedBy".equals(propertyName));

  }

  private void checkRefrigerators(FacilityDistributionEditResults results, FacilityDistribution parent, DistributionRefrigerators original, DistributionRefrigeratorsDTO replacement) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    List<RefrigeratorReading> originalReadings = original.getReadings();
    List<RefrigeratorReading> replacementReadings = replacement.transform().getReadings();

    boolean equalLists = originalReadings.size() == replacementReadings.size() && originalReadings.containsAll(replacementReadings);

    if (!equalLists) {
      if (originalReadings.size() == replacementReadings.size()) {
        // update readings
        for (int i = 0; i < originalReadings.size(); ++i) {
          checkProperties(results, original, "readings", originalReadings.get(i), replacementReadings.get(i));
        }
      } else {
        // remove existing readings
        for (RefrigeratorReading reading : originalReadings) {
          Refrigerator refrigerator = reading.getRefrigerator();
          boolean exist = FluentIterable.from(replacementReadings).anyMatch(new FindRefrigeratorReading(refrigerator.getSerialNumber()));

          if (!exist) {
            refrigerator.setEnabled(false);
            refrigeratorService.save(refrigerator);
          }
        }

        // create new reading
        for (RefrigeratorReading reading : replacementReadings) {
          Refrigerator refrigerator = reading.getRefrigerator();
          boolean exist = FluentIterable.from(originalReadings).anyMatch(new FindRefrigeratorReading(refrigerator.getSerialNumber()));

          if (!exist) {
            refrigerator.setFacilityId(parent.getFacilityId());
            refrigerator.setEnabled(true);
            refrigeratorService.save(refrigerator);
            distributionRefrigeratorsRepository.saveReading(reading, true);
          }
        }
      }
    }
  }

  private static final class FindRefrigeratorReading implements Predicate<RefrigeratorReading> {
    private String serialNumber;

    public FindRefrigeratorReading(String serialNumber) {
      this.serialNumber = serialNumber;
    }

    @Override
    public boolean apply(@Nullable RefrigeratorReading input) {
      return null != input && input.getRefrigerator().getSerialNumber().equals(serialNumber);
    }

  }

}

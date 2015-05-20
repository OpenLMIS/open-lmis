/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.Rnr.RNR_VALIDATION_ERROR;

/**
 * RegimenLineItem represents a regimenLineItem belonging to a Rnr.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonSerialize(include = NON_EMPTY)
public class RegimenLineItem extends LineItem {

  public static final String ON_TREATMENT = "patientsOnTreatment";
  public static final String INITIATED_TREATMENT = "patientsToInitiateTreatment";
  public static final String STOPPED_TREATMENT = "patientsStoppedTreatment";
  public static final String TYPE_NUMERIC = "regimen.reporting.dataType.numeric";
  public static final String REMARKS = "remarks";

  private String code;
  private String name;
  private Integer patientsOnTreatment;
  private Integer patientsToInitiateTreatment;
  private Integer patientsStoppedTreatment;

  private Integer patientsOnTreatmentAdult;
  private Integer patientsToInitiateTreatmentAdult;
  private Integer patientsStoppedTreatmentAdult;

  private Integer patientsOnTreatmentChildren;
  private Integer patientsToInitiateTreatmentChildren;
  private Integer patientsStoppedTreatmentChildren;

  private String remarks;
  private RegimenCategory category;
  private Integer regimenDisplayOrder;

  private Boolean skipped = false;

  private static Logger logger = LoggerFactory.getLogger(RegimenLineItem.class);

  public RegimenLineItem(Long rnrId, RegimenCategory category, Long createdBy, Long modifiedBy) {
    this.rnrId = rnrId;
    this.category = category;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
  }

  public void setRegimenFieldsAccordingToTemplate(RegimenTemplate regimenTemplate) {
    if (regimenTemplate.isRegimenColumnVisible(ON_TREATMENT))
      patientsOnTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(INITIATED_TREATMENT))
      patientsToInitiateTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(STOPPED_TREATMENT))
      patientsStoppedTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(REMARKS))
      remarks = "";
  }

  public void copyCreatorEditableFieldsForRegimen(RegimenLineItem regimenLineItem, RegimenTemplate regimenTemplate) {
    for (Column regimenColumn : regimenTemplate.getColumns()) {
      String fieldName = regimenColumn.getName();
      if (regimenColumn.getVisible())
        copyColumnData(fieldName, regimenLineItem);
    }

  }

  private void copyColumnData(String fieldName, RegimenLineItem regimenLineItem) {
    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      field.set(this, field.get(regimenLineItem));
    } catch (Exception e) {
      logger.error("Error in reading RnrLineItem's field", e);
    }
  }

  @Override
  public boolean compareCategory(LineItem lineItem) {
    if (this.category.getName().equals(((RegimenLineItem) lineItem).getCategory().getName())) return true;
    return false;
  }

  @Override
  public String getCategoryName() {
    return this.category.getName();
  }

  @Override
  public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
    Field field = RegimenLineItem.class.getDeclaredField(columnName);
    field.setAccessible(true);
    Object fieldValue = field.get(this);
    String value = (fieldValue == null) ? "" : fieldValue.toString();
    return value;
  }

  @Override
  public boolean isRnrLineItem() {
    return false;
  }

  public void validate(RegimenTemplate regimenTemplate) throws NoSuchFieldException, IllegalAccessException {
    String[] mandatoryVisibleColumns = new String[]{ON_TREATMENT, INITIATED_TREATMENT, STOPPED_TREATMENT};
    for (String mandatoryColumn : mandatoryVisibleColumns) {
      if (regimenTemplate.isRegimenColumnVisible(mandatoryColumn)) {
        Field field = RegimenLineItem.class.getDeclaredField(mandatoryColumn);
        field.setAccessible(true);
        Object fieldValue = field.get(this);
        if (fieldValue == null)
          throw new DataException(RNR_VALIDATION_ERROR);
      }
    }
  }

  public void populate(RegimenLineItem regimenLineItem) {
    this.patientsOnTreatment = regimenLineItem.patientsOnTreatment;
    this.patientsToInitiateTreatment = regimenLineItem.patientsToInitiateTreatment;
    this.patientsStoppedTreatment = regimenLineItem.patientsStoppedTreatment;
  }
}

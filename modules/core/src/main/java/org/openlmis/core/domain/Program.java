/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * Program represents a Program and its attributes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class Program extends BaseModel {

  private String code;
  private String name;
  private String description;
  private Boolean active;
  private Boolean budgetingApplies;
  private Boolean templateConfigured;
  private Boolean regimenTemplateConfigured;
  private Boolean isEquipmentConfigured;
  private Boolean enableSkipPeriod;
  private Boolean showNonFullSupplyTab;
  private Boolean hideSkippedProducts;
  private Boolean enableIvdForm;
  private Boolean push;
  private Boolean usePriceSchedule;
  private Integer parentId;
  private Boolean isSupportEmergency;

  private Program parent;


  public Program(Long id) {
    this.id = id;
  }

  private Program(Long id, String name, String code) {
    this.id = id;
    this.code = code;
    this.name = name;
  }

  public Program(Long id, String code, String name, String description, Boolean active, boolean templateConfigured) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.active = active;
    this.templateConfigured = templateConfigured;
    this.id = id;
  }

  public Program basicInformation() {
    Program program = new Program(id, name, code);
    program.setBudgetingApplies(budgetingApplies);
    return program;
  }

  @JsonIgnore
  public boolean isMmiaRequisition() {
    if (1 == this.getId()) {
      return true;
    }
    if (null != parent && 1 == parent.getParentId()) {
      return true;
    }
    return false;
  }

  /**
   *
   * @param program
   * @return
   * 0: The program is existed in the SIGLUS, do not need to Update;
   * 1: The program is existed in the SIGLUS, need to Update;
   * 2: The program is existed in the SIGLUS, need to Update Program and ProgramSupported;
   * -1 : it is not the same Program;
   */
  @JsonIgnore
  public int isEqualForFCProgram(Program program) {
    if (program == null) return 0;
    if (!this.code.equals(program.code)) return -1;
    if (!this.toStringForEqual().equals(program.toStringForEqual())) {
      if (this.active != program.active) {
        return 2;
      } else {
        return 1;
      }
    }
    return 0;
  }

  private String toStringForEqual() {
    return this.name + this.description + this.active;
  }

}

/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.domain.ProductCategory;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductList {

    private Integer id;
    private boolean active;
    private Integer categoryId;
    private String code;
    private String dispensingUnit;
    private Integer displayOrder ;
    private Integer dosageUnitId ;
    private Integer formId;
    private String  fullName;
    private String  primaryName;
    private String  programName;
    private Integer programId;
    private Boolean fullSupply;
    private Integer packSize;
    private String strength;
    private Boolean tracer;
    private String  type;
    private Integer packRoundingThreshold;
    private String formCode;
    private String dosageUnitCode;
    private Integer dosesPerDispensingUnit;

    private List<Program> programs;
}

/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.domain.ProductCategory;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:42 AM
 */
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

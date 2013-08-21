/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Wolde
 * Date: 5/16/13
 * Time: 1:00 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentType {
    private String name;
    private String description;
}

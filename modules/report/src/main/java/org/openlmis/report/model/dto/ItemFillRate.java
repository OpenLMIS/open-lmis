package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 9:42 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFillRate {
    private int fillRate;
    private String product;
}

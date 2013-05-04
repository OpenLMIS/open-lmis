package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 5/4/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    private Integer id;
    private String name;
    private String code;
}

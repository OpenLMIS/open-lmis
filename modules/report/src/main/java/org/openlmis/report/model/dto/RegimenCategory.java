package org.openlmis.report.model.dto;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RegimenCategory {
private int id;
private String code;
private String name;
private Integer displayOrder;

}

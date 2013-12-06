package org.openlmis.report.model.dto;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 12/2/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeographicLevel {
    int id;
    String code;
    String name;
    Integer levelNumber;

}

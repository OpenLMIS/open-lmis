package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 2:24 AM
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget extends BaseModel {

    private Facility facility;
    private Program program;
    private ProcessingPeriod period;
    private Money netBudgetAmount = new Money("0");
    private String comment;

    public Budget(Long id) {
        this.id = id;
    }
}

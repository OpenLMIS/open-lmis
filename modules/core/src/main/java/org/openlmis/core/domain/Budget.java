package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget extends BaseModel implements Importable {

    @ImportField(mandatory = true, name = "Facility Code", nested = "code")
    private Facility facility;

    @ImportField(mandatory = true, name = "Program Code", nested = "code")
    private Program program;

    @ImportField(mandatory = true, name = "Period", nested = "name")
    private ProcessingPeriod period;

    @ImportField(mandatory = true, name = "Net Budget Amount")
    private Money netBudgetAmount = new Money("0");

    @ImportField(mandatory = true, name = "Comment")
    private String comment;

    public Budget(Long id) {
        this.id = id;
    }
}

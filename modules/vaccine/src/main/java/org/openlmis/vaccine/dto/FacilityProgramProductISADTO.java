package org.openlmis.vaccine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@NoArgsConstructor
public class FacilityProgramProductISADTO extends BaseModel implements Importable {

    @ImportField(name = "Facility Code", type = "String", nested = "code", mandatory = true)
    private Facility facility;

    @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
    private Program program;

    @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
    private Product product;

    @ImportField(name = "Population Source", type = "String", mandatory = true)
    String populationSourceName;

    @ImportField(name = "WHO Ratio", type = "double", mandatory = true)
    Double whoRatio;

    @ImportField(name = "Doses Per Year", type = "int", mandatory = true)
    Integer dosesPerYear;

    @ImportField(name = "Wastage Factor", type = "double", mandatory = true)
    private Double wastageFactor;

    @ImportField(name = "Buffer Percentage", type = "double", mandatory = true)
    Double bufferPercentage;

    @ImportField(name = "Minimum Value", type = "int")
    Integer minimumValue;

    @ImportField(name = "Maximum Value", type = "int")
    Integer maximumValue;

    @ImportField(name = "Adjustment Value", type = "int", mandatory = true)
    Integer adjustmentValue;
}

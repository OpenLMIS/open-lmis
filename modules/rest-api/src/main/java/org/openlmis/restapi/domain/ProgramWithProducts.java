package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class ProgramWithProducts {

    private String programName;
    private String programCode;
    private List<Product> products;
}

package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class KitProduct extends BaseModel {

    private String kitCode;
    private String productCode;
    private Integer quantity;
}

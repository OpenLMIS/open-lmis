package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class ProductResponse {

  private Product product;
  private List<String> supportedPrograms = new ArrayList<>();

}

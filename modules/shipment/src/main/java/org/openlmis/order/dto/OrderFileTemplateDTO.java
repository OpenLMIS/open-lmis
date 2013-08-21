package org.openlmis.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Configuration;
import org.openlmis.order.domain.OrderFileColumn;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class OrderFileTemplateDTO {

  Configuration configuration;

  List<OrderFileColumn> orderFileColumns;
}

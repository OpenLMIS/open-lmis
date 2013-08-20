package org.openlmis.order.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.openlmis.core.domain.Configuration;
import org.openlmis.order.domain.OrderFileColumn;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class OrderFileTemplateDTO {

  private Configuration configuration;

  private List<OrderFileColumn> orderFileColumns;
}

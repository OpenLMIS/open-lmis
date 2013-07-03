package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RegimenColumn;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegimenTemplate {

  Long programId;

  List<RegimenColumn> regimenColumns;

}

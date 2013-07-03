package org.openlmis.web.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenColumn;

import java.util.List;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class RegimenFormDTO {

  List<Regimen> regimens;

  List<RegimenColumn> columns;

}

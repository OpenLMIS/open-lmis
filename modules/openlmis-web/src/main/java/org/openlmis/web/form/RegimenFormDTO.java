package org.openlmis.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenTemplate;

import java.util.List;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RegimenFormDTO {

  List<Regimen> regimens;

  RegimenTemplate regimenTemplate;

}

package org.openlmis.web.form;

import lombok.Data;
import org.openlmis.rnr.domain.Rnr;

import java.util.List;

@Data
public class RnrList  {
  private List<Rnr> rnrList;
}

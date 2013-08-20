/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetMember extends BaseModel implements Importable{

  @ImportField(name = "Program", nested = "code", mandatory = true)
  private Program program;

  @ImportField(name = "Facility", nested = "code", mandatory = true)
  private Facility facility;

  @ImportField(name = "Processing Period", nested = "code", mandatory = true)
  private ProcessingPeriod period;


}

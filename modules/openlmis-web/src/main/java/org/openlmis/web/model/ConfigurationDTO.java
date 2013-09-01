package org.openlmis.web.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ConfigurationSetting;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/7/13
 * Time: 4:38 PM
 */
@Data
@NoArgsConstructor
public class ConfigurationDTO {

  private List<ConfigurationSetting> list;

}

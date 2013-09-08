package org.openlmis.web.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ConfigurationSetting;

import java.util.List;

@Data
@NoArgsConstructor
public class ConfigurationDTO {

  private List<ConfigurationSetting> list;

}

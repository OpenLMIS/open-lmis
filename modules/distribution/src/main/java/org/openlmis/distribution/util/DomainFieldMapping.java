package org.openlmis.distribution.util;

import org.openlmis.distribution.dto.OpenedVialLineItemDTO;
import org.openlmis.distribution.dto.RefrigeratorReadingDTO;
import org.openlmis.distribution.dto.VaccinationFullCoverageDTO;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class DomainFieldMapping {
  private static final Map<Class, Map<String, String>> DOMAIN_TO_DTO_MAP = new HashMap<>();

  static {
    Map<String, String> refrigeratorReadingDTO = new HashMap<>();
    refrigeratorReadingDTO.put("problem", "problems");

    Map<String, String> vaccinationFullCoverageDTO = new HashMap<>();
    vaccinationFullCoverageDTO.put("femaleHealthCenter", "femaleHealthCenterReading");
    vaccinationFullCoverageDTO.put("femaleOutreach", "femaleMobileBrigadeReading");
    vaccinationFullCoverageDTO.put("maleHealthCenter", "maleHealthCenterReading");
    vaccinationFullCoverageDTO.put("maleOutreach", "maleMobileBrigadeReading");

    Map<String, String> openedVialLineItemDTO = new HashMap<>();
    openedVialLineItemDTO.put("openedVials", "openedVial");

    DOMAIN_TO_DTO_MAP.put(RefrigeratorReadingDTO.class, refrigeratorReadingDTO);
    DOMAIN_TO_DTO_MAP.put(VaccinationFullCoverageDTO.class, vaccinationFullCoverageDTO);
    DOMAIN_TO_DTO_MAP.put(OpenedVialLineItemDTO.class, openedVialLineItemDTO);
  }

  private DomainFieldMapping() {
    throw new UnsupportedOperationException();
  }

  public static String fieldMapping(Class clazz, String propertyName) {
    Map<String, String> map = DOMAIN_TO_DTO_MAP.get(clazz);
    String newName = null != map ? map.get(propertyName) : null;
    return isBlank(newName) ? propertyName : newName;
  }

}

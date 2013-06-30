package org.openlmis.core.service;

import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.repository.GeographicLevelRepository;
import org.openlmis.core.repository.mapper.GeographicLevelMapperExtension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: henok
 * Date: 6/26/13
 * Time: 4:44 PM
 */

public class GeographicLevelService {

    GeographicLevelRepository geographicLevelRepository;

    public GeographicLevel getGeographicLevel (int geographicLevelID){
        return geographicLevelRepository.getGeographicLevel(geographicLevelID);
    }

}

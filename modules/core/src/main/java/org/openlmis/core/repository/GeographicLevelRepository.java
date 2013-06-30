package org.openlmis.core.repository;

import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.repository.mapper.GeographicLevelMapperExtension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: henok
 * Date: 6/26/13
 * Time: 4:45 PM
 */
public class GeographicLevelRepository {

    @Autowired
    GeographicLevelMapperExtension mapper;

    public GeographicLevel getGeographicLevel (int geographicLevelID){
        return mapper.loadGeographicLevelById(geographicLevelID);
    }



}

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapperExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
@NoArgsConstructor
public class GeographicZoneRepositoryExtension extends GeographicZoneRepository {

    private GeographicZoneMapperExtension mapper;


    @Autowired
    public GeographicZoneRepositoryExtension(GeographicZoneMapperExtension mapper, GeographicLevelMapper geographicLevelMapper) {
        super(mapper,geographicLevelMapper);
        this.mapper = mapper;
    }

    public List<GeographicZone> searchGeographicZone(String geographicZoneSearchParam) {
        return mapper.getGeographicZoneWithSearchedName(geographicZoneSearchParam);
    }

    public GeographicZone getById(int id){
        return mapper.getGeographicZoneById(id);
    }

}

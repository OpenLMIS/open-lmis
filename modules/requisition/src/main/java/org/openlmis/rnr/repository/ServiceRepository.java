package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.Service;
import org.openlmis.rnr.repository.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ServiceRepository {

    @Autowired
    private ServiceMapper serviceMapper;

    public List<Service> getAll(){
        return serviceMapper.getAll();
    }

}

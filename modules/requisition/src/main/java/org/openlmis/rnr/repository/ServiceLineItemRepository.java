package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.ServiceLineItem;
import org.openlmis.rnr.repository.mapper.ServiceLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceLineItemRepository {

    @Autowired
    private ServiceLineItemMapper serviceLineItemMapper;

    public void save(ServiceLineItem serviceLineItem) {
        serviceLineItemMapper.insert(serviceLineItem);
    }
}

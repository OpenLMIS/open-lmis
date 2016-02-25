package org.openlmis.restapi.service;

import org.openlmis.core.repository.ArchivedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchivedProductService {
    @Autowired
    ArchivedProductRepository archivedProductRepository;

    public void updateArchivedProductList(long facilityId, List<String> codes) {
        archivedProductRepository.updateArchivedProductList(facilityId,codes);
    }

    public List<String> getAllArchivedProducts(long facilityId) {
        return archivedProductRepository.getAllArchivedProducts(facilityId);
    }
}

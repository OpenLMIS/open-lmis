package org.openlmis.core.repository;

import org.openlmis.core.repository.mapper.ArchivedProductsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ArchivedProductRepository {
    @Autowired
    ArchivedProductsMapper archivedProductsMapper;

    @Transactional
    public void updateArchivedProductList(long facilityId, List<String> codes) {
        archivedProductsMapper.clearArchivedProductList(facilityId);

        for (String code : codes) {
            archivedProductsMapper.updateArchivedProductList(facilityId, code);
        }
    }

    public List<String> getAllArchivedProducts(long facilityId) {
        return archivedProductsMapper.listArchivedProducts(facilityId);
    }
}

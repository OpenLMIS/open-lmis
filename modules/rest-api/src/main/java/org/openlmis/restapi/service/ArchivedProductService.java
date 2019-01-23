package org.openlmis.restapi.service;

import org.openlmis.core.repository.ArchivedProductRepository;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.repository.StockCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArchivedProductService {
    @Autowired
    ArchivedProductRepository archivedProductRepository;
    @Autowired
    StockCardRepository stockCardRepository;

    public void updateArchivedProductList(long facilityId, List<String> codes) {
        List<String> productCodes = new ArrayList<>();

        for(String productCode : codes) {
            if(validIfStockCardExist(facilityId, productCode)) {
                productCodes.add(productCode);
            }
        }
        archivedProductRepository.updateArchivedProductList(facilityId, productCodes);
    }

    private Boolean validIfStockCardExist(long facilityId, String code) {
        StockCard stockCard = stockCardRepository.getStockCardByFacilityAndProduct(facilityId, code);
        if(null == stockCard) {
            return false;
        }
        return true;
    }


    public List<String> getAllArchivedProducts(long facilityId) {
        return archivedProductRepository.getAllArchivedProducts(facilityId);
    }
}

package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryRepository {

    @Autowired
    VaccineInventoryMapper mapper;

    public List<Lot> getLotsByProductId(Long productId) {
        return mapper.getLotsByProductId(productId);
    }
}

package org.openlmis.vaccine.repository.Inventory;

import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.inventory.*;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryConfigurationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryConfigurationRepository {

    @Autowired
    VaccineInventoryConfigurationMapper mapper;


    public List<VaccineInventoryConfiguration> getAll() {
        return mapper.getAll();
    }

    public VaccineInventoryConfiguration getById(Long id) {
        return mapper.getById(id);
    }

    public Integer insert(VaccineInventoryConfiguration configuration) {
        return mapper.insert(configuration);
    }

    public Integer update(VaccineInventoryConfiguration configuration) {
        return mapper.update(configuration);
    }

}

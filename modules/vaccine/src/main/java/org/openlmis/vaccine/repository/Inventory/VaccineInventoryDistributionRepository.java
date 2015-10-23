package org.openlmis.vaccine.repository.Inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryDistributionRepository {

    @Autowired
    VaccineInventoryDistributionMapper mapper;

    public Integer saveDistribution(VaccineDistribution vaccineDistribution) {
        return mapper.saveDistribution(vaccineDistribution);
    }

    public Integer saveDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem) {
        return mapper.saveDistributionLineItem(vaccineDistributionLineItem);
    }

    public Integer saveDistributionLineItemLot(VaccineDistributionLineItemLot vaccineDistributionLineItemLot) {
        return mapper.saveDistributionLineItemLot(vaccineDistributionLineItemLot);
    }

    public List<VaccineDistribution> getDistributedFacilitiesByMonth(int month) {
        return mapper.getDistributedFacilitiesByMonth(month);
    }

    public List<VaccineDistribution> getDistributedFacilitiesByPeriod(Long periodId) {
        return mapper.getDistributedFacilitiesByPeriod(periodId);
    }

    public ProcessingPeriod getCurrentPeriod(Long facilityId, Long programId, Date distributionDate) {
        return mapper.getCurrentPeriod(facilityId, programId, distributionDate);
    }

}

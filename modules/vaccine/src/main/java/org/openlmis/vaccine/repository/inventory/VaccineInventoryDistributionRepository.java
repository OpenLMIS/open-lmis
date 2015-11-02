package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.stockmanagement.domain.Lot;
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

    public List<Facility> getOneLevelSupervisedFacilities(Long facilityId) {
        return mapper.getOneLevelSupervisedFacities(facilityId);
    }
    public Integer saveDistribution(VaccineDistribution vaccineDistribution) {
        return mapper.saveDistribution(vaccineDistribution);
    }

    public Integer updateDistribution(VaccineDistribution vaccineDistribution) {
        return mapper.updateDistribution(vaccineDistribution);
    }

    public Integer saveDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem) {
        return mapper.saveDistributionLineItem(vaccineDistributionLineItem);
    }

    public Integer updateDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem) {
        return mapper.updateDistributionLineItem(vaccineDistributionLineItem);
    }

    public Integer saveDistributionLineItemLot(VaccineDistributionLineItemLot vaccineDistributionLineItemLot) {
        return mapper.saveDistributionLineItemLot(vaccineDistributionLineItemLot);
    }

    public Integer updateDistributionLineItemLot(VaccineDistributionLineItemLot vaccineDistributionLineItemLot) {
        return mapper.updateDistributionLineItemLot(vaccineDistributionLineItemLot);
    }

    public List<VaccineDistribution> getDistributedFacilitiesByMonth(int month, int year) {
        return mapper.getDistributedFacilitiesByMonth(month, year);
    }

    public List<VaccineDistribution> getDistributedFacilitiesByPeriod(Long periodId) {
        return mapper.getDistributedFacilitiesByPeriod(periodId);
    }

    public ProcessingPeriod getCurrentPeriod(Long facilityId, Long programId, Date distributionDate) {
        return mapper.getCurrentPeriod(facilityId, programId, distributionDate);
    }

    public VaccineDistribution getById(Long id) {
        return mapper.getById(id);
    }

    public List<Lot> getLotsByProductId(Long productId) {
        return mapper.getLotsByProductId(productId);
    }
}

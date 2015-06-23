package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.PriceScheduleCategory;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.PriceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@NoArgsConstructor
public class PriceScheduleService {

    @Autowired
    private PriceScheduleRepository repository;

    public void save(PriceSchedule priceSchedule) {
        if(priceSchedule.getId() == null)
          repository.insert(priceSchedule);

        else
            repository.update(priceSchedule);
    }

    public BaseModel getByProductCodePriceScheduleCategory(PriceSchedule priceSchedule) {
        return repository.getByProductCodePriceScheduleCategory(priceSchedule);
    }

    public List<PriceSchedule> getByProductId(Long id) {
        return repository.getByProductId(id);
    }

    public PriceScheduleCategory getPriceScheduleCategoryByCode(String code){
        return repository.getPriceScheduleCategoryByCode(code);
    }

    @Transactional
    public void saveAll(List<PriceSchedule> priceSchedules, Product product) {

        for(PriceSchedule priceSchedule : priceSchedules){

            if(priceSchedule.getId() == null)
            priceSchedule.setProduct(product);
            priceSchedule.setModifiedBy(product.getModifiedBy());
            priceSchedule.setCreatedBy(product.getModifiedBy());
            priceSchedule.setModifiedDate(product.getModifiedDate());
            save(priceSchedule);
        }
    }

    public List<PriceScheduleCategory> getPriceScheduleCategories() {

        return repository.getPriceScheduleCategories();
    }

   public List<PriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(Long programId, Long facilityId){
       return repository.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);
   }
}

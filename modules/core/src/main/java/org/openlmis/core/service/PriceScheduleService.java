package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.repository.PriceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

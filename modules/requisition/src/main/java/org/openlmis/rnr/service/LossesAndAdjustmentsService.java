package org.openlmis.rnr.service;

import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.LossesAndAdjustmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LossesAndAdjustmentsService {

    @Autowired
    LossesAndAdjustmentsRepository lossesAndAdjustmentsRepository;


    public void save(RnrLineItem rnrLineItem, LossesAndAdjustments lossesAndAdjustments){
        lossesAndAdjustmentsRepository.save(rnrLineItem, lossesAndAdjustments);
    }

    public List<LossesAndAdjustments> getByRnrLineItem(RnrLineItem rnrLineItem){
        return lossesAndAdjustmentsRepository.getByRnrLineItem(rnrLineItem);
    }
}

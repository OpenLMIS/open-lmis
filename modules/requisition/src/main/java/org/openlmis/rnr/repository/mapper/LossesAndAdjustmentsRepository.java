package org.openlmis.rnr.repository.mapper;

import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LossesAndAdjustmentsRepository {


    LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    @Autowired
    public LossesAndAdjustmentsRepository(LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper) {
        this.lossesAndAdjustmentsMapper = lossesAndAdjustmentsMapper;
    }

    public void save(RnrLineItem rnrLineItem, LossesAndAdjustments lossesAndAdjustments) {
        Integer id = lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments);
        lossesAndAdjustments.setId(id);
    }

    public List<LossesAndAdjustments> getByRequisitionLineItem(RnrLineItem rnrLineItem) {
        return lossesAndAdjustmentsMapper.getByRequisitionLineItem(rnrLineItem);
    }
}

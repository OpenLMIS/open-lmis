package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@NoArgsConstructor
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

    public List<LossesAndAdjustments> getByRnrLineItem(RnrLineItem rnrLineItem) {
        return lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId());
    }
}

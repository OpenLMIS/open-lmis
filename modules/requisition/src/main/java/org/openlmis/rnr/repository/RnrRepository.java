package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;
import org.openlmis.rnr.service.LossesAndAdjustmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RnrRepository {

    private RnrMapper rnrMapper;
    private RnrLineItemMapper rnrLineItemMapper;
    private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    @Autowired
    public RnrRepository(RnrMapper rnrMapper, RnrLineItemMapper rnrLineItemMapper, LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper) {
        this.rnrMapper = rnrMapper;
        this.rnrLineItemMapper = rnrLineItemMapper;
        this.lossesAndAdjustmentsMapper = lossesAndAdjustmentsMapper;
    }

    public void insert(Rnr requisition) {
        rnrMapper.insert(requisition);
        List<RnrLineItem> lineItems = requisition.getLineItems();
        for (RnrLineItem lineItem : lineItems) {
            lineItem.setRnrId(requisition.getId());
            lineItem.setModifiedBy(requisition.getModifiedBy());
            rnrLineItemMapper.insert(lineItem);
            List<LossesAndAdjustments> lossesAndAdjustments = lineItem.getLossesAndAdjustmentsList();
            for(LossesAndAdjustments lossesAndAdjustment : lossesAndAdjustments){
                lossesAndAdjustmentsMapper.insert(lineItem, lossesAndAdjustment);
            }
        }
    }

    public void update(Rnr rnr) {
        rnrMapper.update(rnr);
        List<RnrLineItem> lineItems = rnr.getLineItems();
        for (RnrLineItem lineItem : lineItems) {
            rnrLineItemMapper.update(lineItem);
            List<LossesAndAdjustments> lossesAndAdjustments = lineItem.getLossesAndAdjustmentsList();
            for(LossesAndAdjustments lossesAndAdjustment : lossesAndAdjustments){
                lossesAndAdjustmentsMapper.update(lineItem, lossesAndAdjustment);
            }
        }
    }

    public Rnr getRequisitionByFacilityAndProgram(Integer facilityId, Integer programId) {
        Rnr rnr = rnrMapper.getRequisitionByFacilityAndProgram(facilityId, programId);
        if (rnr == null) return new Rnr();
        rnr.setLineItems(rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId()));
        return rnr;
    }
}

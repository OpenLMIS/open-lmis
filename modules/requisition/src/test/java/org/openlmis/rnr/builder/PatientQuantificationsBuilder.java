package org.openlmis.rnr.builder;

import org.openlmis.rnr.domain.PatientQuantificationLineItem;

import java.util.ArrayList;
import java.util.List;

public class PatientQuantificationsBuilder {

    private List<PatientQuantificationLineItem> patientQuantificationLineItemList = new ArrayList();

    public PatientQuantificationsBuilder addLineItem(PatientQuantificationLineItem lineItem) {
        this.patientQuantificationLineItemList.add(lineItem);
        return this;
    }

    public List<PatientQuantificationLineItem> build() {
        List<PatientQuantificationLineItem> lineItems = new ArrayList();
        for (PatientQuantificationLineItem lineItem : this.patientQuantificationLineItemList) {
            lineItems.add(lineItem);
        }
        return lineItems;
    }
}

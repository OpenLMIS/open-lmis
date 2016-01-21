package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.Property.newProperty;

public class PatientQuantificationsBuilder {

    public static final Property<PatientQuantificationLineItem, String> category = newProperty();
    public static final Property<PatientQuantificationLineItem, Integer> total = newProperty();

    public static final String DEFAULT_CATEGORY = "new patient";
    public static final Integer DEFAULT_TOTAL = 3;

    public static final Instantiator<PatientQuantificationLineItem> defaultPatientQuantificationLineItem = new Instantiator<PatientQuantificationLineItem>() {
        @Override
        public PatientQuantificationLineItem instantiate(PropertyLookup<PatientQuantificationLineItem> lookup) {
            PatientQuantificationLineItem patientQuantificationLineItem = new PatientQuantificationLineItem();
            patientQuantificationLineItem.setCategory(lookup.valueOf(category, DEFAULT_CATEGORY));
            patientQuantificationLineItem.setTotal(lookup.valueOf(total, DEFAULT_TOTAL));
            return patientQuantificationLineItem;
        }
    };

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

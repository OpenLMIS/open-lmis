package org.openlmis.programs.helpers;

import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class TreatmentBuilder {
    private int amount = nextInt();
    private int stock = nextInt();
    private Product product = ProductBuilder.fresh().build();
    private Implementation implementation;
    private static TreatmentBuilder treatmentBuilder;

    public static TreatmentBuilder fresh() {
        treatmentBuilder = new TreatmentBuilder();
        return treatmentBuilder;
    }

    public Treatment build() {
        Treatment treatment = new Treatment(product, amount, stock);
        treatment.setImplementation(implementation);
        return treatment;
    }

    public TreatmentBuilder setImplementation(Implementation implementation) {
        this.implementation = implementation;
        return this;
    }

    public TreatmentBuilder setProduct(Product product) {
        this.product = product;
        return this;
    }
}

package org.openlmis.programs.helpers;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Product;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openlmis.core.builder.ProductBuilder.randomProduct;

public class TreatmentBuilder {
    public static final Property<Treatment, Product> product = new Property<>();
    public static final Property<Treatment, Integer> amount = new Property<>();
    public static final Property<Treatment, Integer> stock = new Property<>();
    public static final Property<Treatment, Implementation> implementation = new Property<>();

    public static final Instantiator<Treatment> randomTreatment = new Instantiator<Treatment>() {
        @Override
        public Treatment instantiate(PropertyLookup<Treatment> lookup) {
            Treatment treatment = new Treatment(
                    lookup.valueOf(product, make(a(randomProduct))),
                    lookup.valueOf(amount, nextInt(100)),
                    lookup.valueOf(stock, nextInt(100)));
            Implementation nullImplementation = null;
            treatment.setImplementation(lookup.valueOf(implementation, nullImplementation));
            return treatment;
        }
    };
}

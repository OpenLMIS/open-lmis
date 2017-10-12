package org.openlmis.programs.helpers;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import lombok.Data;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.domain.malaria.Treatment;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.randomMalariaProgram;
import static org.openlmis.programs.helpers.TreatmentBuilder.randomTreatment;

@Data
public class ImplementationBuilder {
    public static final Property<Implementation, String> executor = new Property<>();
    public static final Property<Implementation, MalariaProgram> malariaProgram = new Property<>();
    public static final Property<Implementation, List<Treatment>> treatments = new Property<>();

    public static final Instantiator<Implementation> randomImplementation = new Instantiator<Implementation>() {
        @Override
        public Implementation instantiate(PropertyLookup<Implementation> lookup) {
            Implementation implementation = new Implementation(
                    lookup.valueOf(executor, randomAlphabetic(10)),
                    lookup.valueOf(treatments, newArrayList(make(a(randomTreatment)), make(a(randomTreatment)))));
            implementation.setMalariaProgram(lookup.valueOf(malariaProgram, (MalariaProgram) null));
            return implementation;
        }
    };

    public static List<Implementation> createRandomImplementations() {
        MalariaProgram program = make(a(randomMalariaProgram));
        List<Implementation> result = new ArrayList<>();
        int randomQuantity = nextInt(10) + 1;
        for (int i = 0; i < randomQuantity; i++) {
            result.add(make(a(randomImplementation, with(malariaProgram, program))));
        }
        return result;
    }
}

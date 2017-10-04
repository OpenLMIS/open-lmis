package org.openlmis.programs.helpers;

import lombok.Data;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.domain.malaria.Treatment;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Data
public class ImplementationBuilder {
    private int id = nextInt();
    private String executor = randomAlphanumeric(10);
    private List<Treatment> treatments = newArrayList(TreatmentBuilder.fresh().build(),
            TreatmentBuilder.fresh().build());
    private MalariaProgram malariaProgram;
    private static ImplementationBuilder implementationBuilder;

    public static ImplementationBuilder fresh() {
        implementationBuilder = new ImplementationBuilder();
        return implementationBuilder;
    }

    public Implementation build() {
        Implementation implementation = new Implementation(executor, treatments);
        implementation.setMalariaProgram(malariaProgram);
        implementation.setId(id);
        return implementation;
    }

    public ImplementationBuilder setMalariaProgram(MalariaProgram malariaProgram) {
        this.malariaProgram = malariaProgram;
        return this;
    }
}

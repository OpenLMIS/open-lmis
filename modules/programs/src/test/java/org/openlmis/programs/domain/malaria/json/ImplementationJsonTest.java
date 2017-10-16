package org.openlmis.programs.domain.malaria.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertTrue;
import static org.openlmis.programs.helpers.ImplementationBuilder.randomImplementation;

public class ImplementationJsonTest {
    @Test
    public void shouldSerializeAndDeserializeImplementation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Implementation implementation = make(a(randomImplementation));
        setImplementationToTreatments(implementation);
        String implementationJson = mapper.writeValueAsString(implementation);
        Implementation actualImplementation = mapper.readValue(implementationJson, Implementation.class);
        assertTrue(EqualsBuilder.reflectionEquals(actualImplementation, implementation, newArrayList("treatments")));
    }

    private void setImplementationToTreatments(Implementation implementation) {
        for (Treatment treatment : implementation.getTreatments()) {
            treatment.setImplementation(implementation);
        }
    }
}

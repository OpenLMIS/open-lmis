package org.openlmis.programs.domain.malaria.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.junit.Assert.assertTrue;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.randomMalariaProgram;

public class MalariaProgramJsonTest {
    @Test
    public void shouldSerializeAndDeserializeMalariaProgram() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MalariaProgram malariaProgram = make(a(randomMalariaProgram));
        setMalariaProgramToImplementations(malariaProgram);
        String malariaJson = mapper.writeValueAsString(malariaProgram);
        MalariaProgram actualMalariaProgram = mapper.readValue(malariaJson, MalariaProgram.class);
        assertTrue(EqualsBuilder.reflectionEquals(actualMalariaProgram, malariaProgram, newArrayList("implementations")));
    }

    private void setMalariaProgramToImplementations(MalariaProgram malariaProgram) {
        for (Implementation implementation : malariaProgram.getImplementations()) {
            implementation.setMalariaProgram(malariaProgram);
        }
    }
}

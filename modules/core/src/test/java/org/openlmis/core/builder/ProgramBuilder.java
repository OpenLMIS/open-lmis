package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramBuilder {
    public static final String PROGRAM_CODE = "YELL_FVR";

    public static final Property<Program, String> code = newProperty();
    public static final Property<Program, Integer> id = newProperty();

    public static final Instantiator<Program> program = new Instantiator<Program>() {
        @Override
        public Program instantiate(PropertyLookup<Program> lookup) {
            Program program = new Program();
            program.setId(lookup.valueOf(id, 9988));
            program.setCode(lookup.valueOf(code, PROGRAM_CODE));
            program.setName("Yellow Fever");
            program.setDescription("Yellow Fever program");
            program.setActive(true);
            return program;
        }
    };
}

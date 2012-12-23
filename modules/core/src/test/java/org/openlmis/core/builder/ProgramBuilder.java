package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Program;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramBuilder {

    public static final String PROGRAM_CODE = "YELL_FVR";

    public static final Property<Program, String> programCode = newProperty();
    public static final Property<Program, Boolean> programStatus = newProperty();
    public static Property<Program, Integer> programId = newProperty();

    public static final Instantiator<Program> defaultProgram = new Instantiator<Program>() {
        @Override
        public Program instantiate(PropertyLookup<Program> lookup) {
            Program program = new Program();
            program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
            program.setName("Yellow Fever");
            program.setDescription("Yellow Fever program");
            program.setActive(lookup.valueOf(programStatus, true));
            return program;
        }
    };

}

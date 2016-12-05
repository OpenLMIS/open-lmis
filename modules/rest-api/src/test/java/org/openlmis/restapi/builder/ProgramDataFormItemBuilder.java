package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.restapi.domain.ProgramDataFormItemDTO;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramDataFormItemBuilder {

  public static final Property<ProgramDataFormItemDTO, String> name = newProperty();
  public static final Property<ProgramDataFormItemDTO, String> columnCode = newProperty();
  public static final Property<ProgramDataFormItemDTO, Long> value = newProperty();

  public static final Instantiator<ProgramDataFormItemDTO> defaultProgramDataItem = new Instantiator<ProgramDataFormItemDTO>() {
    @Override
    public ProgramDataFormItemDTO instantiate(PropertyLookup<ProgramDataFormItemDTO> lookup) {
      ProgramDataFormItemDTO programDataFormItemDTO = new ProgramDataFormItemDTO();
      programDataFormItemDTO.setName(lookup.valueOf(name, "WARD"));
      programDataFormItemDTO.setColumnCode(lookup.valueOf(columnCode, "HIV-UNIGOLD"));
      programDataFormItemDTO.setValue(lookup.valueOf(value, 10L));
      return programDataFormItemDTO;
    }
  };
}

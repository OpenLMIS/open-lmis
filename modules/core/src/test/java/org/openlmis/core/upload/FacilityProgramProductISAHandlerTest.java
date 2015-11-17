package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityProgramProductISADTO;
import org.openlmis.core.repository.*;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramProductISAHandlerTest {

    @Mock
    FacilityProgramProductRepository repository;

    @Mock
    FacilityRepository facilityRepository;

    @Mock
    ProgramProductRepository programProductRepository;

    @Mock
    ProgramRepository programRepository;

    @Mock
    SupervisoryNodeRepository supervisoryNodeRepository;

    @InjectMocks
    FacilityProgramProductISAHandler handler;

    Facility facility;
    Program program;
    Product product;
    ProgramProduct programProduct;
    FacilityProgramProductISADTO fppISA;
    Double whoRatio;
    Integer dosesPerYear;
    Double wastageFactor;
    Double bufferPercentage;
    Integer adjustmentValue;

    @Before
    public void setup() {
        facility = make(a(FacilityBuilder.defaultFacility));
        program = make(a(ProgramBuilder.defaultProgram));
        product = make(a(ProductBuilder.defaultProduct));
        programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

        whoRatio = 0.0;
        dosesPerYear = 0;
        wastageFactor = 2.0;
        bufferPercentage = 0.0;
        adjustmentValue = 0;

        fppISA = new FacilityProgramProductISADTO();
        fppISA.setFacility(facility);
        fppISA.setProgram(program);
        fppISA.setProduct(product);
        fppISA.setWhoRatio(whoRatio);
        fppISA.setDosesPerYear(dosesPerYear);
        fppISA.setWastageFactor(wastageFactor);
        fppISA.setBufferPercentage(bufferPercentage);
        fppISA.setAdjustmentValue(adjustmentValue);
    }

    @Test
    public void shouldGetFacilityProgramProductISA() {
        FacilityProgramProduct expectedFpp = new FacilityProgramProduct();

        when(repository.getByCodes(any(String.class), any(String.class), any(String.class))).thenReturn(expectedFpp);

        BaseModel returnedFpp = handler.getExisting(fppISA);

        assertThat((FacilityProgramProduct)returnedFpp, is(expectedFpp));
    }

    @Test
    public void shouldSaveFacilityProgramProductISA() {
        when(facilityRepository.getByCode(any(String.class))).thenReturn(facility);
        when(programProductRepository.getByProgramAndProductCode(any(ProgramProduct.class))).thenReturn(programProduct);
        when(programRepository.getByCode(any(String.class))).thenReturn(program);
        when(supervisoryNodeRepository.getFor(any(Facility.class), any(Program.class))).thenReturn(null);

        ISA isa = new ISA();
        isa.setWhoRatio(whoRatio);
        isa.setDosesPerYear(dosesPerYear);
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(bufferPercentage);
        isa.setAdjustmentValue(adjustmentValue);

        ProgramProductISA ppISA = new ProgramProductISA(programProduct.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(facility.getId());
        fpp.setId(programProduct.getId());
        fpp.setProgramProductIsa(ppISA);

        handler.save(fppISA);

        verify(repository).save(fpp);
    }

    @Test
    public void shouldSaveFacilityProgramProductISAAndCalculateParents() {

    }
}

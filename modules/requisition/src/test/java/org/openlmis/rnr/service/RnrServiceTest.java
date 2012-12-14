package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RnrRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrServiceTest {

    public static final String HIV = "HIV";
    public Integer facilityId = 1;

    @Autowired
    private RnrService rnrService;
    @Mock
    private ProgramProductService programProductService;
    @Mock
    private RnrRepository rnrRepository;

    @Before
    public void setup() {
        rnrService = new RnrService(rnrRepository, programProductService);
    }

    @Test
    public void shouldInitRequisition() {
        when(rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(new Rnr());
        List<ProgramProduct> programProducts = new ArrayList<>();
        programProducts.add(new ProgramProduct(null, make(a(ProductBuilder.defaultProduct)), 10));
        when(programProductService.getByFacilityAndProgram(facilityId, HIV)).thenReturn(programProducts);
        Rnr rnr = rnrService.initRnr(facilityId, HIV, "user");
        verify(programProductService).getByFacilityAndProgram(facilityId, HIV);
        verify(rnrRepository).insert(rnr);
        assertThat(rnr.getLineItems().size(), is(1));
    }


    @Test
    public void shouldReturnExistingRnrIfAlreadyInitiated() {
        Rnr initiatedRnr = new Rnr();
        initiatedRnr.setId(1);
        when(rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(initiatedRnr);
        Rnr rnr = rnrService.initRnr(facilityId, HIV, "user");
        verify(programProductService, never()).getByFacilityAndProgram(facilityId, HIV);
        verify(rnrRepository, never()).insert(rnr);
        assertThat(rnr, is(initiatedRnr));
    }
}

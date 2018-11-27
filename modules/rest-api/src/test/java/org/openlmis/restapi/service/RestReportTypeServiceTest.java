/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.ReportType;
import org.openlmis.core.repository.ReportTypeRepository;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.ReportTypeDTO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestFacilityService.class)
public class RestReportTypeServiceTest {

    @Mock
    ReportTypeRepository reportTypeRepository;

    @Mock
    ProgramSupportedService programSupportedService;

    @InjectMocks
    RestReportTypeService restReportTypeService;


    @Test
    public void shouldGetAllReportTypesReturnSuccess() throws Exception {

        List<ReportType> reportTypes = new ArrayList<>();

        Program program = new Program();
        program.setId(1L);
        program.setCode("p_code");

        ReportType rt = new ReportType();
        rt.setId(1L);
        rt.setCode("code");
        rt.setName("name");
        rt.setDescription("desc");
        rt.setProgram(program);
        rt.setProgramId(1l);

        reportTypes.add(rt);

        when(reportTypeRepository.getAll()).thenReturn(reportTypes);
        List<ReportTypeDTO> rts = restReportTypeService.getAllReportType();

        assertThat(rts.size(), notNullValue());
        assertThat(rts.size(), is(1));

    }

    @Test
    public void shouldGetAllReportTypesByFacilityIdReturnSuccess() {

        ProgramSupported ps = new ProgramSupported();
        ps.setId(1l);
        ps.setFacilityId(1l);

        Program program = new Program();
        program.setCode("pc");
        program.setId(1l);
        program.setName("name");

        ps.setProgram(program);

        ReportType rt = new ReportType();
        rt.setId(1l);
        rt.setDescription("desc1");
        rt.setName("name");
        rt.setCode("1");
        ps.setReportType(rt);

        List<ProgramSupported> programSupportedList = new ArrayList<>();
        programSupportedList.add(ps);


        when(programSupportedService.getAllByFacilityId(anyLong())).thenReturn(programSupportedList);
        List<ReportTypeDTO> rts = restReportTypeService.getReportTypeByFacilityId(anyLong());

        assertThat(rts.size(), notNullValue());
        assertThat(rts.size(), is(1));

    }

}

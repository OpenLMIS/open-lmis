package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SupplyLineRepositoryTest {

    SupplyLineRepository supplyLineRepository;

    @Mock
    SupplyLineMapper supplyLineMapper;
    @Mock
    SupervisoryNodeMapper supervisoryNodeMapper;
    @Mock
    ProgramMapper programMapper;
    @Mock
    FacilityMapper facilityMapper;

    SupplyLine supplyLine;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        supplyLineRepository = new SupplyLineRepository(supplyLineMapper, supervisoryNodeMapper, programMapper, facilityMapper);
        supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
    }

    @Test
    public void shouldInsertSupplyLine() {
        supplyLineRepository.insert(supplyLine);
        verify(supplyLineMapper).insert(supplyLine);
    }

    @Test
    public void shouldThrowExceptionForDuplicateSupplyLines() {
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Duplicate entry for Supply Line found.");
        doThrow(new DuplicateKeyException("Duplicate entry for Supply Line found.")).when(supplyLineMapper).insert(supplyLine);
        supplyLineRepository.insert(supplyLine);
    }

    @Test
    public void shouldThrowErrorIfProgramDoesNotExist() {

        supplyLine.setProgram(new Program());
        when(programMapper.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(null);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Program Code does not exist");
        supplyLineRepository.insert(supplyLine);
    }

    @Test
    public void shouldThrowErrorIfFacilityDoesNotExist() {

        supplyLine.setProgram(new Program());
        when(programMapper.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
        when(facilityMapper.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(null);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Facility Code does not exist");
        supplyLineRepository.insert(supplyLine);
    }

    @Test
    public void shouldThrowErrorIfSupervisoryNodeDoesNotExist() {

        supplyLine.setProgram(new Program());
        when(programMapper.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
        when(facilityMapper.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);
        when(supervisoryNodeMapper.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(null);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Supervising Node does not exist");
        supplyLineRepository.insert(supplyLine);
    }

    @Test
    public void shouldThrowErrorIfSupervisoryNodeIsNotTheParentNode() {
        when(programMapper.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
        when(facilityMapper.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);

        SupervisoryNode supervisoryNode = supplyLine.getSupervisoryNode();
        supervisoryNode.setId(1);
        supervisoryNode.setParent(new SupervisoryNode());
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getCode())).thenReturn(1);
        when(supervisoryNodeMapper.getSupervisoryNode(supervisoryNode.getId())).thenReturn(supervisoryNode);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Supervising Node is not the Top node");
        supplyLineRepository.insert(supplyLine);
    }
}

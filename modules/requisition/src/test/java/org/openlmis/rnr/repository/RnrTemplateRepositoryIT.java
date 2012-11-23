package org.openlmis.rnr.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnType;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;
import org.openlmis.rnr.repository.mapper.RnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnrTemplateRepositoryIT {

    public static final String EXISTING_PROGRAM_CODE = "HIV";

    @Autowired
    RnrTemplateRepository rnrRepository;

    @Autowired
    ProgramRnrColumnMapper programRnrColumnMapper;

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    private List<RnrColumn> rnrColumns;


    @Before
    public void setUp() throws Exception {
        rnrColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
        programRnrColumnMapper.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        programRnrColumnMapper.deleteAll();
    }

    @Test
    public void shouldIdentifyWhenARnrTemplateForAProgramExists() throws Exception {
        configureRnRTemplateForTheProgram(EXISTING_PROGRAM_CODE);

        assertThat(rnrRepository.isRnRTemPlateDefinedForProgram(EXISTING_PROGRAM_CODE), is(true));
    }

    @Test
    public void shouldIdentifyWhenARnrTemplateForAProgramDoesNotExist() throws Exception {
        String nonExistingProgramCode = "FLU";
        assertThat(rnrRepository.isRnRTemPlateDefinedForProgram(nonExistingProgramCode), is(false));
    }

    @Test
    public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
        List<RnrColumn> result = rnrRepository.fetchAllMasterRnRColumns();

        RnrColumn rnrColumn = result.get(0);
        assertThat(rnrColumn.getName(), is("product_code"));
        assertThat(rnrColumn.getDescription(), is("Unique identifier for each commodity"));
        assertThat(rnrColumn.getPosition(), is(1));
        assertThat(rnrColumn.getLabel(), is("Product Code"));
        assertThat(rnrColumn.getDefaultValue(), is(""));
        assertThat(rnrColumn.getDataSource(), is("Reference Value (Product Table)"));
        assertThat(rnrColumn.getAvailableColumnTypes().get(0), is(RnrColumnType.Derived));
        assertThat(rnrColumn.getFormula(), is(""));
        assertThat(rnrColumn.getIndicator(), is("O"));
        assertThat(rnrColumn.isMandatory(), is(true));
    }

    @Test
    public void shouldInsertRnRColumnsForAProgram() throws Exception {
        rnrRepository.insertAllProgramRnRColumns(EXISTING_PROGRAM_CODE, rnrColumns);

        List<RnrColumn> programRnrColumns = programRnrColumnMapper.getAllRnrColumnsForProgram(EXISTING_PROGRAM_CODE);
        assertThat(programRnrColumns.size(), is(rnrColumns.size()));
        assertThat(programRnrColumns.contains(rnrColumns.get(0)), is(true));
    }

    @Test
    public void shouldUpdateRnRColumnsForAProgramIfRnrTemplateAlreadyDefined() throws Exception {
        List<RnrColumn> rnrColumns = configureAndReturnRnRTemplateForTheProgram(EXISTING_PROGRAM_CODE);

        rnrColumns.get(0).setUsed(true);
        rnrRepository.updateAllProgramRnRColumns(EXISTING_PROGRAM_CODE, rnrColumns);

        rnrColumns = rnrRepository.fetchProgramRnrColumns(EXISTING_PROGRAM_CODE);

        assertThat(rnrColumns.size(), is(rnrColumns.size()));
        assertThat(rnrColumns, hasItem(rnrColumns.get(0)));
        assertThat(rnrColumns.get(0).isUsed(), is(true));
    }

    private List<RnrColumn> configureAndReturnRnRTemplateForTheProgram(String existingProgramCode) {
        configureRnRTemplateForTheProgram(existingProgramCode);
        return rnrRepository.fetchProgramRnrColumns(EXISTING_PROGRAM_CODE);
    }

    @Test
    public void shouldRetrieveAlreadyDefinedRnrColumnsForAProgram() throws Exception {
        configureRnRTemplateForTheProgram(EXISTING_PROGRAM_CODE);
        List<RnrColumn> rnrColumns = rnrRepository.fetchProgramRnrColumns(EXISTING_PROGRAM_CODE);
        assertThat(rnrColumns.size(), is(1));
    }

    private void configureRnRTemplateForTheProgram(String existingProgramCode) {
        programRnrColumnMapper.insert(existingProgramCode, rnrColumns.get(0));
    }

}

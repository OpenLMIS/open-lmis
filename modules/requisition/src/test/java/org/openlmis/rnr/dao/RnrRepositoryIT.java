package org.openlmis.rnr.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnrRepositoryIT {

    public static final String EXISTING_PROGRAM_CODE = "HIV";

    @Autowired
    RnrRepository rnrRepository;

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
        Assert.assertThat(rnrColumn.getName(), is("MSD ProductCode"));
        Assert.assertThat(rnrColumn.getDescription(), is("This is Unique identifier for each commodity"));
        Assert.assertThat(rnrColumn.getPosition(), is(1));
        Assert.assertThat(rnrColumn.getLabel(), is("MSD ProductCode"));
        Assert.assertThat(rnrColumn.getDefaultValue(), is(""));
        Assert.assertThat(rnrColumn.getDataSource(), is("Reference Value (Product Table)"));
        Assert.assertThat(rnrColumn.getFormula(), is(""));
        Assert.assertThat(rnrColumn.getIndicator(), is("O"));
        Assert.assertThat(rnrColumn.isMandatory(), is(true));
    }

    @Test
    public void shouldInsertRnRColumnsForAProgram() throws Exception {

        rnrRepository.insertAllProgramRnRColumns(EXISTING_PROGRAM_CODE, rnrColumns);

        List<RnrColumn> programRnrColumns= programRnrColumnMapper.getAllRnrColumnsForProgram(EXISTING_PROGRAM_CODE);
        assertThat(programRnrColumns.size(), is(rnrColumns.size()));
        assertThat(programRnrColumns, hasItem(rnrColumns.get(0)));
    }

    @Test
    public void shouldUpdateRnRColumnsForAProgramIfRnrTemplateAlreadyDefined() throws Exception {
        List<RnrColumn> rnrColumns = configureAndReturnRnRTemplateForTheProgram(EXISTING_PROGRAM_CODE);

        rnrColumns.get(0).setUsed(true);
        rnrRepository.updateAllProgramRnRColumns(EXISTING_PROGRAM_CODE, rnrColumns);

        rnrColumns = rnrRepository.fetchRnrColumnsDefinedForAProgram(EXISTING_PROGRAM_CODE);

        assertThat(rnrColumns.size(), is(rnrColumns.size()));
        assertThat(rnrColumns, hasItem(rnrColumns.get(0)));
        assertThat(rnrColumns.get(0).isUsed(), is(true));
    }

    private List<RnrColumn> configureAndReturnRnRTemplateForTheProgram(String existingProgramCode) {
        configureRnRTemplateForTheProgram(existingProgramCode);
        return rnrRepository.fetchRnrColumnsDefinedForAProgram(EXISTING_PROGRAM_CODE);
    }

    @Test
    public void shouldRetrieveAlreadyDefinedRnrColumnsForAProgram() throws Exception {
        configureRnRTemplateForTheProgram(EXISTING_PROGRAM_CODE);
        List<RnrColumn> rnrColumns = rnrRepository.fetchRnrColumnsDefinedForAProgram(EXISTING_PROGRAM_CODE);
        assertThat(rnrColumns.size(), is(1));
    }

    private void configureRnRTemplateForTheProgram(String existingProgramCode) {
        programRnrColumnMapper.insert(existingProgramCode, rnrColumns.get(0));
    }
}

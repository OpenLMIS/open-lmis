package org.openlmis.rnr.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnRColumnMapperIT {

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    @Autowired
    ProgramRnRColumnMapper programRnRColumnMapper;

    @Before
    public void setUp() throws Exception {
        programRnRColumnMapper.deleteAll();
    }

    @Test
    public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
        List<RnrColumn> result = rnrColumnMapper.fetchAllMasterRnRColumns();

        assertThat(result.get(0).getName(), is("Medicine_Name"));
        assertThat(result.get(0).getDescription(), is("First test medicine"));
        assertThat(result.get(0).getPosition(), is(1));
        assertThat(result.get(0).getLabel(), is("Medicine Name"));
        assertThat(result.get(0).getDefaultValue(), is("M"));
        assertThat(result.get(0).getDataSource(), is("Derived"));
        assertThat(result.get(0).getFormula(), is("a+b+c"));
    }

}

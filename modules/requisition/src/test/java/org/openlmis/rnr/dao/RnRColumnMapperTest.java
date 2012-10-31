package org.openlmis.rnr.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnRColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnRColumnMapperTest {

    RnRColumn rnRColumn;

    @Before
    public void setUp() throws Exception {
        rnRColumn = new RnRColumn(1, "Medicine_Name", "First test medicine",1,
                "Medicine Name", "M", "Derived", "a+b+c",
                "X", true,false);
    }

    @Autowired
    RnRColumnMapper rnrColumnMapper;


    @Test
    public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
        List<RnRColumn> result = rnrColumnMapper.fetchAllMasterRnRColumns();
        assertThat(result.contains(rnRColumn),is(true));
    }

    @Test
    public void shouldInsertRnRColumnForProgram() throws Exception {
       int success = rnrColumnMapper.insert(1,rnRColumn);
       assertThat(success,is(1));
    }
}

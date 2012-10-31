package org.openlmis.rnr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.dao.RnRColumnMapper;
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

    @Autowired
    RnRColumnMapper rnrColumnMapper;


    @Test
    public void shouldRetrieveAllColumnsFromMasterTable() throws Exception {
        RnRColumn rnRColumn = new RnRColumn("Medicine_Name", "First test medicine",1,
                "Medicine Name", "M", "Derived", "a+b+c",
                "X", true,false);
        List<RnRColumn> result = rnrColumnMapper.fetchAllMasterRnRColumns();
        assertThat(result.contains(rnRColumn),is(true));
    }
}

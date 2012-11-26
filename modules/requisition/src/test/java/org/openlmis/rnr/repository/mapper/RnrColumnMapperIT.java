package org.openlmis.rnr.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnrColumnMapperIT {

    @Autowired
    private RnrColumnMapper rnrColumnMapper;

    @Test
    public void shouldRetrieveCyclicDependentColumns() throws Exception {
        List<RnrColumn> result = rnrColumnMapper.getCyclicDependencyFor("quantity_dispensed");
        RnrColumn rnrColumn = result.get(0);
        assertThat(rnrColumn.getName(), is("stock_in_hand"));
    }

}

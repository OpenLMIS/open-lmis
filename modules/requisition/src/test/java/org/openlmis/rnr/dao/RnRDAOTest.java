package org.openlmis.rnr.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.openlmis.rnr.domain.RnRColumn;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class RnRDaoTest {
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldInsertAllRnRColumns() throws Exception {
        sqlSessionFactory = mock(SqlSessionFactory.class);

        final RnRColumn column1 = mock(RnRColumn.class);
        final RnRColumn column2 = mock(RnRColumn.class);
        final ArrayList<RnRColumn> rnRColumns = new ArrayList<RnRColumn>() {{
            add(column1);
            add(column2);
        }};

        SqlSession sqlSession = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        RnRColumnMapper mapper = mock(RnRColumnMapper.class);
        when(sqlSession.getMapper(RnRColumnMapper.class)).thenReturn(mapper);

        RnRDao rnRDao = new RnRDao(sqlSessionFactory);
        Integer programId = 1;
        rnRDao.insertAllRnRColumns(programId, rnRColumns);

        verify(sqlSessionFactory).openSession(ExecutorType.BATCH);
        verify(sqlSession).getMapper(RnRColumnMapper.class);
        verify(mapper).insert(programId, column1);
        verify(mapper).insert(programId, column2);
    }
}

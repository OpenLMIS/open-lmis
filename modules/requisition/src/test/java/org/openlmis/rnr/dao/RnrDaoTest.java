package org.openlmis.rnr.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.openlmis.rnr.domain.RnrColumn;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class RnrDaoTest {

    public static final int PROGRAM_ID = 1;
    private SqlSessionFactory sqlSessionFactory;


    //TODO : use mockito annotations and write IT
    @Test
    public void shouldInsertAllRnRColumns() throws Exception {
        sqlSessionFactory = mock(SqlSessionFactory.class);

        final RnrColumn column1 = mock(RnrColumn.class);
        final RnrColumn column2 = mock(RnrColumn.class);
        final ArrayList<RnrColumn> rnrColumns = new ArrayList<RnrColumn>() {{
            add(column1);
            add(column2);
        }};

        SqlSession sqlSession = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        ProgramRnRColumnMapper mapper = mock(ProgramRnRColumnMapper.class);
        when(sqlSession.getMapper(ProgramRnRColumnMapper.class)).thenReturn(mapper);

        RnrDao rnrDao = new RnrDao(sqlSessionFactory);
        rnrDao.insertAllProgramRnRColumns(PROGRAM_ID, rnrColumns);

        verify(sqlSessionFactory).openSession(ExecutorType.BATCH);
        verify(sqlSession).getMapper(ProgramRnRColumnMapper.class);
        verify(mapper).insert(PROGRAM_ID, column1);
        verify(mapper).insert(PROGRAM_ID, column2);
    }
}

package org.openlmis.rnr.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RnrDao {

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public RnrDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertAllProgramRnRColumns(int programId, List<RnrColumn> rnrColumns) {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ProgramRnrColumnMapper mapper = session.getMapper(ProgramRnrColumnMapper.class);
        for (RnrColumn rnrColumn : rnrColumns) {
            mapper.insert(programId, rnrColumn);
        }
        session.commit();
        session.close();
    }

}

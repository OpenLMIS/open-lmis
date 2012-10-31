package org.openlmis.rnr.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openlmis.rnr.domain.RnRColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RnRDao {

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public RnRDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertAllRnRColumns(Integer programId,List<RnRColumn> rnRColumns){
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
        RnRColumnMapper mapper = session.getMapper(RnRColumnMapper.class);
        for(RnRColumn rnRColumn:rnRColumns)
        {
            mapper.insert(programId, rnRColumn);
        }
        session.commit();
        
        session.close();
    }


}

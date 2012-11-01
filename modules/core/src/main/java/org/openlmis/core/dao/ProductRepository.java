package org.openlmis.core.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRepository {


    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public ProductRepository(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertProducts(List<Product> products) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);

        for(Product product:products){
            mapper.insert(product);
        }

        sqlSession.commit();
        sqlSession.close();
    }
}

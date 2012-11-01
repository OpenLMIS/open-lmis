package org.openlmis.core.dao;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.openlmis.core.domain.Product;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductRepositoryTest {
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldInsertAllRnRColumns() throws Exception {
        sqlSessionFactory = mock(SqlSessionFactory.class);

        final Product product1 = mock(Product.class);
        final Product product2 = mock(Product.class);
        final ArrayList<Product> products = new ArrayList<Product>() {{
            add(product1);
            add(product2);

        }};

        SqlSession sqlSession = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        ProductMapper mapper = mock(ProductMapper.class);
        when(sqlSession.getMapper(ProductMapper.class)).thenReturn(mapper);

        ProductRepository repository = new ProductRepository(sqlSessionFactory);
        repository.insertProducts(products);

        verify(sqlSessionFactory).openSession(ExecutorType.BATCH);
        verify(sqlSession).getMapper(ProductMapper.class);
        verify(mapper).insert(product1);
        verify(mapper).insert(product2);
    }

}

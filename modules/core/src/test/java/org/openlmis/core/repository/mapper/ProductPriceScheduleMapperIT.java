/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ProductPriceScheduleMapperIT {


  @Autowired
  private ProductPriceScheduleMapper mapper;

  @Autowired PriceScheduleMapper priceScheduleMapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private QueryExecutor executor;

  private Product product;

  private PriceSchedule schedule;

  @Before
  public void setup() throws Exception{
    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    executor.executeQuery("insert into price_schedules (code, description) values ('A', 'Description')");
    schedule = priceScheduleMapper.getByCode("A");
  }

  @Test
  public void shouldInsert() throws Exception {
    ProductPriceSchedule productPriceSchedule = new ProductPriceSchedule();
    productPriceSchedule.setPrice(new Double(2));
    productPriceSchedule.setProduct(product);
    productPriceSchedule.setPriceSchedule(schedule);

    Integer id = mapper.insert(productPriceSchedule);
    assertThat(id, is(notNullValue()));
    assertThat(productPriceSchedule.getId(), is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    ProductPriceSchedule productPriceSchedule = new ProductPriceSchedule();
    productPriceSchedule.setPrice(new Double(2));
    productPriceSchedule.setProduct(product);
    productPriceSchedule.setPriceSchedule(schedule);

    mapper.insert(productPriceSchedule);

    productPriceSchedule.setPrice(new Double(10));

    Integer recordsUpdated = mapper.update(productPriceSchedule);
    assertThat(recordsUpdated, is(1));

  }

  @Test
  public void shouldGetByProductCodePriceSchedule() throws Exception {
    ProductPriceSchedule productPriceSchedule = new ProductPriceSchedule();
    productPriceSchedule.setPrice(new Double(2));
    productPriceSchedule.setProduct(product);
    productPriceSchedule.setPriceSchedule(schedule);

    mapper.insert(productPriceSchedule);

    ProductPriceSchedule pps = mapper.getByProductCodePriceSchedule(product.getId(), schedule.getId());
    assertThat(pps, is(notNullValue()));
    assertThat(pps.getPrice(), is(new Double(2)));
  }

  @Test
  public void shouldGetByProductId() throws Exception {
    ProductPriceSchedule productPriceSchedule = new ProductPriceSchedule();
    productPriceSchedule.setPrice(new Double(2));
    productPriceSchedule.setProduct(product);
    productPriceSchedule.setPriceSchedule(schedule);

    mapper.insert(productPriceSchedule);

    List<ProductPriceSchedule> ppses = mapper.getByProductId(product.getId());
    assertThat(ppses.size(), is(1));
  }

  @Test
  public void shouldGetPriceScheduleFullSupplyFacilityApprovedProduct() throws Exception {
    //TODO: test this
    // does it return price schedule rows for all full supply products,
    // what if the price was not found for a product?
    // etc
  }
}
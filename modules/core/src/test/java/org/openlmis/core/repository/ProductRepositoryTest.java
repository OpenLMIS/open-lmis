package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  ProductMapper mockedMapper;

  ProductRepository repository;

  @Before
  public void setUp() {
    repository = new ProductRepository(mockedMapper);
  }

  @Test
  public void shouldInsertProduct() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    repository.insert(product);
    verify(mockedMapper).insert(product);
  }

  @Test
  public void shouldRaiseDuplicateProductCodeError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Duplicate Product Code found");
    doThrow(new DuplicateKeyException("")).when(mockedMapper).insert(product);
    repository.insert(product);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("foreign key")).when(mockedMapper).insert(product);
    repository.insert(product);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Missing/Invalid Reference data");
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mockedMapper).insert(product);
    repository.insert(product);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Incorrect data length");
    doThrow(new DataIntegrityViolationException("value too long")).when(mockedMapper).insert(product);
    repository.insert(product);
  }

  @Test
  public void shouldRaiseInvalidReferenceDataDosageUnitError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.getDosageUnit().setCode("invalid code");
    when(mockedMapper.getDosageUnitIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Invalid reference data 'Dosage Unit'");
    repository.insert(product);
  }

  @Test
  public void shouldSetDataDosageUnitIdForCode() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.getDosageUnit().setCode("valid code");
    when(mockedMapper.getDosageUnitIdForCode("valid code")).thenReturn(1);

    repository.insert(product);
    assertThat(product.getDosageUnit().getId(), is(1L));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataProductFormError() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.getForm().setCode("invalid code");
    when(mockedMapper.getProductFormIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Invalid reference data 'Product Form'");
    repository.insert(product);
  }

  @Test
  public void shouldSetProductFormIdForCode() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    product.getForm().setCode("valid code");
    when(mockedMapper.getProductFormIdForCode("valid code")).thenReturn(1);

    repository.insert(product);
    assertThat(product.getForm().getId(), is(1L));
  }
}

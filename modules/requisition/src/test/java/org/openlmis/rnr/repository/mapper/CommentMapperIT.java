package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.Comment;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CommentMapperIT {

  public static final Long MODIFIED_BY = 1L;
  public static final Long HIV = 1L;
  private Rnr requisition;

  @Autowired
  CommentMapper mapper;

  @Autowired
  RequisitionMapper requisitionMapper;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  private User user;


  @Before
  public void setUp() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5"));
    programProductMapper.insert(programProduct);

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);

    requisition = new Rnr(facility.getId(), HIV, processingPeriod.getId(), MODIFIED_BY);
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);

    user = new User();
    user.setId(MODIFIED_BY);
  }

  @Test
  public void shouldInsertAComment() throws Exception {
    Comment comment = new Comment(requisition.getId(), user, "A new Comment", null);
    int numberOfRows = mapper.insert(comment);

    assertThat(numberOfRows, is(notNullValue()));
  }

  @Test
  public void shouldGetAllCommentsForARnR() throws Exception {
    mapper.insert(new Comment(requisition.getId(), user, "A new Comment1",null));

    List<Comment> listOfComments = mapper.getByRnrId(requisition.getId());

    Comment comment = listOfComments.get(0);
    assertThat(listOfComments.size(),is(1));
    assertThat(comment.getCommentText(), is("A new Comment1"));
    assertThat(comment.getAuthor().getId(), is(user.getId()));
    assertThat(comment.getCreatedDate(),is(notNullValue()));
  }
}

package org.openlmis.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.repository.MalariaProgramRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.openlmis.programs.helpers.MalariaProgramBuilder.randomMalariaProgram;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestMalariaProgramControllerTest {

    public static final String CREATE_MALARIA_PROGRAM_URL = "/rest-api/malaria-programs";
    private MockMvc mockMvc;
    private MalariaProgram malariaProgram;

    @Mock
    private MalariaProgramRepository malariaProgramRepository;

    @Mock
    private LocalValidatorFactoryBean factoryBean;

    @InjectMocks
    private RestMalariaProgramController controller;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(factoryBean).build();
        malariaProgram = make(a(randomMalariaProgram));
    }

    @Test
    public void shouldReturnCreatedHttpCode() throws Exception {
        mockMvc.perform(post(CREATE_MALARIA_PROGRAM_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(malariaProgram)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnSavedMalariaProgram() throws Exception {
        MalariaProgram expectedMalariaProgram = make(a(randomMalariaProgram));
        when(malariaProgramRepository.save(malariaProgram)).thenReturn(expectedMalariaProgram);
        ResponseEntity responseEntity = controller.create(malariaProgram);
        assertThat((MalariaProgram) responseEntity.getBody(), is(expectedMalariaProgram));
    }

    @Test
    public void shouldReturnBadRequestHttpCodeWhenMalariaProgramIsMalformed() throws Exception {
        Method create = controller.getClass().getDeclaredMethod("create", MalariaProgram.class);
        Annotation parameterAnnotation = create.getParameterAnnotations()[0][0];
        assertThat(parameterAnnotation.annotationType().getName(), is("javax.validation.Valid"));
    }

    @Test
    public void shouldReturnServerErrorWhenAnInternalExceptionOccurred() throws Exception {
        when(malariaProgramRepository.save(any(MalariaProgram.class))).thenThrow(Exception.class);
        mockMvc.perform(post(CREATE_MALARIA_PROGRAM_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(malariaProgram)))
                .andExpect(status().is5xxServerError());
        System.out.println(mapper.writeValueAsString(malariaProgram));
    }
}
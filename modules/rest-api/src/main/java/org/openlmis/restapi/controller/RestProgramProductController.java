package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.dto.ProgramProductDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;
import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.response;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class RestProgramProductController extends BaseController {

  public static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @Autowired
  private ProgramProductService service;

  @RequestMapping(value = "/rest-api/programProducts", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<RestResponse> getProgramProductsBy(@RequestParam String programCode,
                                                           @RequestParam(required = false) String facilityTypeCode) {
    try {
      final List<ProgramProductDTO> programProductsDTO = new ArrayList<>();
      List<ProgramProduct> programProducts = service.getProgramProductsBy(programCode, facilityTypeCode);
      forAllDo(programProducts, new Closure() {
        @Override
        public void execute(Object o) {
          programProductsDTO.add(new ProgramProductDTO((ProgramProduct) o));
        }
      });
      return response(PROGRAM_PRODUCT_LIST, programProductsDTO);
    } catch (DataException de) {
      return error(de.getOpenLmisMessage(), BAD_REQUEST);
    }
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse> handleException(Exception ex) {
    if (ex instanceof AccessDeniedException) {
      return error(FORBIDDEN_EXCEPTION, FORBIDDEN);
    }
    if (ex instanceof MissingServletRequestParameterException) {
      return error(ex.getMessage(), BAD_REQUEST);
    }
    return error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR);
  }

}

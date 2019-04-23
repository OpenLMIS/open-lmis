package org.openlmis.restapi.controller;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.ProductResponse;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import static org.openlmis.restapi.config.FilterProductConfig.isVersionCodeOverThanFilterThresholdVersion;
import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@NoArgsConstructor
public class RestProductController extends BaseController {

  @Autowired
  private RestProductService restProductService;

  @RequestMapping(value = "/rest-api/products", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity createOrUpdateProduct(@RequestBody(required = true) Product product) {
    restProductService.buildAndSave(product);
    return RestResponse.success("msg.kit.savesuccess");
  }

  @RequestMapping(value = "/rest-api/latest-products")
  public ResponseEntity<RestResponse> getLatestProducts(@RequestParam(required = false) Long afterUpdatedTime,
                                                        @RequestHeader(value = "VersionCode", required = false) String versionCode,
                                                        Principal principal) {

    try {
      Date afterUpdatedTimeInDate = (afterUpdatedTime == null ? null : new Date(afterUpdatedTime));
      List<ProductResponse> products = restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTimeInDate, versionCode, loggedInUserId(principal));
      RestResponse restResponse = new RestResponse("latestProducts", products);
      restResponse.addData("latestUpdatedTime", new Date());
      return new ResponseEntity<>(restResponse, HttpStatus.OK);
    } catch (DataException e) {
      return error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/rest-api/temp86-notice-kit-change")
  public ResponseEntity<RestResponse> getTemp86FilterProduct(@RequestHeader("VersionCode") String versionCode, Principal principal) {
    if (isVersionCodeOverThanFilterThresholdVersion(versionCode)) {
      List<ProductResponse> products = restProductService.getTemp86KitChangeProducts(loggedInUserId(principal));
      RestResponse restResponse = new RestResponse("kitChangeProducts", products);
      return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }
    return null;
  }
}

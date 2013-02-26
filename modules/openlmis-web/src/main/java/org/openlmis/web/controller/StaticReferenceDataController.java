package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@NoArgsConstructor
public class StaticReferenceDataController extends BaseController {

  public static final String CURRENCY = "currency";
  private StaticReferenceDataReader staticReferenceDataReader;

	@Autowired
	public StaticReferenceDataController(StaticReferenceDataReader staticReferenceDataReader) {
		this.staticReferenceDataReader = staticReferenceDataReader;
	}

	@RequestMapping(value = "/reference-data/currency", method = RequestMethod.GET)
	public ResponseEntity<OpenLmisResponse> getCurrency() {
		OpenLmisResponse response = new OpenLmisResponse(CURRENCY, staticReferenceDataReader.getCurrency());
		return new ResponseEntity(response, HttpStatus.OK);
	}
}

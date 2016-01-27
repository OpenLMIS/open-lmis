/*
 * Electronic Logistics Management Information System (eLMIS) is a supply
 * chain management system for health commodities in a developing country
 * setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for
 * the U.S. Agency for International Development (USAID). It was prepared
 * under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
  * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public
  * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.service.FacilityProductsReportDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class SimpleTableController extends BaseController {

	@Autowired
	private RequisitionReportsMapper requisitionReportsMapper;

	@Autowired
	private FacilityProductsReportDataProvider facilityProductsReportDataProvider;


	@RequestMapping(value = "/requisition-report", method = GET, headers = BaseController.ACCEPT_JSON)
	public ResponseEntity<OpenLmisResponse> requisitionReport(
					@RequestParam (value = "startTime", required = true) Date startTime,
					@RequestParam(value = "endTime", required = true) Date endTime) {
		return OpenLmisResponse.response("rnr_list", requisitionReportsMapper
            .getRequisitionList(startTime, endTime));
	}


	@RequestMapping(value = "/single-product-report", method = GET, headers = BaseController.ACCEPT_JSON)
	public ResponseEntity<OpenLmisResponse> singleProductReport(
			@RequestParam(value = "geographicZoneId", required = false) Long geographicZoneId,
			@RequestParam(value = "productId") final Long productId,
			@RequestParam(value = "endTime", required = false) final Date endTime) {
		return OpenLmisResponse.response("products", facilityProductsReportDataProvider.getReportDataForSingleProduct(geographicZoneId, productId, endTime));
	}

	@RequestMapping(value = "/all-products-report",method = GET,headers = BaseController.ACCEPT_JSON)
	public ResponseEntity<OpenLmisResponse> allProductsReport(
			@RequestParam(value = "facilityId",required = true) Long facilityId,
			@RequestParam(value = "endTime", required = false) final Date endTime){
		return OpenLmisResponse.response("products", facilityProductsReportDataProvider.getReportDataForAllProducts(facilityId, endTime));
	}


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}

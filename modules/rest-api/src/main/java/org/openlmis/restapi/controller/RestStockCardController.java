package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestStockCardService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.response;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
@Api(value = "Stock Card", description = "Stock card update", position = 0)
public class RestStockCardController extends BaseController {

    @Autowired
    private RestStockCardService restStockCardService;

    @RequestMapping(value = "/rest-api/facilities/{facilityId}/stockCards", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity adjustStock(@PathVariable long facilityId,
                                      @RequestBody(required = true) List<StockEvent> events,
                                      Principal principal) {
        try {
            restStockCardService.adjustStock(facilityId, events, loggedInUserId(principal));
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }

        return RestResponse.success("msg.stockmanagement.adjuststocksuccess");
    }

    @RequestMapping(value = "/rest-api/facilities/{facilityId}/stockCards", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getStockMovements(@PathVariable long facilityId,
                                            @RequestParam(value = "startTime", required = false) final String startTime,
                                            @RequestParam(value = "endTime", required = false) final String endTime) {
        List<StockCard> stockCards = new ArrayList<>();
        try {
            stockCards = restStockCardService.queryStockCardByMovementDate(facilityId,
                DateUtil.parseDate(startTime, DateUtil.FORMAT_DATE),
                DateUtil.parseDate(endTime, DateUtil.FORMAT_DATE));
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
        return response("stockCards", stockCards);

    }

}

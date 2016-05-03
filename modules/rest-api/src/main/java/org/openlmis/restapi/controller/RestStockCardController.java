package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.StockCardDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestStockCardService;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Date;
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
                                            @RequestParam(value = "startTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") final Date startTime,
                                            @RequestParam(value = "endTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") final Date endTime) {
        List<StockCardDTO> stockCards;
        try {
            stockCards = restStockCardService.queryStockCardByOccurred(facilityId,startTime,endTime);
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
        return response("stockCards", stockCards);
    }

    // TODO: 5/3/16 add new api to replace this
    @RequestMapping(value = "/rest-api/facilities/{facilityId}/unSyncedStockCards", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity updateStockCardsUpdatedTime(@PathVariable long facilityId, @RequestBody(required = true) List<String> unsyncedStockCardProductCodes) {
        try {
            restStockCardService.updateStockCardSyncTime(facilityId);
        } catch (DataException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }


}

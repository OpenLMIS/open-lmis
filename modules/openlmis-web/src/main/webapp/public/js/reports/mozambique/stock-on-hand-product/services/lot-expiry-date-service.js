services.factory('LotExpiryDateService', function ($http, $q, CubesGenerateUrlService, DateFormatService) {

  function populateLotOnHandInformationForSoonestExpiryDate(dataEntries, lotOnHandHash) {
    var promises = requestExpiringLotOnHandInformation(dataEntries, lotOnHandHash);

    $q.all(promises).then(function () {
      _.forEach(dataEntries, function (dataEntry) {
        if (lotOnHandHash[dataEntry.stock_card_entry_id]) {
          dataEntry.formatted_expiry_date = DateFormatService.formatDateWithLocaleNoDay(lotOnHandHash[dataEntry.stock_card_entry_id].soonest_expiry_date);
          dataEntry.soonest_expiring_loh = lotOnHandHash[dataEntry.stock_card_entry_id].soonest_expiring_loh;
        }
      });
    });
  }

  function requestExpiringLotOnHandInformation(dataEntries, lotOnHandHash) {

    var requests = [];

    var populateLotOnHandInfo = function (lotOnHandEntries) {
      _.forEach(lotOnHandEntries.cells, function (cell) {
        if (cell.total_lotonhand > 0) {
          if ((lotOnHandHash[cell.stock_card_entry_id] && new Date(lotOnHandHash[cell.stock_card_entry_id]) < new Date(cell.expiry_date)) || !lotOnHandHash[cell.stock_card_entry_id]) {
            lotOnHandHash[cell.stock_card_entry_id] = {
              soonest_expiry_date: cell.expiry_date,
              soonest_expiring_loh: cell.total_lotonhand
            };
          }
        }
      });
    };

    //had to do 50 each time because cubes url does not take longer than 500 characters
    var i, j, chunk = 50;
    for (i = 0, j = dataEntries.length; i < j; i += chunk) {
      var entryIds = _.pluck(dataEntries.slice(i, i + chunk), 'stock_card_entry_id');

      var request = $http.get(CubesGenerateUrlService.generateAggregateUrl('vw_lot_expiry_dates', ['stock_card_entry_id', 'expiry_date'], [{
        dimension: "stock_card_entry_id",
        values: entryIds
      }])).success(populateLotOnHandInfo);
      requests.push(request);
    }

    return requests;
  }

  return {
    populateLotOnHandInformationForSoonestExpiryDate: populateLotOnHandInformationForSoonestExpiryDate
  };
});
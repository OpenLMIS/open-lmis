services.factory('LotExpiryDateService', function ($http, $q, CubesGenerateUrlService) {

  function populateLotOnHandInformationForSoonestExpiryDate(dataEntries, lotOnHandHash) {
    var promises = requestExpiringLotOnHandInformation(dataEntries, lotOnHandHash);

    $q.all(promises).then(function () {
      _.forEach(dataEntries, function (dataEntry) {
        dataEntry.soonest_expiring_loh = lotOnHandHash[dataEntry.stock_card_entry_id + " " + dataEntry.expiry_date];
      });
    });
  }

  function requestExpiringLotOnHandInformation(dataEntries, lotOnHandHash) {

    var requests = [];

    var populateLotOnHandInfo = function (lotOnHandEntries) {
      _.forEach(lotOnHandEntries.cells, function (cell) {
        var key = cell.stock_card_entry_id + " " + cell.expiry_date;
        lotOnHandHash[key] = cell.total_lotonhand;
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
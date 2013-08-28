/**
 * Created with IntelliJ IDEA.
 * User: riyakata
 * Date: 8/28/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
function ShipmentFileTemplateController($scope,shipmentFileTemplate){

  $scope.shipmentFileTemplate = shipmentFileTemplate;

}

ShipmentFileTemplateController.resolve = {
  shipmentFileTemplate: function ($q, $timeout, ShipmentFileTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ShipmentFileTemplate.get({}, function (data) {
        deferred.resolve(data.shipment_template);
      }, {});
    }, 100);
    return deferred.promise;
  },

  dateFormats: function($q, $timeout, DateFormats) {
    var deferred = $q.defer();
    $timeout(function () {
      DateFormats.get({}, function (data) {
        deferred.resolve(data.dateFormats);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
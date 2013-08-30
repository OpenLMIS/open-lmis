/**
 * Created with IntelliJ IDEA.
 * User: riyakata
 * Date: 8/28/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
function ShipmentFileTemplateController($scope, shipmentFileTemplate, ShipmentFileTemplate, dateFormats) {

  $scope.shipmentFileTemplate = shipmentFileTemplate;
  $scope.dateFormats = _.pluck(_.where(dateFormats, {"orderDate": true}), "format");

  function isDuplicatePositionPresent() {
    var positionList = _.pluck($scope.shipmentFileTemplate.shipmentFileColumns, "position");
    var uniquePositionList = _.uniq(positionList, true);
    if (uniquePositionList.length != positionList.length) {
      $scope.message = "shipment.file.duplicate.position";
      return false;
    }
    return true;
  }

  $scope.saveShipmentFileTemplate = function () {
    if (!isDuplicatePositionPresent()) {
      return;
    }

    ShipmentFileTemplate.save({}, $scope.shipmentFileTemplate, function (data) {
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $scope.message = '';
          });
        });
      }, 3000);
    });
  };

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

  dateFormats: function ($q, $timeout, DateFormats) {
    var deferred = $q.defer();
    $timeout(function () {
      DateFormats.get({}, function (data) {
        deferred.resolve(data.dateFormats);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
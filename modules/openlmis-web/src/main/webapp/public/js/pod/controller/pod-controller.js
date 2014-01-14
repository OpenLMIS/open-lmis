/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function PODController($scope, OrderPOD, $routeParams) {
  $scope.columns = [
    {label: "header.full.supply", name: "fullSupply"},
    {label: "header.product.code", name: "productCode"},
    {label: "header.product.name", name: "productName"},
    {label: "header.unit.of.issue", name: "dispensingUnit"},
    {label: "header.packs.to.ship", name: "packsToShip"},
    {label: "header.quantity.shipped", name: "quantityShipped"},
    {label: "header.quantity.received", name: "quantityReceived"},
    {label: "header.notes", name: "notes"}
  ];

  if (!$scope.$parent.pod) {
    OrderPOD.get({orderId: $routeParams.orderId}, function (data) {
      $scope.pod = data.orderPOD;
      $scope.order = data.order;
      $scope.requisitionType = $scope.order.emergency ? "requisition.type.emergency" : "requisition.type.regular";
    }, {});
  }

  $scope.isCategorySameAsPreviousLineItem = function (index) {
    return !((index > 0 ) && ($scope.pod.podLineItems[index].productCategory == $scope.pod.podLineItems[index - 1].productCategory));
  };

}

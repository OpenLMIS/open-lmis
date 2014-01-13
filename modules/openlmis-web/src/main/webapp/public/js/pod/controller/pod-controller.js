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
    {label: "Full Supply", name: "fullSupply"},
    {label: "Product Code", name: "productCode"},
    {label: "Product Name", name: "productName"},
    {label: "Unit Of Issue", name: "dispensingUnit"},
    {label: "Packs To Ship", name: "packsToShip"},
    {label: "Quantity Shipped", name: "quantityShipped"},
    {label: "Quantity Received", name: "quantityReceived"},
    {label: "Notes", name: "notes"}
  ];

  if (!$scope.$parent.pod) {
    OrderPOD.get({orderId: $routeParams.orderId}, function (data) {
      $scope.pod = data.orderPOD;
      $scope.order = data.order;
      $scope.requisitionType = $scope.order.emergency ? "requisition.type.emergency" : "requisition.type.regular";
    }, {});
  }

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pod.podLineItems[index].productCategory == $scope.pod.podLineItems[index - 1].productCategory));
  };

}

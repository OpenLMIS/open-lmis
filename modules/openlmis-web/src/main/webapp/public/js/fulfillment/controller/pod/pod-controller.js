/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function PODController($scope, orderPOD, OrderPOD, pageSize, $routeParams, $location, $dialog, $q) {

  $scope.pageSize = pageSize;
  $scope.pod = new ProofOfDelivery(orderPOD.orderPOD);
  $scope.order = orderPOD.order;
  $scope.numberOfPages = Math.ceil($scope.pod.podLineItems.length / $scope.pageSize) || 1;
  $scope.requisitionType = $scope.order.emergency ? "requisition.type.emergency" : "requisition.type.regular";

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

  var refreshPageLineItems = function () {
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = $scope.pod.podLineItems.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
  };
  refreshPageLineItems();

  $scope.$watch('currentPage', function () {
    $location.search('page', $scope.currentPage);
  });

  $scope.$on('$routeUpdate', function () {
    $scope.save();
    refreshPageLineItems();
  });

  $scope.save = function () {
    if (!$scope.podForm.$dirty)
      return;
    OrderPOD.update({id: $routeParams.id}, {podLineItems: $scope.pageLineItems}, function (data) {
      $scope.message = data.success;
      $scope.podForm.$setPristine();
    }, function (response) {
      $scope.error = response.data.error;
    });
  };

  function confirm() {
    var deferred = $q.defer();
    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "msg.question.confirmation"
    };

    OpenLmisDialog.newDialog(options, function (result) {
      result ? deferred.resolve() : deferred.reject();
    }, $dialog);

    return deferred.promise;
  }

  $scope.submit = function () {
    if (($scope.errorPages = $scope.pod.error(pageSize).errorPages))
      return;

    confirm().then(function () {
      OrderPOD.update({id: $routeParams.id, action: 'submit'}, function (data) {
        $scope.message = data.success;
      }, function (response) {
        $scope.error = response.data.error;
      });
    });
  };

  $scope.isCategorySameAsPreviousLineItem = function (index) {
    return !((index > 0 ) && ($scope.pod.podLineItems[index].productCategory == $scope.pod.podLineItems[index - 1].productCategory));
  };
}

PODController.resolve = {

  pageSize: function ($q, $timeout, LineItemsPerPage) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemsPerPage.get({}, function (data) {
        deferred.resolve(data.pageSize);
      }, {});
    }, 100);
    return deferred.promise;
  },

  orderPOD: function ($q, $timeout, OrderPOD, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      OrderPOD.get({id: $route.current.params.id}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  }

};
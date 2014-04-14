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
  $scope.pod.receivedDate = orderPOD.receivedDate;
  $scope.order = orderPOD.order;
  $scope.podAlreadySubmitted = orderPOD.order.alreadyReceived;
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
    {label: "header.quantity.returned", name: "quantityReturned"},
    {label: "header.replaced.product.code", name: "replacedProductCode"},
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
    var saveDefer = $q.defer();
    if (!$scope.podForm.$dirty) {
      saveDefer.resolve();
      return saveDefer.promise;
    }
    OrderPOD.update({id: $routeParams.id}, {deliveredBy: $scope.pod.deliveredBy, receivedBy: $scope.pod.receivedBy,
      receivedDate: $scope.pod.receivedDate, podLineItems: $scope.pageLineItems}, function (data) {
      $scope.message = data.success;
      $scope.error = undefined;
      $scope.podForm.$setPristine();
      saveDefer.resolve();
    }, function (response) {
      $scope.error = response.data.error;
      saveDefer.reject();
    });
    return saveDefer.promise;
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
    $scope.save().then(confirmAndSubmit);

    function confirmAndSubmit() {
      $scope.showSubmitErrors = true;
      if (($scope.errorPages = $scope.pod.error(pageSize).errorPages)) {
        $scope.message = undefined;
        $scope.error = 'error.quantity.received.invalid';
        return;
      }

      confirm().then(function () {
        OrderPOD.update({id: $routeParams.id, action: 'submit'}, function (data) {
          $scope.message = data.success;
          $scope.podAlreadySubmitted = true;
          $scope.error = undefined;
        }, function (response) {
          $scope.error = response.data.error;
        });
      });
    }
  };

  $scope.isCategoryDifferentFromPreviousLineItem = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index - 1].productCategory));
  };

  $scope.cssClassForQuantityReceived = function (quantityReceived) {
    if (!$scope.showSubmitErrors)
      return '';
    return (quantityReceived === null || quantityReceived === undefined) ? 'required-error' : '';
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
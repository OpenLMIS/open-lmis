/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ApproveRnrController($scope, requisitionData, comments, Requisitions, RejectRequisition, rnrColumns, regimenTemplate, equipmentOperationalStatus , $location, pageSize, $routeParams, $dialog, requisitionService, $q) {

  $scope.canApproveRnr = requisitionData.canApproveRnr;
  $scope.rnr = new Rnr(requisitionData.rnr, rnrColumns, requisitionData.numberOfMonths);
  $scope.rnrColumns = rnrColumns;
  $scope.regimenColumns = regimenTemplate ? regimenTemplate.columns : [];
  $scope.pageSize = pageSize;
  $scope.visibleColumns = requisitionService.getMappedVisibleColumns(rnrColumns, RegularRnrLineItem.frozenColumns, []);
  $scope.error = $scope.message = "";
  $scope.regimenCount = $scope.rnr.regimenLineItems.length;
  $scope.equipmentCount = $scope.rnr.equipmentLineItems.length;

  $scope.equipmentOperationalStatus = equipmentOperationalStatus;

  $scope.errorPages = {};
  $scope.shownErrorPages = [];
  $scope.rnrComments = comments;

  var NON_FULL_SUPPLY = 'nonFullSupply';

  requisitionService.populateScope($scope, $location, $routeParams);


  $scope.rejectRnR = function( ){
    var callBack = function (result) {

      if (result) {
        // reject
        RejectRequisition.post({id: $scope.rnr.id}, function(){
          OpenLmisDialog.newDialog({
            id: "confirmDialog",
            header: "label.confirm.action",
            body: 'msg.rnr.returned'
          }, function(){
            $location.url('/public/pages/logistics/rnr/index.html#/init-rnr');
          }, $dialog);
        });
        // redirect to the main page
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "label.rnr.confirm.return"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.saveRnr = function (preventMessage) {
    var deferred = $q.defer();
    if (!$scope.approvalForm || !$scope.approvalForm.$dirty) {
      deferred.resolve();
      return deferred.promise;
    }
    resetFlags();
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id: $scope.rnr.id, operation: "save"},
        rnr, function (data) {
          deferred.resolve();
          if (preventMessage === true) return;
          $scope.message = data.success;
          $scope.error = "";
          $scope.approvalForm.$setPristine();
        }, function (data) {
          deferred.reject();
          $scope.error = data.data.error;
          $scope.message = "";
        });

    return deferred.promise;
  };

  $scope.$on('$routeUpdate', function () {
    requisitionService.refreshGrid($scope, $location, $routeParams, true);
  });

  requisitionService.refreshGrid($scope, $location, $routeParams, true);

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    if ($scope.visibleTab == "Regimen" || !$scope.page[$scope.visibleTab].length) return rnr;

    rnr[$scope.visibleTab + 'LineItems'] = _.map($scope.page[$scope.visibleTab], function (lineItem) {
      return lineItem.reduceForApproval();
    });

    return rnr;
  }

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = true;
    resetFlags();
    requisitionService.resetErrorPages($scope);
    var saveRnrPromise = $scope.saveRnr(true);

    saveRnrPromise.then(function () {
      if (!setError()) confirm();
    });
  };

  function setError() {
    var fullSupplyError = $scope.rnr.validateFullSupplyForApproval();
    var nonFullSupplyError = $scope.rnr.validateNonFullSupplyForApproval();
    $scope.fullSupplyTabError = !!fullSupplyError;
    $scope.nonFullSupplyTabError = !!nonFullSupplyError;


    var error = fullSupplyError || nonFullSupplyError;

    if (error) {
      requisitionService.setErrorPages($scope);
      $scope.error = error;
      $scope.message = '';
    }

    return !!error;
  }

  var confirm = function () {
    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "msg.question.confirmation"
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog);
  };

  $scope.dialogCloseCallback = function (result) {
    if (result)
      approveValidatedRnr();
  };

  var approveValidatedRnr = function () {
    Requisitions.update({id: $scope.rnr.id, operation: "approve"}, {}, function (data) {
      $scope.$parent.message = data.success;
      $scope.error = "";
      $location.path("rnr-for-approval");
    }, function (data) {
      $scope.error = data.data.error;
      $scope.message = "";
    });
  };

  $scope.checkErrorOnPage = function (page) {
    return $scope.visibleTab === NON_FULL_SUPPLY ?
        _.contains($scope.errorPages.nonFullSupply, page) : _.contains($scope.errorPages.fullSupply, page);
  };

  function resetFlags() {
    $scope.error = "";
    $scope.message = "";
  }

}

ApproveRnrController.resolve = {

  requisitionData: function ($q, $timeout, Requisitions, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      Requisitions.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },

  comments: function($q, $timeout, RequisitionComment, $route){
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionComment.get({id: $route.current.params.rnr}, function (data) {
        deferred.resolve(data.comments);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns: function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  pageSize: function ($q, $timeout, LineItemsPerPage) {
    var deferred = $q.defer();
    $timeout(function () {
      LineItemsPerPage.get({}, function (data) {
        deferred.resolve(data.pageSize);
      }, {});
    }, 100);
    return deferred.promise;
  },
  equipmentOperationalStatus: function ($q, $timeout, EquipmentOperationalStatus) {
    var deferred = $q.defer();
    $timeout(function () {
      EquipmentOperationalStatus.get({}, function (data) {
        deferred.resolve(data.status);
      }, {});
    }, 100);
    return deferred.promise;
  },
  regimenTemplate: function ($q, $timeout, $route, ProgramRegimenTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRegimenTemplate.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.template);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

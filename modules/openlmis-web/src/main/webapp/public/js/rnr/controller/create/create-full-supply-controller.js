/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateFullSupplyController($scope, messageService) {
  $scope.currentRnrLineItem = undefined;

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.saveLossesAndAdjustmentsForRnRLineItem = function () {
    $scope.modalError = '';

    if (!$scope.currentRnrLineItem.validateLossesAndAdjustments()) {
      $scope.modalError = messageService.get('error.correct.highlighted');
      return;
    }

    $scope.currentRnrLineItem.reEvaluateTotalLossesAndAdjustments();
    $scope.clearAndCloseLossesAndAdjustmentModal();
  };

  $scope.clearAndCloseLossesAndAdjustmentModal = function () {
    $scope.lossAndAdjustment = undefined;
    $scope.lossesAndAdjustmentsModal = false;

    $('#' + $scope.currentLinkId).focus();
  };

  $scope.resetModalErrorAndSetFormDirty = function () {
    $scope.modalError = '';
    $scope.saveRnrForm.$dirty = true;
  };

  $scope.showLossesAndAdjustments = function (lineItem) {
    $scope.currentRnrLineItem = lineItem;
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal = true;
  };

  $scope.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    $scope.currentRnrLineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.resetModalErrorAndSetFormDirty();
  };

  $scope.addLossAndAdjustment = function (newLossAndAdjustment) {
    $scope.currentRnrLineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.saveRnrForm.$dirty = true;
  };

  $scope.showAddSkippedProductsModal = function(){
    $scope.addSkippedProductsModal = true;
  };

  $scope.unskipProducts = function(rnr){

    var selected = _.where(rnr.skippedLineItems, {unskip: true});
    angular.forEach(selected, function(lineItem, index){
      lineItem.skipped = false;
      $scope.$parent.page[$scope.$parent.visibleTab].push( new RegularRnrLineItem(lineItem, rnr.numberOfMonths, rnr.programRnrColumnList, rnr.status));
    });

    $scope.saveRnrForm.$dirty = true;
    var save = $scope.$parent.saveRnr();

    save.then(function(){
      $scope.$parent.goToPage($scope.$parent.currentPage);
    });

    rnr.skippedLineItems = _.where(rnr.skippedLineItems, {unskip: undefined});
    window.window.adjustHeight();
    // all is said and done, close the dialog box
    $scope.addSkippedProductsModal = false;
  };

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.lossesAndAdjustmentTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}

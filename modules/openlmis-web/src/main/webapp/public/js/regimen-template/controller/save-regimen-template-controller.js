/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SaveRegimenTemplateController($scope, program, programRegimens, regimenColumns, regimenCategories, messageService, Regimens, $location) {

  $scope.program = program;
  $scope.regimens = programRegimens;
  $scope.regimenColumns = regimenColumns;
  $scope.regimenCategories = regimenCategories;
  $scope.selectProgramUrl = "/public/pages/admin/regimen-template/index.html#/select-program";
  $scope.regimensByCategory = [];
  $scope.$parent.message = "";
  $scope.newRegimen = {active: true};


  function addRegimenByCategory(regimen) {
    regimen.editable = false;
    var regimenCategoryId = regimen.category.id;
    var regimenList = $scope.regimensByCategory[regimenCategoryId];
    if (regimenList) {
      regimenList.push(regimen);
    } else {
      regimenList = [regimen];
    }
    $scope.regimensByCategory[regimenCategoryId] = regimenList;
    $scope.error = "";
  }

  function filterRegimensByCategory(regimens) {
    $(regimens).each(function (index, regimen) {
      addRegimenByCategory(regimen);
    });
  }

  filterRegimensByCategory($scope.regimens);

  $scope.addNewRegimen = function () {
    if ($scope.newRegimenForm.$error.required) {
      $scope.inputClass = true;
      $scope.newRegimenError = messageService.get('label.missing.values');
    } else {
      if (checkDuplicateRegimenError($scope.newRegimen)) {
        return;
      }
      $scope.newRegimen.programId = $scope.program.id;
      $scope.newRegimen.displayOrder = 1;
      $scope.newRegimen.editable = false;
      addRegimenByCategory($scope.newRegimen);
      $scope.newRegimenError = null;
      $scope.newRegimen = null;
      $scope.inputClass = false;
      $scope.newRegimen = {active: true};
    }
  };

  function checkDuplicateRegimenError(regimen) {
    var codes = [];
    var regimenCode = regimen.code;
    var duplicateRegimen = false;
    var regimenLists = $scope.getRegimenValuesByCategory();
    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, loopRegimen) {
        if (regimen.$$hashKey != loopRegimen.$$hashKey) {
          codes.push(loopRegimen.code.toLowerCase());
          if (codes.length > 0 && regimenCode != undefined && _.contains(codes, regimenCode.toLowerCase())) {
            $scope.newRegimenError = "";
            $scope.error = messageService.get('error.duplicate.regimen.code');
            duplicateRegimen = true;
            return;
          }
          if (duplicateRegimen) return;
        }
      });
      if (duplicateRegimen) return;
    });
    return duplicateRegimen;
  }

  $scope.getRegimenValuesByCategory = function () {
    return _.values($scope.regimensByCategory);
  }

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && isUndefined(value)) {
      return "required-error";
    }
    return null;
  };

  $scope.saveRow = function (regimen) {
    if (checkDuplicateRegimenError(regimen)) {
      return;
    }

    if (invalidRegimen(regimen)) {
      regimen.doneRegimenError = true;
      return;
    }

    regimen.doneRegimenError = false;
    regimen.editable = false;
    $scope.error = "";
  };

  function invalidRegimen(regimen) {
    if (isUndefined(regimen.code) || isUndefined(regimen.name)) return true;
    return false;
  }

  function checkAllRegimensNotDone() {
    var notDone = false;
    var regimenLists = _.values($scope.regimensByCategory);
    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, loopRegimen) {
        if (loopRegimen.editable) {
          $scope.error = messageService.get('error.regimens.not.done')
          notDone = true;
          return;
        }
      });
      if (notDone) return;
    });
    return notDone;
  }

  $scope.save = function () {

    if (checkAllRegimensNotDone()) {
      return;
    }

    var regimenTemplate = {};
    var regimenListToSave = [];
    var regimenLists = _.values($scope.regimensByCategory);

    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, regimen) {
        regimen.doneRegimenError = undefined;
        regimen.editable = undefined;
        regimen.displayOrder = index + 1;
      });
      regimenListToSave = regimenListToSave.concat(regimenList);
    });
    regimenTemplate.regimens = regimenListToSave;
    regimenTemplate.columns = $scope.regimenColumns;
    Regimens.post({programId: $scope.program.id}, regimenTemplate, function () {
      $scope.$parent.message = messageService.get('regimens.saved.successfully');
      $location.path('select-program');
    }, function () {
    });
  };

}

SaveRegimenTemplateController.resolve = {

  program: function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      Program.get({id: id}, function (data) {
        deferred.resolve(data.program);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  programRegimens: function ($q, ProgramRegimens, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      ProgramRegimens.get({programId: id}, function (data) {
        deferred.resolve(data.regimens);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenColumns: function ($q, RegimenColumns, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RegimenColumns.get({programId: id}, function (data) {
        deferred.resolve(data.regimen_columns);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenCategories: function ($q, RegimenCategories, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RegimenCategories.get({}, function (data) {
        deferred.resolve(data.regimen_categories);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  }

};
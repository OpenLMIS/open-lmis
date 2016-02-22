/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SaveRegimenTemplateController($scope, program, programRegimens, regimenTemplate, regimenCategories, messageService, Regimens, $location) {

  $scope.program = program;
  $scope.regimens = programRegimens;
  $scope.regimenTemplate = regimenTemplate;
  var hiddenColumns = _.filter($scope.regimenTemplate.columns, function (column) {
    return (column.name === 'name' || column.name === 'code' || column.name === 'skipped');
  });
  $scope.regimenTemplate.columns = _.difference($scope.regimenTemplate.columns,hiddenColumns);
  $scope.regimenCategories = regimenCategories;
  $scope.selectProgramUrl = "/public/pages/admin/regimen-template/index.html#/select-program";
  $scope.regimensByCategory = [];
  $scope.$parent.message = "";
  $scope.newRegimen = {active: true};
  $scope.showReportingFields = true;
  $scope.regimensError = false;
  $scope.reportingFieldsError = false;

  function addRegimenByCategory(regimen) {
    if (!$scope.regimensByCategory[regimen.category.id])
      $scope.regimensByCategory[regimen.category.id] = [];
    $scope.regimensByCategory[regimen.category.id].push(regimen);
    $scope.error = "";
  }

  function filterRegimensByCategory(regimens) {
    $scope.regimensByCategory = _.groupBy(regimens, function (regimen) {
      return regimen.category.id;
    });
  }

  filterRegimensByCategory($scope.regimens);

  $scope.addNewRegimen = function () {
    if (invalidRegimen($scope.newRegimen)) {
      $scope.inputClass = true;
      $scope.newRegimenError = messageService.get('label.missing.values');
      $scope.regimensError = true;
    } else {
      if (!valid($scope.newRegimen)) {
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

  function valid(regimen) {
    var regimens = _.reject(_.flatten(_.values($scope.regimensByCategory)), function (regimen1) {
      return regimen1.$$hashKey == regimen.$$hashKey;
    });
    if (_.findWhere(regimens, {code: regimen.code})) {
      $scope.newRegimenError = "";
      $scope.error = messageService.get('error.duplicate.regimen.code');
      return false;
    }
    return true;
  }

  $scope.getRegimenValuesByCategory = function () {
    return _.values($scope.regimensByCategory);
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && isUndefined(value)) {
      return "required-error";
    }
    return null;
  };

  $scope.saveRow = function (regimen) {
    if (!valid(regimen)) {
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
    if (isUndefined(regimen.category) || isUndefined(regimen.code) || isUndefined(regimen.name)) {
      $scope.regimensError = true;
      return true;
    }
    return false;
  }

  function checkAllRegimensNotDone() {
    var notDone = false;
    var regimenLists = _.values($scope.regimensByCategory);
    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, loopRegimen) {
        if (loopRegimen.editable) {
          $scope.regimensError = true;
          $scope.error = messageService.get('error.regimens.not.done');
          notDone = true;
          return;
        }
      });
      if (notDone) return;
    });
    return notDone;
  }

  function validReportingFields() {

    var DEFAULT_VISIBLE_COUNT = 0;

    if (_.find($scope.regimenTemplate.columns, function (column) {
      return isUndefined(column.label);
    })) {
      $scope.reportingFieldsError = true;
      $scope.error = messageService.get('error.regimen.null.label');
      return;
    }

    var count = _.countBy($scope.regimenTemplate.columns, function (column) {
      return column.visible ? 'visible' : 'invisible';
    });

    if (count.visible === undefined || count.visible === DEFAULT_VISIBLE_COUNT) {
      $scope.reportingFieldsError = true;
      $scope.error = messageService.get('error.regimens.none.selected');
      return;
    }
    return true;
  }

  $scope.save = function () {

    if (checkAllRegimensNotDone() || !validReportingFields()) {
      return;
    }

    var regimenForm = {};
    var regimenListToSave = [];

    $(_.flatten(_.values($scope.regimensByCategory))).each(function (index, regimen) {
      regimen.displayOrder = index + 1;
      regimenListToSave.push(regimen);
    });
    regimenForm.regimens = regimenListToSave;

    $(_.flatten($scope.regimenTemplate.columns)).each(function (index, column) {
      column.displayOrder = index + 3;
    });
    regimenForm.regimenColumnList = _.union(hiddenColumns,$scope.regimenTemplate.columns);
    Regimens.save({programId: $scope.program.id}, regimenForm, function () {
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

  regimenTemplate: function ($q, RegimenTemplate, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RegimenTemplate.get({programId: id}, function (data) {
        deferred.resolve(data.template);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenCategories: function ($q, RegimenCategories, $location, $route, $timeout) {
    var deferred = $q.defer();
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
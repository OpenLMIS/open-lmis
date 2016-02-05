/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


function FacilityController($scope, facilityReferenceData, $routeParams, facility, Facility, demographicCategories, $location, FacilityProgramProductsISA, priceSchedules, $dialog, messageService, interfacesReferenceData)
{
  $scope.$parent.facilityId = null;
  $scope.message = "";
  $scope.$parent.message = "";
  $scope.isaService = FacilityProgramProductsISA; //isaService is used by ISACoefficientsModalController, which is intended to be used as a descendant controller of this one.
  initialize();

  $scope.demographicCategories = demographicCategories; //Will be undefined if we aren't in VIMS


  function initialize() {
    $scope.facilityTypes = facilityReferenceData.facilityTypes;
    $scope.geographicZones = facilityReferenceData.geographicZones;
    $scope.facilityOperators = facilityReferenceData.facilityOperators;
    $scope.programs = facilityReferenceData.programs;
    $scope.priceSchedules = priceSchedules;
    $scope.interfaces = interfacesReferenceData;
    if ($routeParams.facilityId) {
      $scope.facility = getFacilityWithDateObjects(facility);
      $scope.originalFacilityCode = facility.code;
      $scope.originalFacilityName = facility.name;
      $scope.isEdit = true;
      updateProgramsToDisplay();
      updateInterfacesToDisplay();
    } else {
      $scope.facility = {};
      updateProgramsToDisplay();
      updateInterfacesToDisplay();
      $scope.facility.enabled = true;
    }
    $scope.facilityProgramProductsList = [];
  }

  function convertStringToCorrectDateFormat(stringDate) {
    if (stringDate) {
      return stringDate.split("-").reverse().join("-");
    }
    return null;
  }

  function getFacilityWithDateObjects(facility) {
    angular.forEach(facility.supportedPrograms, function (supportedProgram) {
      if (supportedProgram.startDate) {
        supportedProgram.startDate = supportedProgram.stringStartDate;
      }
    });

    facility.goLiveDate = convertStringToCorrectDateFormat(facility.stringGoLiveDate);
    facility.goDownDate = convertStringToCorrectDateFormat(facility.stringGoDownDate);

    return facility;
  }

  $scope.showISAEditModal = function (supportedProgram) {
    $scope.currentProgram = supportedProgram.program;
    $scope.$broadcast('showISAEditModal');
  };

  $scope.cancel = function () {
    $location.path('#/search');
  };

  $scope.saveFacility = function()
  {
    if ($scope.facilityForm.$error.pattern || $scope.facilityForm.$error.required)
    {
      $scope.showError = "true";
      $scope.error = 'form.error';
      $scope.message = "";
      return;
    }


    var facilitySaveCallback = function(data)
    {
      $scope.showError = "true";
      $scope.error = "";
      $scope.errorProgram = "";
      $scope.$parent.message = data.success;
      $scope.facility = getFacilityWithDateObjects(data.facility);
      $scope.$parent.facilityId = $scope.facility.id;
      $location.path('');
    };


    if (!$scope.isEdit) {
      Facility.save({}, $scope.facility, facilitySaveCallback, errorFunc);
    } else {
      Facility.update({id: $scope.facility.id}, $scope.facility, facilitySaveCallback, errorFunc);
    }
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.addSupportedProgram = function (supportedProgram) {
    if (supportedProgram.active && !supportedProgram.editedStartDate) {
      $scope.showDateNotEnteredError = true;
      return;
    }
    supportedProgram.program = getProgramById(supportedProgram.program.id);
    $scope.facility.supportedPrograms.push(supportedProgram);
    $scope.showDateNotEnteredError = false;
    $scope.supportedProgram = undefined;
    updateProgramsToDisplay();
  };

  $scope.showConfirmDateChangeWindow = function (program) {
    window.program = program;
    if (getProgramById(program.program.id).push) {
      $scope.dateChangeCallback(true);
      return;
    }
    var dialogOpts = {
      id: "dateChangeConfirmModal",
      header: 'message.setProgramStartDate',
      body: 'message.dateChangeConfirmMessage'
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.dateChangeCallback, $dialog);
  };

  $scope.dateChangeCallback = function (result) {
    if (result) {
      window.program.startDate = window.program.editedStartDate;
    } else {
      window.program.editedStartDate = window.program.startDate;
    }
  };

  $scope.showRemoveProgramConfirmDialog = function (supportedProgram) {
    $scope.selectedSupportedProgram = supportedProgram;
    var options = {
      id: "removeProgramConfirmDialog",
      header: 'delete.facility.program.header',
      body: messageService.get('delete.facility.program.confirm', $scope.selectedSupportedProgram.program.name)
    };
    OpenLmisDialog.newDialog(options, $scope.removeSupportedProgramConfirm, $dialog);
  };

  $scope.removeSupportedProgramConfirm = function (result) {
    if (result) {
      $scope.removeSupportedProgram();
    }
    $scope.selectedSupportedProgram = undefined;
  };

  $scope.removeSupportedProgram = function () {
    $scope.facility.supportedPrograms = _.without($scope.facility.supportedPrograms, $scope.selectedSupportedProgram);
    updateProgramsToDisplay();
  };


  function getProgramById(id) {
    return (_.findWhere($scope.programs, {'id': id}));
  }

  var successFunc = function (data) {
    $scope.showError = "true";
    $scope.error = "";
    $scope.message = data.success;
    $scope.facility = getFacilityWithDateObjects(data.facility);
    $scope.originalFacilityCode = data.facility.code;
    $scope.originalFacilityName = data.facility.name;
  };

  var errorFunc = function (data) {
    $scope.showError = "true";
    $scope.message = "";
    $scope.error = data.data.error;
  };

  $scope.disableFacilityCallBack = function (result) {
    if (!result) return;
    Facility.remove({id: $scope.facility.id}, {}, successFunc, errorFunc);
  };

  $scope.showConfirmFacilityDisableWindow = function () {
    var dialogOpts = {
      id: "disableFacilityDialog",
      header: 'disable.facility.header',
      body: messageService.get('disable.facility.confirm', $scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.disableFacilityCallBack, $dialog);
  };

  $scope.showConfirmFacilityEnable = function () {
    var dialogOpts = {
      id: "enableConfirmModal",
      header: "create.facility.enableFacility",
      body: "'{0}' / '{1}' will be enabled in the system.".format($scope.originalFacilityName, $scope.originalFacilityCode)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.enableFacilityCallBack, $dialog);
  };

  $scope.enableFacilityCallBack = function (result) {
    if (!result) return;
    Facility.restore({id: $scope.facility.id}, successFunc, errorFunc);
  };

  function updateProgramsToDisplay() {
    $scope.facility.supportedPrograms = $scope.facility.supportedPrograms || [];
    var supportedProgramIds = _.pluck(_.pluck($scope.facility.supportedPrograms, 'program'), "id");
    $scope.programsToDisplay = _.reject($scope.programs, function (supportedProgram) {
      return _.contains(supportedProgramIds, supportedProgram.id);
    });
    $scope.programSupportedMessage = ($scope.programsToDisplay.length) ? 'label.select.program.supported' : 'label.no.programs.left';
  }

  function updateInterfacesToDisplay() {
    $scope.facility.interfaceMappings = $scope.facility.interfaceMappings || [];
    var interfaceIds = _.pluck(_.pluck($scope.facility.interfaceMappings, 'interfaceId'), "id");
    $scope.interfacesToDisplay = _.reject($scope.interfaces, function (_interface) {
      return _.contains(interfaceIds, _interface.id);
    });
    $scope.interfaceSelectMessage = ($scope.interfacesToDisplay.length) ? 'label.select.interface' : 'label.no.interface.left';
  }

  $scope.addInterfaceMapping = function(mapping){
    if(!mapping.interfaceId){
      $scope.showInterfaceRequiredError = true;
      return;
    }
    if(!mapping.mappedId){
      $scope.showMappingIdRequiredError = true;
      return;
    }
    $scope.showInterfaceRequiredError = false;
    $scope.showMappingIdRequiredError = false;

    mapping.interfaceId = getInterfaceById(mapping.interfaceId);
    $scope.facility.interfaceMappings.push(mapping);
    $scope.interfaceMapping = undefined;
    updateInterfacesToDisplay();
  };

  function getInterfaceById(interfaceId){
    return (_.findWhere($scope.interfaces, {'id': interfaceId}));
  }

  $scope.showRemoveInterfaceMappingConfirmDialog = function (interfaceMapping) {
    $scope.selectedInterfaceMapping = interfaceMapping;
    var options = {
      id: "removeInterfaceMappingConfirmDialog",
      header: 'delete.interface.mapping.header',
      body: messageService.get('delete.facility.interface.mapping.confirm', $scope.selectedInterfaceMapping.interfaceId.name)
    };
    OpenLmisDialog.newDialog(options, $scope.removeInterfaceMappingConfirm, $dialog);
  };

  $scope.removeInterfaceMappingConfirm = function(result){
    if (result) {
      $scope.facility.interfaceMappings = _.without($scope.facility.interfaceMappings, $scope.selectedInterfaceMapping);
    }
    $scope.selectedInterfaceMapping = undefined;
    updateInterfacesToDisplay();
  };
}

FacilityController.resolve = {
  facilityReferenceData: function ($q, $timeout, FacilityReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilityReferenceData.get({}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facility: function ($q, $timeout, Facility, $route) {
    if ($route.current.params.facilityId === undefined) return undefined;

    var deferred = $q.defer();
    var facilityId = $route.current.params.facilityId;

    $timeout(function () {
      Facility.get({id: facilityId}, function (data) {
        deferred.resolve(data.facility);
      }, {});
    }, 100);
    return deferred.promise;
  },

  priceSchedules: function ($q, $route, $timeout, PriceScheduleCategories) {
    var deferred = $q.defer();
    $timeout(function () {
      PriceScheduleCategories.get({}, function (data) {
        deferred.resolve(data.priceScheduleCategories);
      }, {});
    }, 100);
    return deferred.promise;
  },

  interfacesReferenceData : function ($q, $route, $timeout, ELMISInterface) {
    var deferred = $q.defer();

    $timeout(function () {
      ELMISInterface.getInterfacesReference().get({}, function (data) {
        deferred.resolve(data.activeInterfaces);
      }, {});
    }, 100);

    return deferred.promise;
  }
};

//Begin: Specific for Tanzania
/*  The code below is intended to illustrate one potential way of conditionally injecting demographic-category data
    For now, because we don’t have a way to conditionally toggle OpenLMIS’ features on and off, we simple set injectDemographyCategories to true. */
var injectDemographyCategories = true;
if(injectDemographyCategories)
{
  FacilityController.resolve.demographicCategories = function ($q, $route, $timeout, DemographicEstimateCategories)
  {
    var deferred = $q.defer();
    $timeout(function () {
      DemographicEstimateCategories.get({}, function(data)
      {
        //Add 'Facility Population' to the set of available categories
        var categories = data.estimate_categories;
        var facilityCatchmentPopulation = {'id': 0, 'name': 'Facility Catchment Population'};
        categories.unshift(facilityCatchmentPopulation);
        deferred.resolve(categories);
      }, {});
    }, 100);
    return deferred.promise;
  };
}
else //As suggested in the comments above, this else-clause is intended to run for non-Tanzanian countries.
{
  //demographicEstimateCategories has to be assigned something...
  FacilityController.resolve.demographicCategories = function($timeout)
  {
    //...so set it to a $timeout which returns a promise that will be resolved
    return $timeout
    (
        function() {},
        5
    );
  };
}

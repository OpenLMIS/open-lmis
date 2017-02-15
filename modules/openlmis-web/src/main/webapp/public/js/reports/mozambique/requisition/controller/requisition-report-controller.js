function RequisitionReportController($scope, $controller, RequisitionReportService, messageService, DateFormatService, FeatureToggleService, $window, $cacheFactory, ReportExportExcelService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.location = '';
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.selectedItems = [];

  function filterRequisitionsBasedOnGeographicZones() {
    if ($scope.reportParams.selectedFacility) {
      $scope.requisitions = _.filter($scope.allRequisitions, function (requisition) {
        return requisition.facilityName === $scope.reportParams.selectedFacility.name &&
          requisition.districtName === $scope.reportParams.selectedDistrict.name &&
          requisition.provinceName === $scope.reportParams.selectedProvince.name;
      });
    } else if ($scope.reportParams.selectedDistrict) {
      $scope.requisitions = _.filter($scope.allRequisitions, function (requisition) {
        return requisition.districtName === $scope.reportParams.selectedDistrict.name &&
          requisition.provinceName === $scope.reportParams.selectedProvince.name;
      });
    } else if ($scope.reportParams.selectedProvince) {
      $scope.requisitions = _.filter($scope.allRequisitions, function (requisition) {
        return requisition.provinceName === $scope.reportParams.selectedProvince.name;
      });
    } else {
      $scope.requisitions = $scope.allRequisitions;
    }
  }

  $scope.loadRequisitions = function () {
    var requisitionQueryParameters = {
      startTime: $scope.reportParams.startTime + " 00:00:00",
      endTime: $scope.reportParams.endTime + " 11:59:59"
    };

    RequisitionReportService.get(requisitionQueryParameters, function (data) {
      $scope.allRequisitions = data.rnr_list;
      filterRequisitionsBasedOnGeographicZones();
      setInventoryDateAndSubmittedStatus();
      renameRequisitionType();
    });
  };

  $scope.loadReport = function () {
    $scope.locationIdToCode($scope.reportParams);
    $scope.loadRequisitions();
  };

  var setInventoryDateAndSubmittedStatus = function () {
    _.each($scope.allRequisitions, function (rnr) {
      if (rnr.actualPeriodEnd === null) {
        rnr.actualPeriodEnd = rnr.schedulePeriodEnd;
      }

      if (rnr.clientSubmittedTime !== null) {
        var FIVE_DAYS = 5 * 24 * 60 * 60 * 1000;
        if (rnr.clientSubmittedTime <= rnr.schedulePeriodEnd + FIVE_DAYS) {
          rnr.submittedStatus = messageService.get("rnr.report.submitted.status.ontime");
        } else {
          rnr.submittedStatus = messageService.get("rnr.report.submitted.status.late");
        }
      }

      rnr.inventoryDate = formatDate(rnr.actualPeriodEnd);
    });
  };

  var renameRequisitionType = function () {
    _.each($scope.allRequisitions, function (rnr) {
      if (rnr.type === "Normal") {
        rnr.type = messageService.get("label.requisition.type.normal");
      }
      if (rnr.type === "Emergency") {
        rnr.type = messageService.get("label.requisition.type.emergency");
      }
    });
  };

  $scope.isSubmitLate = function (status) {
    return messageService.get("rnr.report.submitted.status.late") === status;
  };

  $scope.filterOptions = {
    filterText: '',
    useExternalFilter: false
  };

  $scope.rnrListGrid = {
    data: 'requisitions',
    displayFooter: false,
    multiSelect: false,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: redirectPage,
    displaySelectionCheckbox: false,
    enableColumnResize: true,
    showColumnMenu: false,
    enableSorting: true,
    plugins: [new ngGridFlexibleHeightPlugin()],
    sortInfo: {fields: ['webSubmittedTimeString'], directions: ['desc']},
    enableFiltering: true,
    filterOptions: $scope.filterOptions,
    showFilter: false,
    columnDefs: [
      {
        displayName: messageService.get("label.report.requisitions.number"),
        cellTemplate: '<div class="customCell">{{$parent.$index + 1}}</div>',
        sortable: false
      },
      {field: 'programName', displayName: messageService.get("label.report.requisitions.programname")},
      {field: 'type', displayName: messageService.get("label.report.requisitions.type")},
      {
        field: 'facilityName',
        displayName: messageService.get("label.report.requisitions.facilityname"),
        width: 200
      },
      {field: 'submittedUser', displayName: messageService.get("label.report.requisitions.submitteduser")},
      {field: 'inventoryDate', displayName: messageService.get("label.report.requisitions.inventorydate")},
      {
        field: 'submittedStatus',
        displayName: messageService.get("label.report.requisitions.submittedstatus"),
        cellTemplate: '<div class="customCell" ng-class="{submitStatusLate: isSubmitLate(row.getProperty(col.field))}">{{row.getProperty(col.field)}}</div>'
      },
      {
        field: 'clientSubmittedTimeString',
        displayName: messageService.get("label.report.requisitions.submittedtime")
      },
      {field: 'webSubmittedTimeString', displayName: messageService.get("label.report.requisitions.syncedtime")}
    ]
  };

  function formatDate(date) {
    return DateFormatService.formatDateWithLocale(date);
  }

  $scope.getRedirectUrl = function () {
    var url = "/public/pages/logistics/rnr/index.html#/";
    var urlMapping = {
      "VIA": "view-requisition-via/",
      "VIA Classica": "view-requisition-via/",
      "ESS_MEDS": "view-requisition-via/",
      "MMIA": "view-requisition-mmia/"
    };

    var selectedItem = $scope.selectedItems[0];
    if (selectedItem.programName) {
      url = url + urlMapping[selectedItem.programName];
    }

    url += selectedItem.id + "?supplyType=fullSupply&page=1";
    return url;
  };

  function redirectPage() {
    FeatureToggleService.get({key: "redirect.view.rnr.page"}, function (result) {
      if (result.key) {
        $window.location.href = $scope.getRedirectUrl();
      }
    });
  }

  if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
    $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
  }
  if ($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined) {
    $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReport', "no");
    $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReportForSingleProduct', "no");
  }

  $scope.exportXLSX = function () {
    var data = {
      reportHeaders: {
        programName: messageService.get('report.header.program.name'),
        type: messageService.get('report.header.type'),
        //get parent Node in later story
        facilityName: messageService.get('report.header.facility.name'),
        submittedUser: messageService.get('report.header.submitted.user'),
        inventoryDate: messageService.get('report.header.inventory.date'),
        submittedStatus: messageService.get('report.header.submitted.status'),
        submittedTime: messageService.get('report.header.submitted.time'),
        syncTime: messageService.get('report.header.sync.time')
      },
      reportContent: []
    };

    if ($scope.requisitions) {
      $scope.requisitions.forEach(function (requisition) {
        var requisitionContent = {};
        requisitionContent.programName = requisition.programName;
        requisitionContent.type = requisition.type;
        //get Parent Node in later story: get info in backend service: approverService.getNextApprovers(requisition.getId())
        requisitionContent.facilityName = requisition.facilityName;
        requisitionContent.submittedUser = requisition.submittedUser;
        requisitionContent.inventoryDate = requisition.inventoryDate;
        requisitionContent.submittedStatus = requisition.submittedStatus;
        requisitionContent.submittedTime = requisition.clientSubmittedTimeString;
        requisitionContent.syncTime = requisition.webSubmittedTimeString;
        data.reportContent.push(requisitionContent);
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.requisition.report'));
    }
  };
}
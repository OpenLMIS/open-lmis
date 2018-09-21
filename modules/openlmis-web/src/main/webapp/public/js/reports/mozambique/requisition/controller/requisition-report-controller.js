function RequisitionReportController($scope, $controller, RequisitionReportService, messageService, DateFormatService, FeatureToggleService, $window, $cacheFactory, ReportExportExcelService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.location = '';
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.selectedItems = [];

  $scope.loadRequisitions = function () {
    var reportParams = $scope.reportParams;

    var requisitionQueryParameters = {
      startTime: reportParams.startTime + " 00:00:00",
      endTime: reportParams.endTime + " 23:59:59",
      provinceId: reportParams.provinceId.toString(),
      districtId: reportParams.districtId.toString(),
      facilityId: reportParams.facilityId.toString()
    };

    RequisitionReportService.get(_.pick(requisitionQueryParameters, function (parameter) {
      return !_.isEmpty(parameter.trim());
    }), function (data) {
      $scope.requisitions = data.rnr_list;
      formatRequisitionList();
    });
  };

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict() && $scope.validateFacility()) {
      $scope.locationIdToCode($scope.reportParams);
      $scope.loadRequisitions();
    }
  };

  var setSubmittedStatus = function (rnr) {
    if (rnr.clientSubmittedTime !== null) {
      var FIVE_DAYS = 5 * 24 * 60 * 60 * 1000;
      if (rnr.clientSubmittedTime <= rnr.schedulePeriodEnd + FIVE_DAYS) {
        rnr.submittedStatus = messageService.get("rnr.report.submitted.status.ontime");
      } else {
        rnr.submittedStatus = messageService.get("rnr.report.submitted.status.late");
      }
    }

    if (!rnr.clientSubmittedTime) {
      rnr.submittedStatus = messageService.get("rnr.report.submitted.status.notsubmitted");
    }
  };

  var formatRequisitionList = function () {
    _.each($scope.requisitions, function (rnr) {
      if (rnr.actualPeriodEnd === null) {
        rnr.actualPeriodEnd = rnr.schedulePeriodEnd;
      }
      setSubmittedStatus(rnr);
      setOriginalPeriodString(rnr);
      renameRequisitionType(rnr);

      rnr.inventoryDate = formatDate(rnr.actualPeriodEnd);
    });
  };

  var setOriginalPeriodString = function (rnr) {
    if (rnr.schedulePeriodStart && rnr.schedulePeriodEnd) {
      var schedulePeriodStartString = formatDate(rnr.schedulePeriodStart);
      var schedulePeriodEndString = formatDate(rnr.schedulePeriodEnd);
      rnr.originalPeriodString = schedulePeriodStartString + ' - ' + schedulePeriodEndString;
    } else {
      rnr.originalPeriodString = "";
    }
  };

  var renameRequisitionType = function (rnr) {
    if (rnr.type === "Normal") {
      rnr.type = messageService.get("label.requisition.type.normal");
    }
    if (rnr.type === "Emergency") {
      rnr.type = messageService.get("label.requisition.type.emergency");
    }
  };

  $scope.isSubmitLate = function (status) {
    return messageService.get("rnr.report.submitted.status.late") === status;
  };

  // Todo: when the via classic program name change, this logic need to change. to be continue...
  $scope.programNameFormatter = function (programName) {
    if (programName === "VIA Classica") {
      return messageService.get("label.report.requisitions.programname.balancerequisition");
    }

    return programName;
  };

  $scope.submittedTimeFormatter = function (submittedTime, submittedTimeString) {
    return submittedTime ? submittedTimeString : "";
  };

  $scope.originalPeriodFormatter = function (originalPeriodStartDate, originalPeriodEndDate) {
    if (originalPeriodStartDate && originalPeriodEndDate) {
      return originalPeriodStartDate + " - " + originalPeriodEndDate;
    }

    return "";
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
        sortable: false,
        width: 100
      },
      {
        field: 'programName',
        displayName: messageService.get("label.report.requisitions.programname"),
        cellTemplate: '<div class="customCell">{{programNameFormatter(row.entity.programName)}}</div>',
      },
      {
        field: 'type',
        displayName: messageService.get("label.report.requisitions.type"),
        width: 100
      },
      {
        field: 'facilityName',
        displayName: messageService.get("label.report.requisitions.facilityname"),
        width: 150
      },
      {
        field: 'submittedUser',
        displayName: messageService.get("label.report.requisitions.submitteduser")
      },
      {
        field: 'inventoryDate',
        displayName: messageService.get("label.report.requisitions.inventorydate"),
        sortFn: function (currentDateString, previousDateString) {
          var currentDate = new Date(currentDateString);
          var previousDate = new Date(previousDateString);

          if (currentDate === previousDate) return 0;
          if (currentDate < previousDate) return -1;
          return 1;
        }
      },
      {
        field: 'submittedStatus',
        displayName: messageService.get("label.report.requisitions.submittedstatus"),
        cellTemplate: '<div class="customCell" ng-class="{submitStatusLate: isSubmitLate(row.getProperty(col.field))}">{{row.getProperty(col.field)}}</div>'
      },
      {
        field: 'originalPeriodString',
        displayName: messageService.get("label.report.requisitions.originalperiod"),
        width: 180,
        sortFn: function (currentOriginalPeriodString, previousOriginalPeriodString) {
          var currentDate = new Date(currentOriginalPeriodString.split("-")[0]);
          var previousDate = new Date(previousOriginalPeriodString.split("-")[0]);

          if (currentDate === previousDate) return 0;
          if (currentDate < previousDate) return -1;
          return 1;
        }
      },
      {
        field: 'clientSubmittedTimeString',
        displayName: messageService.get("label.report.requisitions.submittedtime"),
        cellTemplate: '<div class="customCell">{{submittedTimeFormatter(row.entity.clientSubmittedTime, row.entity.clientSubmittedTimeString)}}</div>'
      },
      {
        field: 'webSubmittedTimeString',
        displayName: messageService.get("label.report.requisitions.syncedtime"),
        cellTemplate: '<div class="customCell">{{submittedTimeFormatter(row.entity.webSubmittedTime, row.entity.webSubmittedTimeString)}}</div>'
      }
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

  function redirectPage(row) {
    var requisition = row.entity;
    if (requisition && !requisition.webSubmittedTime) {
      return;
    }

    FeatureToggleService.get({key: "redirect.view.rnr.page"}, function (result) {
      if (result.key) {
        $window.location.href = $scope.getRedirectUrl();
      }
    });
  }

  if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
    $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
  }
  if ($cacheFactory.get('stockOutReportParams') !== undefined) {
    $cacheFactory.get('stockOutReportParams').put('shouldLoadStockOutReportAllProductsFromCache', "no");
    $cacheFactory.get('stockOutReportParams').put('shouldLoadStockOutReportSingleProductFromCache', "no");
  }

  $scope.exportXLSX = function () {
    var data = {
      reportTitles: [
        messageService.get('report.header.generated.for'),
        DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.startTime) + ' - ' +
        DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.endTime)
      ],
      reportHeaders: {
        programName: messageService.get('report.header.program.name'),
        type: messageService.get('report.header.type'),
        provinceName: messageService.get('report.header.province'),
        districtName: messageService.get('report.header.district'),
        facilityName: messageService.get('report.header.facility.name'),
        submittedUser: messageService.get('report.header.submitted.user'),
        inventoryDate: messageService.get('report.header.inventory.date'),
        submittedStatus: messageService.get('report.header.submitted.status'),
        originalPeriodDate: messageService.get('report.header.originalperiod.date'),
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
        requisitionContent.provinceName = requisition.provinceName;
        requisitionContent.districtName = requisition.districtName;
        requisitionContent.facilityName = requisition.facilityName;
        requisitionContent.submittedUser = requisition.submittedUser;
        requisitionContent.inventoryDate = {
          value: requisition.actualPeriodEnd,
          dataType: 'date',
          style: {
            dataPattern: 'yyyy-MM-dd'
          }
        };
        requisitionContent.submittedStatus = requisition.submittedStatus;
        requisitionContent.originalPeriodDate = requisition.originalPeriodString;
        requisitionContent.submittedTime = {
          value: requisition.clientSubmittedTime,
          dataType: 'date',
          style: {
            dataPattern: 'yyyy-MM-dd'
          }
        };
        requisitionContent.syncTime = {
          value: requisition.webSubmittedTime,
          dataType: 'date',
          style: {
            dataPattern: 'yyyy-MM-dd'
          }
        };
        data.reportContent.push(requisitionContent);
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.requisition.report'));
    }
  };
}
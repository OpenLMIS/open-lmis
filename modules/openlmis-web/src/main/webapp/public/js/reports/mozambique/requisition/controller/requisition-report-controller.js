function RequisitionReportController($scope, $controller, RequisitionReportService,
     messageService, DateFormatService, FeatureToggleService, $window, $cacheFactory,
     ReportExportExcelService, ProgramService, UnitService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.location = '';
  var SELECTION_CHECKBOX_NOT_SELECT_STYLES = 'selection-checkbox__not-select';
  var SELECTION_CHECKBOX_ALL_STYLES = 'selection-checkbox__all';
  var SELECTION_CHECKBOX_HALF_STYLES = 'selection-checkbox__half';

  $scope.$on('$viewContentLoaded', function () {
    loadRequisitionPrograms();
    $scope.loadHealthFacilities();
    $scope.selectedProgramNames = [];
    $scope.selectedProgramIds = [];
    $scope.showProgramList = false;
    $scope.selectedAll = false;
    $scope.allProgramIds = [];
    $scope.selectedProgramClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
    $scope.selectedProgramAllClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
  });

  $scope.closeProgramListDialog = function () {
    $scope.showProgramList = false;
  };

  $scope.clickProgramSelection = function () {
    $scope.showProgramList = !$scope.showProgramList;
  };

  $scope.selectALL = function () {
    $scope.selectedAll = !$scope.selectedAll;

    if ($scope.selectedAll) {
      $scope.selectedProgramAllClass = SELECTION_CHECKBOX_ALL_STYLES;
      $scope.selectedProgramClass = _.map($scope.requisitionPrograms, function (program) {
        program.isSelected = true;
        return program;
      });
      $scope.selectedProgramIds = _.map($scope.requisitionPrograms, function (program) {
        return program.id;
      });

      $scope.selectedProgramNames = [messageService.get('report.option.all')];

    } else {
      $scope.selectedProgramClass = _.map($scope.requisitionPrograms, function (program) {
        program.isSelected = false;
        return program;
      });
      $scope.selectedProgramAllClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
      $scope.selectedProgramIds = [];

      $scope.selectedProgramNames = [];
    }
  };

  $scope.selectProgram = function (programId) {
    $scope.requisitionPrograms = _.map($scope.requisitionPrograms, function (program) {
      if (programId === program.id) {
        program.isSelected = !program.isSelected;
        updateSelectedProgramIdsAndNames(program);
      }
      return program;
    });
  };

  var updateSelectedProgramIdsAndNames = function (program) {
    if (program.isSelected) {
      $scope.selectedProgramIds.push(program.id);
      $scope.selectedProgramNames.push(program.name);
    } else {
      $scope.selectedProgramIds = _.filter($scope.selectedProgramIds, function (programId) {
        return programId !== program.id;
      });

      $scope.selectedProgramNames = _.filter($scope.selectedProgramNames, function (programName) {
        return programName !== program.name;
      });
    }
    updateSelectAllOptionStyle();
  };

  var updateSelectAllOptionStyle = function () {
    var selectedProgramIds = $scope.selectedProgramIds;
    var allProgramIds = $scope.allProgramIds;
    var intersectionIds = _.intersection(allProgramIds, selectedProgramIds);

    if (intersectionIds.length === allProgramIds.length) {
      $scope.selectedProgramAllClass = SELECTION_CHECKBOX_ALL_STYLES;
      $scope.selectedAll = true;
      $scope.selectedProgramNames = [messageService.get('report.option.all')];
    }

    if (intersectionIds.length > 0 && intersectionIds.length < allProgramIds.length) {
      $scope.selectedProgramAllClass = SELECTION_CHECKBOX_HALF_STYLES;
      $scope.selectedAll = false;
      $scope.selectedProgramNames = _.filter(_.map($scope.requisitionPrograms, function (program) {
        if (_.include(selectedProgramIds, program.id)) {
          return program.name;
        }
      }));
    }

    if (intersectionIds.length === 0) {
      $scope.selectedProgramAllClass = SELECTION_CHECKBOX_NOT_SELECT_STYLES;
      $scope.selectedAll = false;
      $scope.selectedProgramNames = [];
    }
  };

  var loadRequisitionPrograms = function () {
    ProgramService.loadRequisitionPrograms().get({}, function (data) {
      $scope.requisitionPrograms = _.map(data['requisition-programs'], function (program) {
        return {
          id: program.id,
          name: UnitService.programNameFormatter(program.name),
          isSelected: false
        };
      });

      setAllProgramIds();
    });
  };

  var setAllProgramIds = function () {
    $scope.allProgramIds = _.map($scope.requisitionPrograms, function (program) {
      return program.id;
    });
  };

  $scope.selectedItems = [];

  $scope.loadRequisitions = function () {
    var reportParams = $scope.reportParams;

    var requisitionQueryParameters = {
      startTime: reportParams.startTime + " 00:00:00",
      endTime: reportParams.endTime + " 23:59:59",
      provinceId: reportParams.provinceId.toString(),
      districtId: reportParams.districtId.toString(),
      facilityId: reportParams.facilityId.toString(),
      programIds: $scope.selectedProgramIds
    };

    RequisitionReportService.get(utils.pickEmptyObject(requisitionQueryParameters), function (data) {
      $scope.requisitions = data.rnr_list;
      formatRequisitionList();
      calculatorProgramsExpectedAndSubmittedQuantity();
    });
  };

  var calculatorProgramsExpectedAndSubmittedQuantity = function () {
    var selectedProgram = _.filter($scope.requisitionPrograms, function (program) {
      return _.include($scope.selectedProgramIds, program.id);
    });

    $scope.programsExpectedAndSubmittedQuantity =_.map(selectedProgram, function (selectedProgram) {
      return expectedAndSubmittedQuantityByProgramName(selectedProgram.name);
    });
  };

  var expectedAndSubmittedQuantityByProgramName = function (selectedProgramName) {
    var requisitions = _.filter($scope.requisitions, function (requisition) {
      return requisition.programName === selectedProgramName;
    });

    var submittedRequisitions = _.filter(requisitions, function (balanceRequisition) {
      return balanceRequisition.clientSubmittedTime;
    });

    return {
      programName: selectedProgramName,
      expectedQuantity: requisitions.length,
      submittedQuantity: submittedRequisitions.length
    };
  };

  $scope.validateProgram = function () {
    $scope.invalidProgram = _.isEmpty($scope.selectedProgramIds);
    return !$scope.invalidProgram;
  };

  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility() &&
      $scope.validateProgram()
    ) {
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
      setSubmittedStatus(rnr);
      setOriginalPeriodString(rnr);
      renameRequisitionType(rnr);
      rnr.programName = UnitService.programNameFormatter(rnr.programName);

      rnr.inventoryDate = formatDate(rnr.actualPeriodEnd);

      rnr.clientSubmittedTimeString = $scope.submittedTimeFormatter(rnr.webSubmittedTime);

      rnr.webSubmittedTimeString = $scope.submittedTimeFormatter(rnr.clientSubmittedTime);
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

  $scope.submittedTimeFormatter = function (submittedTime) {
    return submittedTime ? DateFormatService.formatDateWithTimeAndLocale(submittedTime) : "";
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
        displayName: messageService.get("label.report.requisitions.programname")
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
          var currentDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(currentDateString));
          var previousDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(previousDateString));

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
          var currentDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(currentOriginalPeriodString.split("-")[0]));
          var previousDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(previousOriginalPeriodString.split("-")[0]));

          if (currentDate === previousDate) return 0;
          if (currentDate < previousDate) return -1;
          return 1;
        }
      },
      {
        field: 'clientSubmittedTimeString',
        displayName: messageService.get("label.report.requisitions.submittedtime"),
        cellTemplate: '<div class="customCell ngCellText">{{submittedTimeFormatter(row.entity.clientSubmittedTime)}}</div>',
        sortFn: function (currentDateString, previousDateString) {
          var currentDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(currentDateString));
          var previousDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(previousDateString));
    
          if (currentDate === previousDate) return 0;
          if (currentDate < previousDate) return -1;
          return 1;
        }
      },
      {
        field: 'webSubmittedTimeString',
        displayName: messageService.get("label.report.requisitions.syncedtime"),
        cellTemplate: '<div class="customCell ngCellText">{{submittedTimeFormatter(row.entity.webSubmittedTime)}}</div>',
        sortFn: function (currentDateString, previousDateString) {
          var currentDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(currentDateString));
          var previousDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(previousDateString));
    
          if (currentDate === previousDate) return 0;
          if (currentDate < previousDate) return -1;
          return 1;
        }
      }
    ]
  };

  function formatDate(date) {
    return !!date ? DateFormatService.formatDateWithLocale(date) : '';
  }

  $scope.getRedirectUrl = function () {
    var url = "/public/pages/logistics/rnr/index.html#/";
    var urlMapping = {
      "VIA": "view-requisition-via/",
      "Balance Requisition": "view-requisition-via/",
      "Requisição Balancete": "view-requisition-via/",
      "ESS_MEDS": "view-requisition-via/",
      "MMIA": "view-requisition-mmia/",
      "AL": "view-requisition-al/",
      "Repaid Test": "view-requisition-rapid-test/",
      "PTV": "view-requisition-ptv/"
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

  var generateSubmittedAndExceptedQuantity = function() {
    var submittedAndExceptedQuantityArray = _.map($scope.programsExpectedAndSubmittedQuantity, function (programExpectedAndSubmittedQuantity) {
      return programExpectedAndSubmittedQuantity.programName + '-' +
        programExpectedAndSubmittedQuantity.submittedQuantity + '/' +
        programExpectedAndSubmittedQuantity.expectedQuantity;
    });

    return submittedAndExceptedQuantityArray.join(', ');
  };

  $scope.exportXLSX = function () {
    var data = {
      reportTitles: [
        [
          messageService.get('report.header.generated.for'),
          DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.startTime) + ' - ' +
          DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.endTime)
        ], [
          messageService.get('label.report.requisitions.submittedandexpected'),
          generateSubmittedAndExceptedQuantity()
        ]
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
          value: !!requisition.clientSubmittedTime ?
            DateFormatService.formatDateWithDateMonthYear(requisition.actualPeriodEnd) : null,
          dataType: 'date',
          style: {
            dataPattern: 'dd-MM-yyyy',
            excelDataPattern: 'd/m/yy'
          }
        };
        requisitionContent.submittedStatus = requisition.submittedStatus;
        requisitionContent.originalPeriodDate = requisition.originalPeriodString;
        requisitionContent.submittedTime = {
          value: !!requisition.clientSubmittedTime ?
            DateFormatService.formatDateWithDateMonthYear(requisition.clientSubmittedTime) : '',
          dataType: 'date',
          style: {
            dataPattern: 'dd-MM-yyyy',
            excelDataPattern: 'd/m/yy'
          }
        };
        requisitionContent.syncTime = {
          value: !!requisition.webSubmittedTime ?
            DateFormatService.formatDateWithDateMonthYear(requisition.webSubmittedTime) : '',
          dataType: 'date',
          style: {
            dataPattern: 'dd-MM-yyyy',
            excelDataPattern: 'd/m/yy'
          }
        };
        data.reportContent.push(requisitionContent);
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.requisition.report'));
    }
  };
}
services.factory('TracerDrugsChartService', function ($http, $filter, $q, $timeout, messageService, CubesGenerateUrlService, StockoutSingleProductZoneChartService, CubesGenerateCutParamsService, ReportLocationConfigService, ReportExportExcelService, DateFormatService, WeeklyDrugExportService) {

  var drugCodekey = "drug.drug_code";
  var drugNameKey = "drug.drug_name";
  var selectedDrugs = [];
  var chartDataItems;

  function getTracerDrugStockRateOnFriday(zone, friday, stockOuts, tracerDrugCode, carryStartDates) {
    var stockOutsOfTracerDrug = _.filter(stockOuts, function (stockOut) {
      return stockOut[drugCodekey] === tracerDrugCode;
    });
    var carryStartDatesOfTracerDrug = _.filter(carryStartDates, function (carry) {
      return carry[drugCodekey] === tracerDrugCode;
    });

    return StockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, friday, friday, stockOutsOfTracerDrug, carryStartDatesOfTracerDrug)[0];
  }

  function getCubesRequestPromise(tracerDrugs, province, district, userSelectedStartDate, userSelectedEndDate, cubesName, timeDimensionName) {
    var cutsParams = CubesGenerateCutParamsService.generateCutsParams(timeDimensionName,
      $filter('date')(userSelectedStartDate, "yyyy,MM,dd"),
      $filter('date')(userSelectedEndDate, "yyyy,MM,dd"),
      undefined, tracerDrugs, province, district);

    var requestUrl = CubesGenerateUrlService.generateFactsUrl(cubesName, cutsParams);
    return $http.get(requestUrl);
  }

  function generateTracerDurgDataItemForOneFriday(friday, tracerDrugs, province, district, stockOuts, carryStartDates) {
    var chartDataItem = {date: friday};

    var totalPercentage = 0;
    _.forEach(tracerDrugs, function (tracerDrug) {
      var tracerDrugCode = tracerDrug[drugCodekey];

      var fridayStockOutRate = getTracerDrugStockRateOnFriday(ReportLocationConfigService.getZone(province, district), friday, stockOuts, tracerDrugCode, carryStartDates);
      chartDataItem[tracerDrugCode + "StockOutFacilities"] = fridayStockOutRate.stockOutFacilities;
      chartDataItem[tracerDrugCode + "CarryingFacilities"] = fridayStockOutRate.carryingFacilities;
      chartDataItem[tracerDrugCode] = 0;
      if (fridayStockOutRate.carryingFacilities.length > 0) {
        chartDataItem[tracerDrugCode] = 100 - fridayStockOutRate.percentage;
      }

      totalPercentage += chartDataItem[tracerDrugCode];
    });
    chartDataItem.average = (totalPercentage / tracerDrugs.length).toFixed(0);

    return chartDataItem;
  }

  function LinkedNode() {
    var self = this;

    this.addToTail = function (elem) {
      if (self.next === undefined) {
        self.next = elem;
      } else {
        self.next.addToTail(elem);
      }
    };

    this.showSelfAndNext = function (isToggleOff, event) {
      $timeout(function () {
        if (isToggleOff) {
          event.chart.hideGraph(self.value);
        } else {
          event.chart.showGraph(self.value);
        }
        if (self.next !== undefined) {
          self.next.showSelfAndNext(isToggleOff, event);
        }
      });
    };
  }

  function toggleGraphsExclude(event, isToggleOff, excludes) {
    var firstLinkedNode = _.chain(event.chart.graphs)
      .filter(function (graph) {
        return _.every(excludes, function (exclude) {
          return graph.id != exclude;
        });
      })
      .reduce(function (node, graph) {
        var newNode = new LinkedNode();
        newNode.value = graph;

        node.addToTail(newNode);
        return node;
      }, new LinkedNode())
      .value().next;

    firstLinkedNode.showSelfAndNext(isToggleOff, event);
  }

  function generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, province, district) {
    var fridays = DateFormatService.getFridaysBetween(userSelectedStartDate, userSelectedEndDate);
    return _.chain(fridays)
      .map(function (friday) {
        return generateTracerDurgDataItemForOneFriday(friday, tracerDrugs, province, district, stockOuts, carryStartDates);
      }).value();
  }

  function makeTracerDrugsChart(chartDivId, legendDivId, userSelectedStartDate, userSelectedEndDate, province, district) {
    selectedDrugs = [];
    $http.get('/cubesreports/cube/products/facts?cut=is_tracer:true').success(function (tracerDrugs) {
      var stockOutPromise = getCubesRequestPromise(tracerDrugs, province, district, userSelectedStartDate, userSelectedEndDate, "vw_stockouts", "overlapped_date");
      var carryStartDatesPromise = getCubesRequestPromise(tracerDrugs, province, district, "", userSelectedEndDate, "vw_carry_start_dates", "carry_start");

      $q.all([stockOutPromise, carryStartDatesPromise]).then(function (arrayOfResults) {
        var stockOuts = arrayOfResults[0].data;
        var carryStartDates = arrayOfResults[1].data;
        chartDataItems = generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, province, district);

        renderTracerDrugsChart(chartDivId, legendDivId, chartDataItems, tracerDrugs);
      });
    });
    return true;
  }

  function generateGraphs(tracerDrugs) {
    function stringToRGB(str) {
      var hash = 0;
      for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
      }

      var c = (hash & 0x00FFFFFF)
        .toString(16)
        .toUpperCase();

      return "#" + "00000".substring(0, 6 - c.length) + c;
    }

    function makeBalloon(tracerDrugName, tracerDrugcode) {
      return function (item, graph) {
        var percentage = item.dataContext[tracerDrugcode];
        var stockOutFacilities = item.dataContext[tracerDrugcode + "StockOutFacilities"];
        var carryingFacilities = item.dataContext[tracerDrugcode + "CarryingFacilities"];
        var facilitiesWithStock = carryingFacilities.filter(function (a) {
          return !~this.indexOf(a);
        }, stockOutFacilities);
        var facilitiesWithStockNames = facilitiesWithStock.join(', ');

        var informationDrug = messageService.get('report.tracer.name') + ": " + tracerDrugName + "[" + tracerDrugcode + "]" + "<br>" +
          messageService.get('report.tracer.percentage') + ": " + percentage + "% <br>" +
          messageService.get('report.tracer.health.facility') + ": " + (carryingFacilities.length - stockOutFacilities.length) + "% <br>";

        if (!_.isEmpty(stockOutFacilities)) {
          var stockOutFacilitiesNames = stockOutFacilities.join(', ');
          informationDrug += "<span style='color:red;'>" + stockOutFacilitiesNames + "</span><br>";
        }

        informationDrug += facilitiesWithStockNames + "<br>";

        return informationDrug;
      };
    }

    var tracerDrugGraphs = _.chain(tracerDrugs)
      .sortBy(function (tracerDrug) {
        return tracerDrug[drugNameKey];
      })
      .map(function (tracerDrug) {
        var tracerDrugcode = tracerDrug[drugCodekey];
        var tracerDrugName = tracerDrug[drugNameKey];

        return {
          lineColor: stringToRGB(tracerDrugcode + tracerDrugName),
          bullet: "round",
          title: tracerDrugName + "[" + tracerDrugcode + "]",
          valueField: tracerDrugcode,
          hidden: true,
          lineThickness: 2,
          balloonFunction: makeBalloon(tracerDrugName, tracerDrugcode)
        };
      }).value();

    tracerDrugGraphs.unshift({
      id: "average",
      lineColor: "red",
      bullet: "round",
      title: messageService.get('report.tracer.average'),
      valueField: "average",
      dashLength: 3,
      lineThickness: 6,
      balloonText: messageService.get('report.tracer.average') + ": [[average]]%"
    });
    tracerDrugGraphs.push({
      id: "all",
      title: messageService.get('report.tracer.all'),
      lineColor: "black",
      hidden: true,
      legendValueText: " ",
      legendPeriodValueText: " "
    });

    return tracerDrugGraphs;
  }

  function renderTracerDrugsChart(chartDivId, legendDivId, chartDataItems, tracerDrugs) {
    var dateWeeklyString = messageService.get('report.tracer.week') + ' ' + 'W';

    function onInit(initEvent) {
      function legendHandler(toggleEvent) {
        if (toggleEvent.dataItem.id == 'all') {
          toggleGraphsExclude(toggleEvent, toggleEvent.dataItem.hidden, ['all', 'average']);
        }
      }

      initEvent.chart.legend.addListener('hideItem', legendHandler);
      initEvent.chart.legend.addListener('showItem', legendHandler);
    }

    AmCharts.makeChart(chartDivId, {
      "listeners": [{
        "event": "init",
        "method": onInit
      }],
      "type": "serial",
      "theme": "light",
      "legend": {
        divId: legendDivId,
        "listeners": [{
          "event": "hideItem",
          "method": handleLegendClick
        }, {
          "event": "showItem",
          "method": handleLegendClick
        }]
      },
      "dataProvider": chartDataItems,
      "valueAxes": [{
        "axisThickness": 2,
        "gridAlpha": 0,
        "axisAlpha": 1,
        "position": "left",
        maximum: 100,
        minimum: 0
      }],
      "graphs": generateGraphs(tracerDrugs),
      balloon: {textAlign: "left", maxWidth: 300},
      "chartScrollbar": {
        "oppositeAxis": false,
        "offset": 30
      },
      "chartCursor": {
        "cursorPosition": "mouse",
        categoryBalloonDateFormat: dateWeeklyString + "(DD.MM.YYYY)"
      },
      "categoryField": "date",
      "categoryAxis": {
        "parseDates": true,
        "axisColor": "#DADADA",
        "minorGridEnabled": true,
        "dateFormats": [{
          period: 'DD',
          format: 'YYYY' + ' ' + dateWeeklyString
        }, {
          period: 'WW',
          format: 'YYYY' + ' ' + dateWeeklyString
        }, {
          period: 'MM',
          format: 'MM.YYYY'
        }, {
          period: 'YYYY',
          format: 'YYYY'
        }]
      }
    });
  }

  function handleLegendClick(evt) {
    if (evt.type === "hideItem") {
      _.uniq(selectedDrugs);
      var index = selectedDrugs.indexOf(evt.dataItem.valueField);
      if (index > -1) {
        selectedDrugs.splice(index, 1);
      }
    } else {
      selectedDrugs.push(evt.dataItem.valueField);
    }
  }

  function exportXLSX(startTime, endTime, province, district) {
    WeeklyDrugExportService.getDataForExport(selectedDrugs, province, district, startTime, endTime);
  }

  // function exportXLSX(startTime, endTime, province, district) {
  //   var fillTracerDrugReportDataPromise = fillTracerDrugReportData(startTime, endTime, province, district);
  //
  //   $q.all([fillTracerDrugReportDataPromise]).then(function (data) {
  //     ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.tracer.drugs.report'));
  //   });
  // }

  // function assignTracerDrugData(endTime, tracerDrugInFacility, dates) {
  //   var endDateParam = new Date($filter('date')(endTime, "yyyy,MM,dd"));
  //   var startDatePeriod = DateFormatService.formatDateWithStartDayOfPeriod(endDateParam);
  //   var endDatePeriod = DateFormatService.formatDateWithEndDayOfPeriod(endDateParam);
  //
  //   var newTracerDrug = {};
  //   newTracerDrug.drugCode = tracerDrugInFacility[0]['drug.drug_code'];
  //   newTracerDrug.drugName = tracerDrugInFacility[0]['drug.drug_name'];
  //   newTracerDrug.province = tracerDrugInFacility[0]['location.province_name'];
  //   newTracerDrug.district = tracerDrugInFacility[0]['location.district_name'];
  //   newTracerDrug.facility = tracerDrugInFacility[0]['facility.facility_name'];
  //
  //   getSohOnDate(dates, newTracerDrug, tracerDrugInFacility);
  //
  //   newTracerDrug.cmm_value = getCMMForTracerDrugInPeriod(tracerDrugInFacility, startDatePeriod, endDatePeriod);
  //   return newTracerDrug;
  // }
  //
  // function fillTracerDrugReportData(startTime, endTime, province, district) {
  //     var params = [{
  //       name: 'fields',
  //       value: ['location.province_name', 'location.district_name', 'facility.facility_code', 'facility.facility_name', 'drug.drug_name', 'drug.drug_code', 'date', 'soh']
  //     }];
  //
  //     var drugParams = validateDrugs(selectedDrugs);
  //
  //     if (chartDataItems) {
  //       var cutParamsWeekly = CubesGenerateCutParamsService.generateCutsParams('cutDate',
  //         $filter('date')(startTime, "yyyy,MM,dd"),
  //         $filter('date')(endTime, "yyyy,MM,dd"),
  //         undefined, drugParams, province, district);
  //
  //       $http.get(CubesGenerateUrlService.generateFactsUrlWithParams('vw_weekly_tracer_soh', cutParamsWeekly , params)).success(function (tracerDrugs) {
  //         var dates = getColumnsFromDates(tracerDrugs);
  //         tracerDrugs = _.groupBy(tracerDrugs, function (tracerDrug) {
  //           return tracerDrug["drug.drug_code"] + tracerDrug["facility.facility_code"];
  //         });
  //         var data = {
  //           reportHeaders: addReportDateHeaders(dates),
  //           reportContent: []
  //         };
  //
  //         _.forEach(tracerDrugs, function (tracerDrugInFacility) {
  //           var newTracerDrug = assignTracerDrugData(endTime, tracerDrugInFacility, dates);
  //
  //           data.reportContent.push(newTracerDrug);
  //         });
  //         return data;
  //       });
  //     }
  // }
  //
  // function getCMMForTracerDrugInPeriod(tracerDrugInFacility, startDatePeriod, endDatePeriod) {
  //   var cutsParams = [
  //     {dimension: "facilityCode", values: [tracerDrugInFacility[0]["facility.facility_code"]]},
  //     {dimension: "product", values: [tracerDrugInFacility[0]['drug.drug_code']]},
  //     {dimension: "periodbegin", values: [$filter('date')(startDatePeriod, "yyyy,MM,dd")], skipEscape: true},
  //     {dimension: "periodend", values: [$filter('date')(endDatePeriod, "yyyy,MM,dd")], skipEscape: true}
  //   ];
  //   var requestUrl = CubesGenerateUrlService.generateFactsUrl('vw_cmm_entries', cutsParams);
  //   $q.when($http.get(requestUrl)).then(function (cmmEntries) {
  //     return _.isEmpty(cmmEntries) ? cmmEntries[0].cmm : "N/A";
  //   });
  // }

  return {
    generateGraphs: generateGraphs,
    generateTracerDrugsChartDataItems: generateTracerDrugsChartDataItems,
    makeTracerDrugsChart: makeTracerDrugsChart,
    exportXLSX: exportXLSX
  };
});
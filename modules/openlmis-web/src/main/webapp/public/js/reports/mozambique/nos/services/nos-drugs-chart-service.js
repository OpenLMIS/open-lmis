services.factory('NosDrugsChartService', function ($http, $filter, $q, $timeout, messageService,
                                                   CubesGenerateUrlService, StockoutSingleProductZoneChartService,
                                                   CubesGenerateCutParamsService, ReportLocationConfigService,
                                                   ReportExportExcelService, DateFormatService, NosDrugStatusService,
                                                   WeeklyNosDrugExportService) {

  var DATE_FORMAT = 'yyyy,MM,dd';
  var drugCodeKey = "drug.drug_code";
  var drugNameKey = "drug.drug_name";
  var selectedDrugs = [];
  var chartDataItems;
  var allNosDrugs = [];

  function getNosDrugStockRateOnFriday(zone, friday, stockOuts, nosDrugCode, carryStartDates) {
    var stockOutsOfNosDrug = _.filter(stockOuts, function (stockOut) {
      return stockOut[drugCodeKey] === nosDrugCode;
    });
    var carryStartDatesOfNosDrug = _.filter(carryStartDates, function (carry) {
      return carry[drugCodeKey] === nosDrugCode;
    });

    return StockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, friday, friday, stockOutsOfNosDrug, carryStartDatesOfNosDrug)[0];
  }

  function getCubesRequestPromise(nosDrugs, province, district, userSelectedStartDate, userSelectedEndDate, cubesName, timeDimensionName) {
    var cutsParams = CubesGenerateCutParamsService.generateCutsParams(timeDimensionName,
      $filter('date')(userSelectedStartDate, "yyyy,MM,dd"),
      $filter('date')(userSelectedEndDate, "yyyy,MM,dd"),
      undefined, nosDrugs, province, district);

    var requestUrl = CubesGenerateUrlService.generateFactsUrl(cubesName, cutsParams);
    return $http.get(requestUrl);
  }

  function generateNosDrugDataItemForOneFriday(friday, nosDrugs, province, district, stockOuts, carryStartDates) {
    var chartDataItem = {date: friday};

    var totalPercentage = 0;
    _.forEach(nosDrugs, function (nosDrug) {
      var nosDrugCode = nosDrug[drugCodeKey];

      var fridayStockOutRate = getNosDrugStockRateOnFriday(ReportLocationConfigService.getZone(province, district), friday, stockOuts, nosDrugCode, carryStartDates);
      chartDataItem[nosDrugCode + "StockOutFacilities"] = fridayStockOutRate.stockOutFacilities;
      chartDataItem[nosDrugCode + "CarryingFacilities"] = fridayStockOutRate.carryingFacilities;
      chartDataItem[nosDrugCode] = 0;
      if (fridayStockOutRate.carryingFacilities.length > 0) {
        chartDataItem[nosDrugCode] = 100 - fridayStockOutRate.percentage;
      }

      totalPercentage += chartDataItem[nosDrugCode];
    });
    chartDataItem.average = (totalPercentage / nosDrugs.length).toFixed(0);

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

  function generateNosDrugsChartDataItems(nosDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, province, district) {
    var fridays = DateFormatService.getFridaysBetween(userSelectedStartDate, userSelectedEndDate);
    return _.chain(fridays)
      .map(function (friday) {
        return generateNosDrugDataItemForOneFriday(friday, nosDrugs, province, district, stockOuts, carryStartDates);
      }).value();
  }

  function makeNosDrugHistogram(chartDivId, province, district, userSelectedStartDate, userSelectedEndDate, selectedDrugCode) {
    var nosDrugItemsPromise = getNosDrugItemsPromise(province, district, userSelectedStartDate, userSelectedEndDate, selectedDrugCode);

    nosDrugItemsPromise.$promise.then(function (nosDrugItemsResponse) {
      var formattedNosDrugItems = formatNosDrugItems(nosDrugItemsResponse.data);

      renderNosDrugHistogram(chartDivId, formattedNosDrugItems);

    });

    return true;
  }

  function formatNosDrugItems(nosDrugItems) {
    if (nosDrugItems.length === 0) {
      return [{
        nosData: null,
        date: '',
        lowStockPercentage: 0,
        overStockPercentage: 0,
        regularStockPercentage: 0,
        stockOutPercentage: 0
      }];
    }

    return _.map(nosDrugItems, function (nosDrugItem) {
      var dateKey = Object.keys(nosDrugItem)[0];
      return {
        nosData: nosDrugItem,
        date: dateKey,
        lowStockPercentage: nosDrugItem[dateKey].lowStock.percentage === 0 ? null : nosDrugItem[dateKey].lowStock.percentage,
        overStockPercentage: nosDrugItem[dateKey].overStock.percentage === 0 ? null : nosDrugItem[dateKey].overStock.percentage,
        regularStockPercentage: nosDrugItem[dateKey].regularStock.percentage === 0 ? null : nosDrugItem[dateKey].regularStock.percentage,
        stockOutPercentage: nosDrugItem[dateKey].stockOut.percentage === 0 ? null : nosDrugItem[dateKey].stockOut.percentage
      };
    });
  }

  function getNosDrugItemsPromise(province, district, startTime, endTime, selectedDrugCode) {
    selectedDrugs = [];
    selectedDrugs.push(selectedDrugCode);
    var params = {
      province: province,
      district: district,
      startTime: $filter('date')(startTime, DATE_FORMAT),
      endTime: $filter('date')(endTime, DATE_FORMAT),
      selectedDrugs: selectedDrugs,
      reportType: "nosDrug"
    };

    return NosDrugStatusService.get(params);
  }

  function getNosDrugList() {
    return $http.get('/cubesreports/cube/products/facts?cut=is_nos:true').success(function (nosDrugs) {
      return nosDrugs;
    }).error(function () {
      return [];
    });
  }

  function generateGraphs(nosDrugs) {
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

    function makeBalloon(nosDrugName, nosDrugcode) {
      return function (item, graph) {
        var percentage = item.dataContext[nosDrugcode];
        var stockOutFacilities = item.dataContext[nosDrugcode + "StockOutFacilities"];
        var carryingFacilities = item.dataContext[nosDrugcode + "CarryingFacilities"];
        var facilitiesWithStock = carryingFacilities.filter(function (a) {
          return !~this.indexOf(a);
        }, stockOutFacilities);
        var facilitiesWithStockNames = facilitiesWithStock.join(', ');

        var informationDrug = messageService.get('report.tracer.name') + ": " + nosDrugName + "[" + nosDrugcode + "]" + "<br>" +
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

    var nosDrugGraphs = _.chain(nosDrugs)
      .sortBy(function (nosDrug) {
        return nosDrug[drugNameKey];
      })
      .map(function (nosDrug) {
        var nosDrugcode = nosDrug[drugCodeKey];
        var nosDrugName = nosDrug[drugNameKey];

        return {
          lineColor: stringToRGB(nosDrugcode + nosDrugName),
          bullet: "round",
          title: nosDrugName + "[" + nosDrugcode + "]",
          valueField: nosDrugcode,
          hidden: true,
          lineThickness: 2,
          balloonFunction: makeBalloon(nosDrugName, nosDrugcode)
        };
      }).value();

    nosDrugGraphs.unshift({
      id: "average",
      lineColor: "red",
      bullet: "round",
      title: messageService.get('report.tracer.average'),
      valueField: "average",
      dashLength: 3,
      lineThickness: 6,
      balloonText: messageService.get('report.tracer.average') + ": [[average]]%"
    });
    nosDrugGraphs.push({
      id: "all",
      title: messageService.get('report.tracer.all'),
      lineColor: "black",
      hidden: true,
      legendValueText: " ",
      legendPeriodValueText: " "
    });

    return nosDrugGraphs;
  }

  function renderNosDrugHistogram(chartDivId, nosDrugItems) {

    var chart = AmCharts.makeChart(chartDivId, {
      type: "serial",
      categoryField: "date",
      startDuration: 1,
      columnWidth: 0.5,
      trendLines: [],
      guides: [],
      allLabels: [],
      categoryAxis: {
        gridPosition: "start",
        autoWrap: true,
      },
      zoomOutText: '',
      chartScrollbar: {
        enabled: true,
        graphType: "line",
        offset: 40,
        oppositeAxis: false,
        scrollbarHeight: 5,
        scrollDuration: 0
      },
      balloon: {
        textAlign: "left",
        fixedPosition: false,
      },
      graphs: [
        {
          balloonFunction: generateBalloonInfo,
          fillAlphas: 1,
          title: "stockOut",
          type: "column",
          valueField: "stockOutPercentage",
          fillColors: "#f5212d",
          legendColor: "#f5212d",
        },
        {
          balloonFunction: generateBalloonInfo,
          fillAlphas: 1,
          id: "AmGraph-3",
          title: "lowStock",
          type: "column",
          valueField: "lowStockPercentage",
          fillColors: "#fad74d",
          legendColor: "#fad74d",
        },
        {
          balloonFunction: generateBalloonInfo,
          fillAlphas: 1,
          id: "AmGraph-4",
          title: "regularStock",
          type: "column",
          valueField: "regularStockPercentage",
          fillColors: "#4bba14",
          legendColor: "#4bba14",
        },
        {
          balloonFunction: generateBalloonInfo,
          fillAlphas: 1,
          title: "overStock",
          type: "column",
          valueField: "overStockPercentage",
          fillColors: "#6610c7",
          legendColor: "#6610c7",
        }
      ],
      valueAxes: [
        {
          axisFrequency: 4,
          baseValue: 2,
          maximum: 0,
          minMaxMultiplier: 0,
          stackType: "100%",
          unit: "%",
          offset: 1,
          titleColor: "#0000FF",
          color: "#999999",
          titleFontSize: 0
        }
      ],
      legend: {
        enabled: true,
        align: "center",
        textClickEnabled: true,
      },
      dataProvider: nosDrugItems
    });

    AmCharts.checkEmptyData = function (chart) {
      if (!chart.dataProvider[0].nosData) {
        chart.addLabel(0, '50%', 'The chart contains no data', 'center');

        chart.chartDiv.style.opacity = 0.5;

        chart.validateNow();
      }
    };

    AmCharts.checkEmptyData(chart);
  }

  function generateBalloonInfo(e) {
    var drugContext = e.dataContext;
    var graph = e.graph;
    var originalNosDrugData = drugContext.nosData[drugContext.date];

    function generateFacilitiesName(facilities) {
      var names = '';
      return _.each(facilities, function (facility) {
        names += facility;
      });
    }

    return messageService.get('report.tracer.health.facility.number') + ": <span style='font-weight: bold'>" + originalNosDrugData[graph.title].facilities.length + "</span><br>" +
      messageService.get('report.tracer.percentage') + ": <span style='font-weight: bold'>" + originalNosDrugData[graph.title].percentage + "%</span>" +
      "<hr style='margin: 0'>" + generateFacilitiesName(originalNosDrugData[graph.title].facilities);
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
    var allNosCodes = _.map(allNosDrugs, function (drug) {
      return drug['drug.drug_code'];
    });

    WeeklyNosDrugExportService.getDataForExport(selectedDrugs, province, district, startTime, endTime, allNosCodes);
  }

  return {
    generateGraphs: generateGraphs,
    generateNosDrugsChartDataItems: generateNosDrugsChartDataItems,
    exportXLSX: exportXLSX,
    getNosDrugList: getNosDrugList,
    makeNosDrugHistogram: makeNosDrugHistogram,
    getNosDrugItemsPromise: getNosDrugItemsPromise
  };
});
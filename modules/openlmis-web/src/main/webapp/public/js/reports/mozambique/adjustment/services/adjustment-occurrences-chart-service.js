services.factory('AdjustmentOccurrencesChartService', function (messageService) {

  function renderAdjustmentChart(chartDivId, adjustmentsInPeriods, adjustmentType, label) {
    var adjustmentGraphConfigs = {
      "negative": [
        {
          "balloonText": messageService.get("stock.movement.INVENTORY_NEGATIVE") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.INVENTORY_NEGATIVE"),
          "valueField": "INVENTORY_NEGATIVE"
        },
        {
          "balloonText": messageService.get("stock.movement.EXPIRED_RETURN_TO_SUPPLIER") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.EXPIRED_RETURN_TO_SUPPLIER"),
          "valueField": "EXPIRED_RETURN_TO_SUPPLIER"
        },
        {
          "balloonText": messageService.get("stock.movement.DAMAGED") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.DAMAGED"),
          "valueField": "DAMAGED"
        },
        {
          "balloonText": messageService.get("stock.movement.LOANS_DEPOSIT") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.LOANS_DEPOSIT"),
          "valueField": "LOANS_DEPOSIT"
        },
        {
          "balloonText": messageService.get("stock.movement.PROD_DEFECTIVE") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.PROD_DEFECTIVE"),
          "valueField": "PROD_DEFECTIVE"
        },
        {
          "balloonText": messageService.get("stock.movement.RETURN_TO_DDM") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.RETURN_TO_DDM"),
          "valueField": "RETURN_TO_DDM"
        }
      ],
      "positive":[
        {
          "balloonText": messageService.get("stock.movement.CUSTOMER_RETURN") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.CUSTOMER_RETURN"),
          "valueField": "CUSTOMER_RETURN"
        },
        {
          "balloonText": messageService.get("stock.movement.EXPIRED_RETURN_FROM_CUSTOMER") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.EXPIRED_RETURN_FROM_CUSTOMER"),
          "valueField": "EXPIRED_RETURN_FROM_CUSTOMER"
        },
        {
          "balloonText": messageService.get("stock.movement.DONATION") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.DONATION"),
          "valueField": "DONATION"
        },
        {
          "balloonText": messageService.get("stock.movement.LOANS_RECEIVED") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.LOANS_RECEIVED"),
          "valueField": "LOANS_RECEIVED"
        },
        {
          "balloonText": messageService.get("stock.movement.INVENTORY_POSITIVE") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.INVENTORY_POSITIVE"),
          "valueField": "INVENTORY_POSITIVE"
        },
        {
          "balloonText": messageService.get("stock.movement.RETURN_FROM_QUARANTINE") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("stock.movement.RETURN_FROM_QUARANTINE"),
          "valueField": "RETURN_FROM_QUARANTINE"
        }
      ]
    };

    AmCharts.makeChart(chartDivId, {
      "type": "serial",
      "theme": "light",
      "allLabels": [{
        "text": label,
        "bold": true,
        "align":"center"
      }],
      "legend": {
        "position": "bottom",
        "valueAlign": "left"
      },
      "dataProvider": adjustmentsInPeriods,
      "valueAxes": [{
        "stackType": "regular"
      }],
      "graphs": adjustmentGraphConfigs[adjustmentType],
      "chartScrollbar": {
        "oppositeAxis": false,
        "offset": 30
      },
      "chartCursor": {},
      "categoryField": "period"
    });
  }

  return {
    renderAdjustmentChart: renderAdjustmentChart
  };
});
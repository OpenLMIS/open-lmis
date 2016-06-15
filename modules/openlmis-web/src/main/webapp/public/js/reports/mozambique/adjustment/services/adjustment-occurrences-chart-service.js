services.factory('AdjustmentOccurrencesChartService', function (messageService) {

  function renderAdjustmentChart(chartDivId, adjustmentsInPeriods, adjustmentType, label) {
    var adjustmentGraphConfigs = {
      "negative": [
        {
          "balloonText": messageService.get("adjustment.chart.expired.return.to.supplier") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.expired.return.to.supplier"),
          "valueField": "EXPIRED_RETURN_TO_SUPPLIER"
        },
        {
          "balloonText": messageService.get("adjustment.chart.damaged") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.damaged"),
          "valueField": "DAMAGED"
        },
        {
          "balloonText": messageService.get("adjustment.chart.loans.deposit") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.loans.deposit"),
          "valueField": "LOANS_DEPOSIT"
        },
        {
          "balloonText": messageService.get("adjustment.chart.inventory.negative") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.inventory.negative"),
          "valueField": "INVENTORY_NEGATIVE"
        },
        {
          "balloonText": messageService.get("adjustment.chart.prod.defective") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.prod.defective"),
          "valueField": "PROD_DEFECTIVE"
        }
      ],
      "positive":[
        {
          "balloonText": messageService.get("adjustment.chart.customer.return") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.customer.return"),
          "valueField": "CUSTOMER_RETURN"
        },
        {
          "balloonText": messageService.get("adjustment.chart.expired.return.from.customer") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.expired.return.from.customer"),
          "valueField": "EXPIRED_RETURN_FROM_CUSTOMER"
        },
        {
          "balloonText": messageService.get("adjustment.chart.donation") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.donation"),
          "valueField": "DONATION"
        },
        {
          "balloonText": messageService.get("adjustment.chart.loans.received") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.loans.received"),
          "valueField": "LOANS_RECEIVED"
        },
        {
          "balloonText": messageService.get("adjustment.chart.inventory.positive") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.inventory.positive"),
          "valueField": "INVENTORY_POSITIVE"
        },
        {
          "balloonText": messageService.get("adjustment.chart.return.from.quarantine") + ": [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": messageService.get("adjustment.chart.return.from.quarantine"),
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
      "categoryField": "period",
      "categoryAxis": {
        "startOnAxis": true
      }
    });
  }

  return {
    renderAdjustmentChart: renderAdjustmentChart
  };
});
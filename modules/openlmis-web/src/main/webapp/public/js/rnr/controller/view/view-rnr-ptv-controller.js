function ViewRnrPTVController($scope, $route, Requisitions, messageService, DateFormatService, downloadPdfService, downloadSimamService) {

  $scope.rnrLineItems = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadALDetail();
  });

  $scope.$on('messagesPopulated', function () {
    $scope.initMonth();
    $scope.initDate();
    $scope.initContent();
  });

  $(".btn-download-pdf").hide();
  $(".btn-download-simam").hide();
  $scope.loadALDetail = function () {

    Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
        $scope.rnr = {
          "clientSubmittedTime": "**",
          "actualPeriodStartDate": "**",
          "actualPeriodEndDate": "**",
          "products": [
            {
              "services": [
                {
                  "code": "CPN",
                  "name": "CPN",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Maternity",
                  "name": "Maternity",
                  "patientsOnTreatment": "11"
                },
                {
                  "code": "CCR",
                  "name": "CCR",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Farmacy",
                  "name": "Farmacy",
                  "patientsOnTreatment": "0"
                }
              ],
              "productCode": "Zidovudina 300mg/Lamivudine150mg Frasco 60 comps",
              "initialAmount": "Initial Stock Level",
              "received": "Entradas",
              "issued": 0,
              "adjustment": "Losses and Adjustments",
              "inventory": "Final Stock",
              "validate": 0,
              "requestAmount": 0,
              "approvedAmount": 0,
              "calculatedOrderQuantity": "TOTALS/Partials"
            },
            {
              "services": [
                {
                  "code": "CPN",
                  "name": "CPN",
                  "patientsOnTreatment": "58"
                },
                {
                  "code": "Maternity",
                  "name": "Maternity",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "CCR",
                  "name": "CCR",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Farmacy",
                  "name": "Farmacy",
                  "patientsOnTreatment": "0"
                }
              ],
              "productCode": "Zidovudina 300mg frasco 60 comps",
              "initialAmount": "Initial Stock Level",
              "received": "Entradas",
              "issued": 0,
              "adjustment": "Losses and Adjustments",
              "inventory": "Final Stock",
              "validate": 0,
              "requestAmount": 0,
              "approvedAmount": 0,
              "calculatedOrderQuantity": "TOTALS/Partials"
            },
            {
              "services": [
                {
                  "code": "CPN",
                  "name": "CPN",
                  "patientsOnTreatment": "1"
                },
                {
                  "code": "Maternity",
                  "name": "Maternity",
                  "patientsOnTreatment": "1"
                },
                {
                  "code": "CCR",
                  "name": "CCR",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Farmacy",
                  "name": "Farmacy",
                  "patientsOnTreatment": "0"
                }
              ],
              "productCode": "Nevirapina 200mg frasco 60 Comps",
              "initialAmount": "Initial Stock Level",
              "received": "Entradas",
              "issued": 0,
              "adjustment": "Losses and Adjustments",
              "inventory": "Final Stock",
              "validate": 0,
              "requestAmount": 0,
              "approvedAmount": 0,
              "calculatedOrderQuantity": "TOTALS/Partials"
            },
            {
              "services": [
                {
                  "code": "CPN",
                  "name": "CPN",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Maternity",
                  "name": "Maternity",
                  "patientsOnTreatment": "5"
                },
                {
                  "code": "CCR",
                  "name": "CCR",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Farmacy",
                  "name": "Farmacy",
                  "patientsOnTreatment": "0"
                }
              ],
              "productCode": "Zidovudina sol. Oral 100 ou 240ml",
              "initialAmount": "Initial Stock Level",
              "received": "Entradas",
              "issued": 0,
              "adjustment": "Losses and Adjustments",
              "inventory": "Final Stock",
              "validate": 0,
              "requestAmount": 0,
              "approvedAmount": 0,
              "calculatedOrderQuantity": "TOTALS/Partials"
            },
            {
              "services": [
                {
                  "code": "CPN",
                  "name": "CPN",
                  "patientsOnTreatment": "0"
                },
                {
                  "code": "Maternity",
                  "name": "Maternity",
                  "patientsOnTreatment": "39"
                },
                {
                  "code": "CCR",
                  "name": "CCR",
                  "patientsOnTreatment": "59"
                },
                {
                  "code": "Farmacy",
                  "name": "Farmacy",
                  "patientsOnTreatment": "26"
                }
              ],
              "productCode": "Nevirapina Sol. Oral 240ml",
              "initialAmount": "Initial Stock Level",
              "received": "Entradas",
              "issued": 0,
              "adjustment": "Losses and Adjustments",
              "inventory": "Final Stock",
              "validate": 0,
              "requestAmount": 0,
              "approvedAmount": 0,
              "calculatedOrderQuantity": "TOTALS/Partials"
            }
          ],
          "regimens": [
            {
              "name": "PTV Mulheres OpA+",
              "code": "**",
              "type": "Adults",
              "patientsOnTreatment-isCustom": "type Paediatrics",
              "isCustom": true,
              "hf": 0,
              "chw": 0
            },
            {
              "name": "PTV Criancas OpA+",
              "code": "**",
              "patientsOnTreatment": "type Paediatrics",
              "isCustom": true,
              "hf": 0,
              "chw": 0
            }
          ]
        };
        $scope.productNumber = $scope.rnr.products.length;
        $scope.serviceNumber = $scope.rnr.products[0].services.length;
        $scope.actualPeriodStartDate = "1/02/2011";
        $scope.actualPeriodEndDate = "28/02/2011";

        // $scope.initMonth();
        // $scope.initDate();
        $scope.initContent();

        // parseSignature($scope.rnr.rnrSignatures);

        downloadPdfService.init($scope, $scope.rnr.id);
        downloadSimamService.init($scope, $scope.rnr.id);
    });
  };

  $scope.initContent = function () {
    var content = [];
    var head = ["Initial STOCK LEVEL"];
    content.push(head);

    _.map($scope.rnr.products, function (product) {
      var row = [];
      row.push(product.initialAmount);
      _.map(product.services, function (service) {
        arrayAdd(head, service.name);
        row.push(service.patientsOnTreatment);
      });
      for (var i = 0; i < $scope.serviceNumber; i++) {
        row.push("");
      }
      row.push(product.calculatedOrderQuantity);
      row.push(product.received);
      row.push(product.adjustment);
      row.push(product.inventory);
      content.push(row);
    });

    for (var i = 0; i < $scope.serviceNumber; i++) {
      head.push("");
    }
    head.push("TOTALS / Partials");
    head.push("Entradas");
    head.push("Losses and Adjustments");
    head.push("Final Stock");
    $scope.content = format(content);
  };

  $scope.initMonth = function () {
    var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
    $scope.month = messageService.get(month);
  };

  $scope.initDate = function () {
    $scope.submittedDate = DateFormatService.formatDateWithLocaleNoDay($scope.rnr.submittedDate);
  };

  function parseSignature(signatures) {
    _.forEach(signatures, function (signature) {
      if (signature.type == "SUBMITTER") {
        $scope.submitterSignature = signature.text;
      } else if (signature.type == "APPROVER") {
        $scope.approverSignature = signature.text;
      }
    });
  }

  function arrayAdd(array, value) {
    if (array.indexOf(value) === -1) {
      array.push(value);
    }
  }

  function format(content) {
    var result = [];
    for (var j = 0; j < content[0].length; j++) {
      var row = [];
      for (var i = 0; i < content.length; i++) {
        row.push(content[i][j]);
      }
      result.push(row);
    }
    return result;
  }

}

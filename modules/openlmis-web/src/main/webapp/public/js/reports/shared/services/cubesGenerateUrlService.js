services.factory('CubesGenerateUrlService', function () {
    var baseUrl = "/cubesreports/cube/";

    var generateAggregateUrl = function (cubesName, drillDowns, cuts) {
        return baseUrl + cubesName + "/aggregate" + "?drilldown=" + drillDowns.join("|") + "&cut=" + generateParamsWithSymbols(cuts, ":", ";", "|");
    };

    var generateFactsUrl = function (cubesName, cuts) {
        return baseUrl + cubesName + "/facts" + "?cut=" + generateParamsWithSymbols(cuts, ":", ";", "|");
    };

    var generateFactsUrlWithParams = function (cubesName, cuts, params) {
        return generateFactsUrl(cubesName, cuts) + '&' + generateParamsWithSymbols(params, "=", ",", "&");
    };

    function generateParamsWithSymbols(params, firstSymbol, secondSymbol, thirdSymbol) {
        return _.map(params, function (param) {
            return param.dimension + firstSymbol + param.values.join(secondSymbol);
        }).join(thirdSymbol);
    }

    return {
        generateAggregateUrl: generateAggregateUrl,
        generateFactsUrl: generateFactsUrl,
        generateFactsUrlWithParams: generateFactsUrlWithParams
    };
});
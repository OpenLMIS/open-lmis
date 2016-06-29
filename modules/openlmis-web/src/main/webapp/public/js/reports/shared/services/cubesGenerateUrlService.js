services.factory('CubesGenerateUrlService', function () {
    var baseUrl = "/cubesreports/cube/";
    var cubesSpecialCharacters = "-|;:,";

    var generateAggregateUrl = function (cubesName, drillDowns, cuts) {
        if (drillDowns.length > 0) {
            return baseUrl + cubesName + "/aggregate" + "?drilldown=" + drillDowns.join("|") + "&cut=" + generateCuts(cuts);
        } else {
            return baseUrl + cubesName + "/aggregate" + "?cut=" + generateCuts(cuts);
        }
    };

    var generateFactsUrl = function (cubesName, cuts) {
        return baseUrl + cubesName + "/facts" + "?cut=" + generateCuts(cuts);
    };

    var generateFactsUrlWithParams = function (cubesName, cuts, params) {
        return generateFactsUrl(cubesName, cuts) + "&" + jQuery.param(params);
    };

    function generateCuts(cuts) {
        return _.map(cuts, function (cut) {
            if (!cut.skipEscape) {
                cut.values = _.map(cut.values, function (value) {
                    return escapeCubesSpecialCharacters(value);
                });
            }
            return cut.dimension + ":" + cut.values.join(';');
        }).join("|");
    }

    function escapeCubesSpecialCharacters(stringOrArray) {
        if (typeof stringOrArray === "string") {
            return encodeURIComponent(_.map(stringOrArray, function (char) {
                if (cubesSpecialCharacters.indexOf(char) != -1) {
                    return "\\" + char;
                } else {
                    return char;
                }
            }).join(""));
        } else {
            return _.map(stringOrArray, function (str) {
                return escapeCubesSpecialCharacters(str);
            });
        }
    }

    return {
        generateAggregateUrl: generateAggregateUrl,
        generateFactsUrl: generateFactsUrl,
        generateFactsUrlWithParams: generateFactsUrlWithParams
    };
});
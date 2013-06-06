/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*global jasmineGradle: true, $: false */

jasmineGradle = (function () {
  var specRunners = [];

  var updateTimer;

  var globalStatus = "loading";

  var add = function (pathToSpecRunner) {
    specRunners.push(pathToSpecRunner);
  };

  var buildDivsForSpecRunners = function () {
    var containerDiv = $("#runnerContainer");
    containerDiv.empty();
    $.each(specRunners, function (i, path) {
      var div = $("<div />").addClass("runnerDiv").appendTo(containerDiv);
      div.append($("<div />").append(titleFor(i, path)));
      div.append(iframeFor(i, path));
      div.append($("<div />").append(reloadButton(i, path)));
    });
  };

  var titleFor = function (i, path) {
    return $("<a />").addClass("runnerTitle")
      .attr("href", "#")
      .attr("onclick", "window.open('" + path + "');")
      .append(path);
  };

  var reloadButton = function (i, path) {
    return $("<button />", { type:"button",
      "href":"#",
      text:"Reload"})
      .addClass("runnerReload")
      .click(function () {
        runSpec(i);
      });
  };

  var iframeFor = function (i, path) {
    return $("<iframe />").attr("name", "frame" + i)
      .attr("id", "frame" + i)
      .attr("src", path)
      .append("No iframes support");
  };

  var runAllSpecs = function () {
    $.each(specRunners, function (i, value) {
      runSpec(i);
    });
  };

  var runSpec = function (i) {
    var iframe = $("#frame" + i);
    iframe[0].contentWindow.location.reload(true);
    setTimeout(function () {
      update();
    }, 10);
    setTimeout(function () {
      update();
    }, 100);
  };

  var mySome = function (collection, predicate) {
    var result = false;
    $.each(collection, function (i, x) {
      result = result || predicate(x);
    });
    return result;
  };

  var checkRunner = function (runnerFrameName) {
    var node = document.getElementById(runnerFrameName).contentWindow;
    if (node && node.apiReporter && node.apiReporter.finished) {
      var results = [];
      $.each(node.apiReporter.results(), function (i, r) {
        results.push(r);
      });
      if (mySome(results, function (x) {
        return x.result === "failed";
      })) {
        return "failed";
      }
      else {
        return "passed";
      }
    }
    else {
      return "running";
    }
  };

  var runnningStatus = "running";
  var failedStatus = "failed";

  var update = function () {
    var status = "running";
    var stats = [];
    $.each(specRunners,
      function (i, val) {
        stats.push(checkRunner("frame" + i));
      });
    var busy = mySome(stats, function (x) {
      return x == "running";
    });
    var failures = mySome(stats, function (x) {
      return x == "failed";
    });
    if (failures) {
      if (busy) {
        status = "failing";
      }
      else {
        status = "failed";
      }
    }
    else {
      if (!busy) {
        status = "passed";
      }
    }
    setStatus(status);
    if (busy) {
      if (updateTimer) {
        clearTimeout(updateTimer);
      }
      updateTimer = setTimeout(function () {
        update();
      }, 1000);
    }
    else {
      try {
        results = getResults();
      } catch (x) {
      }
    }
  };

  var setStatus = function (value) {
    globalStatus = value;
    $("#headline").attr("class", globalStatus);
    $("#subheadline").empty().append(globalStatus);
  };

  var getStatus = function () {
    return globalStatus;
  };

  /*    jgself.getBigText = function() {
   var result = "";
   $.each( specRunners, function( i, x ) {
   var iframe = $( "#frame" + i );
   result += "----\n  For Spec Runner " + i + "\n----\n";
   result += iframe[0].contentWindow.document.body.innerHTML;
   result += "----\n  End for Spec Runner " + i + "\n----\n";
   } );
   return result;
   };
   */

  var specPaths = function (reporter) {
    var result = [];
    var path = "/";
    addSubpaths(reporter, result, path, reporter.suites());
    return result;
  };

  var addSubpaths = function (reporter, accum, pathSoFar, specsOrSuites) {
    $.each(specsOrSuites, function (i, x) {
      var mypath = pathSoFar + ":" + x.name;
      if (x.children && x.children.length > 0) {
        addSubpaths(reporter, accum, mypath, x.children);
      }
      if (x.type === "spec") {
        accum.push({ path:mypath, specId:x.id });
      }
    });
  };

  var results;

  var getResults = function () {
    var result = [];
    $.each(specRunners, function (i, x) {
      var iframe = $("#frame" + i);
      var reporter = iframe[0].contentWindow.apiReporter;
      var sps = specPaths(reporter);
      $.each(sps, function (j, y) {
          var specResult = reporter.resultsForSpecs([y.specId])[y.specId];
          $.each(specResult.messages, function (a, b) {
            result.push({
              page:i,
              path:y.specId,
              passed:b.passed,
              message:b.message,
              stacktrace:b.trace.stack
            });
          });
        }
      );
    });
    return result;
  };

  var getResultsAsText = function (includeTrace) {
    var result = "";
    $.each(results, function (i, x) {
      result += "Page " + x.page + ", Spec " + x.path + ": "
        + (x.passed ? "passed" : "FAILED") + "\n";
      result += x.message + "\n";
      if (includeTrace) {
        result += "stacktrace: " + x.stacktrace + "\n";
      }
      ;
      result += "-----\n";
    });
    return result;
  };

  var sourceDisplay = function (path) {
    var lastSlash = path.lastIndexOf("/");
    if (path.length < 10 || lastSlash < 0) {
      return path;
    }
    while (path.length - lastSlash < 3) {
      lastSlash = path.substring(0, lastSlash - 3).lastIndexOf("/");
    }
    return "..." + path.substring(lastSlash + 1);
  };

  var getJsLintResultsAsText = function () {
    var result = "";
    var passCount = 0;
    var failCount = 0;
    $.each(specRunners, function (i, x) {
      var iframe = $("#frame" + i);
      var reporter = iframe[0].contentWindow.apiReporter;
      var runnerResult = "";
      var anyFailure = false;
      var anyRun = false;
      if (typeof( reporter.jslintResults ) !== "undefined") {
        $.each(reporter.jslintResults.summaries, function (j, y) {
          anyRun = true;
          anyFailure = anyFailure || y.error;
          result += "  " + sourceDisplay(y.src) + ": "
            + (y.error ? "failed" : "passed") + "\n";
        });
      }
      result += "Runner " + i + ": ";
      if (anyRun) {
        anyFailure ? ++failCount : ++passCount;
        result += (anyFailure ? "Failed" : "Passed" ) + "\n";
      }
      else {
        result += "None\n";
      }
      result += runnerResult;
    });
    return "JSLint: " + failCount + " Failed, " + passCount + " Passed\n" + result;
  };

  var override = function (memberName, newValue) {
    memberName = newValue;
  };

  return { add:add,
    buildDivsForSpecRunners:buildDivsForSpecRunners,
    runAllSpecs:runAllSpecs,
    getResultsAsText:getResultsAsText,
    getJsLintResultsAsText:getJsLintResultsAsText,
    getResults:getResults,
    getStatus:getStatus,
    update:update
  };

})();

$(document).ready(function () {
  jasmineGradle.buildDivsForSpecRunners();
  jasmineGradle.update();
});


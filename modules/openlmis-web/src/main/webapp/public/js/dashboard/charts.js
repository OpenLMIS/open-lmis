var Charts = function () {

    return {
		//main function to initiate the module

        init: function () {

            App.addResponsiveHandler(function () {
                 Charts.initPieCharts(); 
            });
            
        },
        initBarCharts: function () {

            // bar chart:
          
            var ticks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"]];
            
             var data1 = GenerateSeries(0);
             
        var dataset = [{ label: "Random Tabs Data Size", data: data1, color: "#5482FF" }];
      
     
            function GenerateSeries(added){
                var data = [];
                var start = 0 + added;
                var end = 100 + added;
         
                for(i=1;i<=3;i++){        
                    var d = Math.floor(Math.random() * (end - start + 1) + start);        
                    data.push([i, d]);
                    start++;
                    end++;
                }
         
                return data;
            }
            
                     
            var options = {
                    series:{
                        bars:{show: true}
                    },
                    bars:{
						  align:"center",
                          barWidth:0.5
                    },
                    xaxis:{
						axisLabel: "Sample tabs",
						 axisLabelUseCanvas: true,
						 axisLabelFontSizePixels: 12,
						 axisLabelFontFamily: 'Verdana, Arial',
						axisLabelPadding: 10,
						ticks:ticks
					},
					yaxis: {
						axisLabel: "Data Size",
						axisLabelUseCanvas: true,
						axisLabelFontSizePixels: 12,
						axisLabelFontFamily: 'Verdana, Arial',
						axisLabelPadding: 3,
						tickFormatter: function (v, axis) {
							return v + "kb";
						}
					},
					legend: {
						noColumns: 0,
						labelBoxBorderColor: "#000000",
						position: "nw"
					},
                    grid:{
						clickable:true,
						hoverable: true,
						borderWidth: 2,
						backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
                    }
            };
 
            $.plot($("#bar_chart_1"), dataset, options);
			$("#bar_chart_1").bind("plotclick", function (event, pos, item) {
   
			var showTab = 2- item.dataIndex;
			$('#dashboard-tabs li:eq('+showTab+') a').tab('show');    
			});			
            
        },
        initPieCharts: function () {
			var data = [];
            var series = 3;
            var colors = ["#05BC57","#CC0505", "#FFFF05"];
            var labels = ["Reported on time","Did not report","Reported late"];
            
            for (var i = 0; i < series; i++) {
                data[i] = {
                    label: labels[i],
                    data: Math.floor(Math.random() * 100) + 1,
                    color: colors[i]
                }
            }
            
            $.plot($("#pie_chart_1"), data, {
                    series: {
                        pie: {
                            show: true,
                            radius: 1,
                            
                            label: {
                                show: true,
                                radius: 2 / 3,
                                formatter: function (label, series) {
                                    return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                                },
                                threshold: 0.1
                            }
                        }
                    },
                    legend: {
                        show: true
                    }
                });

		}
      };

}();

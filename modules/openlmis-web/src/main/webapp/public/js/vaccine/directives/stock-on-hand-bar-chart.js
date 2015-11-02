/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

app.directive('stockchart', ['messageService',function(messageService) {
    return{
            restrict: 'EA',
            template: '<div ng-show="showGraph" ></div> <div class="chart-tooltip" style="display: none;z-index:532;position: absolute;width: auto;line-height: 20px;padding: 10px;font-size: 14px;text-align: center;color:#333;background: #FFF;border: 1px solid #ccc;border-radius: 5px;text-shadow: rgba(0, 0, 0, 0.0980392) 1px 1px 1px;box-shadow: rgba(0, 0, 0, 0.0980392) 1px 1px 2px 0px;"></div>',
            link: function(scope, elem, attrs){
                var stockCards=null;
                var forecasts=null;
                var xlabel=messageService.get('label.stock.chart.vaccine');
                var ylabelDoses=messageService.get('label.stock.chart.doses');
                var ylabelMos=messageService.get('label.stock.chart.mos');
                var type='vaccine';

                plotArea = $(elem.children()[0]);
                plotArea.css({
                        width: attrs.width,
                        height: attrs.height
                      });
                var stock = null,
                    options = {
                                series:{
                                   stack: true,
                                   bars: {
                                       show: true,
                                       clickable: true,
                                       barWidth: 0.3,
                                       align: "center",
                                       fill:1,
                                       label:{
                                            color:'CCCCCC'
                                        }
                                    }
                                  },
                                axisLabels: {
                                   show: true
                                   },
                                xaxis: {
                                    axisLabel: xlabel,
                                    axisLabelUseCanvas: true,
                                    axisLabelFontSizePixels: 12,
                                    axisLabelPadding: 5,
                                    mode: "categories",
                                    tickLength: 0
                                },
                                yaxis: {
                                     axisLabel: ylabelDoses,
                                     axisLabelUseCanvas: true,
                                     axisLabelFontSizePixels: 12,
                                     axisLabelPadding: 5
                                },
                                grid: {
                                  labelMargin: 10,
                                  backgroundColor: '#ffffff',
                                  color: '#333333',
                                  borderColor: '#CCCCCC',
                                  hoverable: true,
                                  clickable: true
                                }

                            };

                var data = scope[attrs.ngModel];
                // If the data changes somehow, update it in the chart
                scope.$watch('data', function(v){
                     stockCards=v.stockcards;
                     if(v.forecasts !== undefined){
                        forecasts=v.forecasts;
                     }

                     options.yaxis.axisLabel=ylabelDoses;
                     if( forecasts !== null) {
                        options.yaxis.axisLabel=ylabelMos;
                     }
                     var graphData=[];
                     if(stockCards !== null )
                     {
                         stockCards.forEach( function (c)
                                             {
                                                 var d={};
                                                 var data=[];
                                                 var color=null;
                                                 var n=c.product.primaryName;
                                                 var q=c.totalQuantityOnHand;
                                                 var id=c.product.id;


                                                 var f=_.find(forecasts, function(forecast){ if(forecast.product.id==id) return forecast;});
                                                 if(f !==undefined)
                                                 {

                                                     //Set Colors
                                                     if(q<=f.forecast.max && q>f.forecast.orderLevel)
                                                     {
                                                        color="green";
                                                     }
                                                     else if(q<=f.forecast.orderLevel && q>f.forecast.buffer)
                                                     {
                                                        color="#e5e500";
                                                     }
                                                     else if(q<=f.forecast.buffer)
                                                     {
                                                        color="red";
                                                     }
                                                     //Calculate MOS
                                                     q=q/f.forecast.buffer;
                                                 }
                                                 var s=[n,q];
                                                 data.push(s);
                                                 d.data=data;
                                                 d.color=color;
                                                 graphData.push(d);
                                              });


                     }

                     if(!stock){
                        stock = $.plot(plotArea, graphData , options);
                        elem.show();
                    }else{
                        stock.setData(graphData);
                        stock.setupGrid();
                        stock.draw();
                    }
                });

                 plotArea.bind("plothover", function(event, pos, item) {
                     var tip = $('.chart-tooltip');
                     if (item) {
                        var c=_.find(stockCards, function(card){ if(card.product.primaryName==item.series.data[0][0]) return card;});
                        var offset = stock.getPlotOffset();
                        var axis = stock.getAxes();
                        var yValue = item.datapoint[1];
                        var xValue = item.datapoint[0];
                        tip.css('left', pos.pageX-60);
                        tip.css('top', pos.pageY-50);
                        tip.html(c.product.primaryName+'='+c.totalQuantityOnHand+' Doses');

                        tip.show();
                     } else {
                        tip.hide();
                     }
                  });

            }
        };
}]);

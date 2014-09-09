/* Copyright Joe Tsoi, FreeBSD-License
 * simple flot plugin to draw bar numbers halfway in bars
 *
 * options are
 * series: {
 *     bars: {
 *         showNumbers: boolean (left for compatibility)
 *         numbers : {
 *             show : boolean,
 *             alignX : number or function,
 *             alignY : number or function,
 *         }
 *     }
 * }
 */
(function ($) {
    var options = {
        bars: {
            numbers: {
            }
        }
    };
    
    function processOptions(plot, options) {
        var bw = options.series.bars.barWidth;
        var numbers = options.series.bars.numbers;
        var horizontal = options.series.bars.horizontal;
        if(horizontal){
            numbers.xAlign = numbers.xAlign || function(x){ return x / 2; };
            numbers.yAlign = numbers.yAlign || function(y){ return y + (bw / 2); };
            numbers.horizontalShift = 0;
        } else {
            numbers.xAlign = numbers.xAlign || function(x){ return x + (bw / 2); };
            numbers.yAlign = numbers.yAlign || function(y){ return y / 2; };
            numbers.horizontalShift = 1;
        }
    }

    function draw(plot, ctx){
        $.each(plot.getData(), function(idx, series) {
            if(series.bars.numbers.show || series.bars.showNumbers){
                var ps = series.datapoints.pointsize;
                var points = series.datapoints.points;
                var ctx = plot.getCanvas().getContext('2d');
                var offset = plot.getPlotOffset();
                ctx.textBaseline = "top";
                ctx.textAlign = "center";
                alignOffset = series.bars.align === "left" ? series.bars.barWidth / 2 : 0;
                xAlign = series.bars.numbers.xAlign;
                yAlign = series.bars.numbers.yAlign;
                var shiftX = typeof xAlign == "number" ? function(x){ return x; } : xAlign;
                var shiftY = typeof yAlign == "number" ? function(y){ return y; } : yAlign;
    
                axes = {
                    0 : 'x',
                    1 : 'y'
                } 
                hs = series.bars.numbers.horizontalShift;
                for(var i = 0; i < points.length; i += ps){
                    barNumber = i + series.bars.numbers.horizontalShift
                    var point = {
                        'x': shiftX(points[i]),
                        'y': shiftY(points[i+1])
                    };
                    if(series.stack != null){
                        point[axes[hs]] = (points[barNumber] - series.data[i/3][hs] / 2);
                        text = series.data[i/3][hs];
                    } else {
                        text = points[barNumber];
                    }
                    var c = plot.p2c(point);
                    ctx.fillText(text.toString(10), c.left + offset.left, c.top + offset.top)
                }
            }
        });
    }
    
    function init(plot) {
        plot.hooks.processOptions.push(processOptions);
        plot.hooks.draw.push(draw);
    }

    $.plot.plugins.push({
        init: init,
        options: options,
        name: 'barnumbers',
        version: '0.4'
    });
})(jQuery);

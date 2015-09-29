var OrderRequisitionLineItem2 = function(stockCards,report){

    $.extend(this, stockCards);

    OrderRequisitionLineItem2.prototype.getQuantityToRequest = function() {
        var quantity = Math.ceil(this.getMaximumStock() - (Number(parseInt(this.stockOnHand,10)) / this.getTotalByVial()) );
        this.quantityRequested = quantity;
        return quantity;

    };

    OrderRequisitionLineItem2.prototype.getMaximumStock = function(){
        var max= Math.ceil((Number(parseInt(this.overriddenisa,10) / this.getTotalByVial()) * Number(this.maxmonthsofstock)));
        this.maximumStock = max;
        return max;
    };
    OrderRequisitionLineItem2.prototype.getMinimumStock = function(){
        var min = Math.ceil( (Number(this.overriddenisa) / this.getTotalByVial()) * (Number(this.minmonthsofstock)/ this.getTotalByVial()));
        this.minimumStock = min;
        return min;
    };

    OrderRequisitionLineItem2.prototype.getReorderLevel= function(){
        var reorder = Math.ceil(((Number(this.overriddenisa) ) * (Number(this.eop)))/ this.getTotalByVial());
        this.reOrderLevel = reorder;
        return reorder;
    };

    OrderRequisitionLineItem2.prototype.getBufferStock = function(){
        var buffer = (this.getReorderLevel() - this.getMaximumStock());
        this.bufferStock = buffer;
        return buffer;

    };

    OrderRequisitionLineItem2.prototype.getTotalByVial = function(){
        return Number(this.product.dosesPerDispensingUnit);
    };

    OrderRequisitionLineItem2.prototype.getTotalStockOnHand = function(){
        return Math.ceil(Number(this.stockOnHand) / this.getTotalByVial());
    };


};

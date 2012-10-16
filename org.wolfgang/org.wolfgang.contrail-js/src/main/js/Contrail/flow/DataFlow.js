/*global define*/

define( [ "../core/jObj" ] , 
function(jObj) {
    
    function DataFlow() {
        jObj.bless(this);
    }

    DataFlow.prototype.handleData = function(Data) {
        // Nothing
    };

    DataFlow.prototype.handleClose = function() {
        // Nothing
    };

    return DataFlow;
});
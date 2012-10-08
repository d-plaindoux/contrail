/*global define*/
	
define([  ], function () {
	
    var Utils = {};
	
    Utils.UUID = function () {
        var S4 = function () {
            return Math.floor(Math.random() * 0x10000).toString(16);
        };

        return (
                S4() + S4() + "-" +
                S4() + "-" +
                S4() + "-" +
                S4() + "-" +
                S4() + S4() + S4()
               );
    };
    
    return Utils;
});

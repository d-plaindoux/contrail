/*global define*/
	
define( [ ], 
function () {
	
    var jDom = function (tag, attributes, content) {
        var key, result;
        
        result =  "<" + tag;
 
        for(key in attributes) {
            result += " " + key + "='" + attributes[key] + "'";
        }

        if (content) {
            result += ">" + content + "</" + tag + ">"; 
        } else {
            result += "/>";
        }        
        
        return result;
    };
        
    return jDom;
});

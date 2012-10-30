/*global require */

require([ "Core/jObj", "Codec/Factory", "qunit" ], function(jObj, Factory, QUnit) {
          
    QUnit.test("String encoding ", function() {
        var bytes = "Hello, World!".split(""), encoder, result;
        encoder = Factory.payload.encoder();
        result = encoder.transform(bytes);        
        QUnit.equal(result.length, 1, "Checking result length");
        QUnit.equal(jObj.instanceOf(result[0], jObj.types.Array), true, "Checking result type");
        QUnit.equal(result[0].length, bytes.length + 4, "Checking encoding length");
    });
});


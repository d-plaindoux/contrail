/*global require */

require([ "Core/jObj", "Codec/Factory", "qunit" ], function(jObj, Factory, QUnit) {
          
    QUnit.test("String decoding", function() {
        var bytes = [0,0,0,13].concat("Hello, World!".split("").concat([0,0,0,50]).concat("Bla".split(""))), decoder, result;
        decoder = Factory.payload.decoder();
        result = decoder.transform(bytes);        
        QUnit.equal(result.length, 1, "Checking result length");
        QUnit.equal(jObj.instanceOf(result[0], jObj.types.Array), true, "Checking result type");
        QUnit.equal(result[0].join(""), "Hello, World!".split("").join(""), "Checking decoding value");
    });
});


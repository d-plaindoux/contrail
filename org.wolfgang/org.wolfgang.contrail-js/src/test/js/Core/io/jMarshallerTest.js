/*global require */

require([ "IO/jMarshaller", "qunit" ], function (jMarshaller, QUnit) {

    QUnit.test("Checking intToBytes length", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b.length, 4, "Size of bytes from int is 4");
    });

    QUnit.test("Checking intToBytes content at index 0", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[0], 0x89, "Content of bytes at index 0");
    });

    QUnit.test("Checking intToBytes content at index 1", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[1], 0xAB, "Content of bytes at index 1");
    });

    QUnit.test("Checking intToBytes content at index 2", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[2], 0xCD, "Content of bytes at index 2");
    });

    QUnit.test("Checking intToBytes content at index 3", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[3], 0xEF, "Content of bytes at index 3");
    });
});

/**
 * Implementation based on the Zinc Abstract Machine
 * --------------------------------------------------
 * From Krivine’s machine to the Caml implementations
 *
 * Xavier Leroy INRIA Rocquencourt
 */

var Zam = {
	state : {
		EPSILON : 0x0,
		ENTRY : 0x1,

		Epsilon : function() {
			this.type = Zam.state.EPSILON;
		},
		Entry : function(code, env) {
			this.type = Zam.state.ENTRY;
			this.code = code;
			this.env = env;
		},

		State : function(code) {
			this.code = code;
			this.env = [];
			this.aStk = [];
			this.rStk = [];
		},
	},

	/**
	 * Abstract machine instructions
	 */

	objcode : {
		/**
		 * Abstract machine instruction types
		 */

		type : {
			ACCESS : 0x0,
			CLOSURE : 0x1,
			TAILAPPLY : 0x2,
			APPLY : 0x3,
			PUSHMARK : 0x4,
			GRAB : 0x5,
			RETURNS : 0x6,
		},

		access : function(n) {
			this.type = Zam.objcode.type.ACCESS;
			this.n = n;
			this.execute = function(state) {
			};
		},

		closure : function(c) {
			this.type = Zam.objcode.type.CLOSURE;
			this.n = c;
		},

		tailApply : function() {
			this.type = Zam.objcode.type.TAILAPPLY;
		},

		apply : function() {
			this.type = Zam.objcode.type.APPLY;
		},

		pushMark : function() {
			this.type = Zam.objcode.type.PUSHMARK;
		},

		grab : function() {
			this.type = Zam.objcode.type.GRAB;
		},

		returns : function() {
			this.type = Zam.objcode.type.RETURNS;
		},
	},

	execute : function(state) {
		var current = code.pop();
		switch (current.type) {
			case Zam.objcode.type.ACCESS: {
				// ACCESS(n);c e s r => c e e(n).s r
				state.aStk.push(env[current.n]);
				break;
			}
			case Zam.objcode.type.CLOSURE: {
				// CLOSURE(cp);c e s r => c e [cp,e].s r
				state.aStk.push(new Zam.state.Entry(current.code, state.env.slice(0)));
				break;
			}
			case Zam.objcode.type.TAILAPPLY: {
				// TAILAPPLY;c e [cp,ep].s r => cp ep s r
				var entry = state.aStk.pop();
				state.code = entry.code;
				state.env = entry.env;
				break;
			}
			case Zam.objcode.type.APPLY: {
				// APPLY;c e [cp,ep].s r => cp ep s c.e.r
				var entry = AStk.pop();
				var entry = state.aStk.pop();
				state.rStk.push(state.env);
				state.rStk.push(state.code);
				state.code = entry.code;
				state.env = entry.env;
				break;
			}
			case Zam.objcode.type.PUSHMARK: {
				// PUSHMARK;c e s r => c e eps.s r
				state.aStk.push(new Zam.state.Epsilon())
			}
			case Zam.objcode.type.GRAB: {
				var value = state.aStk.pop();
				switch (value.type) {
					case Zam.state.EPSILON: {
						// GRAB;c e esp.s cp.ep.r => cp ep [(GRAB;c),e].s r
						state.aStck.puch(new Zam.state.Entry([GRAB, state.code], state.env));
						state.code = state.rStack.pop();
						state.env = state.rStack.pop();
						break;
					}
					case Zam.state.ENTRY: {
						// GRAB;c e v.s r => c v.e s r
						state.env.push(value);
						break;
					}
				}
			}
			case Zam.objcode.type.RETURN: {
				var value = state.aStk.pop();
				switch (state.aStk[0].type) {
					case Zam.state.EPSILON: {
						// RETURN;c e v.esp.s cp.ep.r => cp ep v.s r
						state.aStk.pop(); // Remove EPSILON
						state.aStk.push(value);
						state.code = state.rStack.pop();
						state.env = state.rStack.pop();
						break;
					}
					case Zam.state.ENTRY: {
						// RETURN;c e [cp,ep].s r => cp ep s r 
						state.code = value.code;
						state.env = value.env;
						break;
					}
				}
			}
		}
	},

	executeAll : function(code) {
		var state = new Zam.state.Entry(code);
		while (state.code.lenght > 0) {
			Zam.execute(state);
		}
	}
}

/*
 * Factories
 */

var ACCESS = function(n) {
	return new Zam.objcode.access(n);
}
var CLOSURE = function(c) {
	return new Zam.objcode.closure(c);
}
var TAILAPPLY = new Zam.objcode.tailApply();
var APPLY = new Zam.objcode.apply();
var PUSHMARK = new Zam.objcode.pushMark();
var GRAB = new Zam.objcode.grab();
var RETURN = new Zam.objcode.returns();

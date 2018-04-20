class SparseMap {

	constructor() {

	}

	add(x, y, object) {
		if (!this.hasOwnProperty(x)) {
			this[x] = {};
		}

		if (!this[x].hasOwnProperty(y)) {
			this[x][y] = [];
		}

		this[x][y].push(object);
	}

	get(x, y, def) {

		if (def == undefined) {
			def = null;
		}

		if (!this.hasOwnProperty(x)) {
			return def;
		}

		if (!this[x].hasOwnProperty(y)) {
			return def;
		}

		return this[x][y];
	}

	hasObjsAt(x, y) {
		return this.get(x, y, []).length > 0;
	}

	getFirst(x, y, def) {

		if (def == undefined) {
			def = null;
		}

		return this.get(x, y, [def])[0];
	}
}
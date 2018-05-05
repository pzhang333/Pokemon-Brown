/**
 * Append a prefix to every element in an array.
 */
function appendPrefix(prefix, arr) {
	let newArr = [];

	for(let i = 0; i < arr.length; i++) {
		newArr.push(prefix + arr[i].toString());
	}

	return newArr;
}

function ucfirst(str) {
	return str[0].toUpperCase() + str.substr(1);
}

/**
 * Get a list of integers from [start, end).
 */
function range(start, end, suffix) {
	if (suffix == undefined) {
		suffix = [];
	}
	return (new Array(end - start).fill().map((d, i) => i + start)).concat(suffix);
}

/**
 * Get a value from a map, if the value if undefined, return
 * the specified default value.
 */
function mapGet(map, key, def) {
	let val = map[key];
	if (val == undefined) {
		return def;
	}

	return val;
}

function debounce(container, varName, time) {

	if (container[varName] == undefined) {
		container[varName] = 0;
	}

	if (performance.now() - container[varName] < time) {
		return true;
	}

	container[varName] = performance.now();
	return false;
}

function getFromMatrix(matrix, x, y, def) {
	if (matrix == undefined) {
		return def;
	}

	let row = matrix[y];
	if (row == undefined) {
		return def;
	}

	let value = row[x];
	if (value == undefined) {
		return def;
	}

	return value;
}

function getCoordinatesFromTile(x, y) {
	let corX = Game.map.tileWidth*x;
	let corY = Game.map.tileHeight*y;

	return new Phaser.Point(corX, corY);
}

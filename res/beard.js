
window.beard.bus_bind = function(sink, selector, type) {
	var x = $(selector);
	if (x.length==0) return null;
	var fn = function(evt) {
		sink.write(evt);
	};
	x.bind(type, fn);
	return fn;
};

window.beard.bus_unbind = function(selector, type, fnptr) {
	$(selector).unbind(type, fnptr);
};


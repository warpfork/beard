
window.beard.internal = {}

window.beard.internal.bus_bind = function(routekey, sink, selector, type) {
	var x = (selector == "document") ? $(document) : $(selector);
	if (x.length==0) return null;
	var fn = function(evt) {
		sink.write(
				routekey,
			// decompose event before passing across pass,
			//  since otherwise the java side of the bridge gets stuck with the overhead of numerous round trips back across the bridge,
			//  one for every single property that needed to be obtained.
				evt.type,
				evt.target.id,
				0+ evt.timeStamp,
				0+ evt.screenX,
				0+ evt.screenY,
				0+ evt.pageX,
				0+ evt.pageY,
				0+ evt.clientX,
				0+ evt.clientY,
				!! evt.shiftKey,
				!! evt.metaKey,
				!! evt.ctrlKey,
				!! evt.altKey,
				0+ evt.button,
				0+ evt.which
		);
	};
	x.bind(type, fn);
	return fn;
};

window.beard.internal.bus_unbind = function(selector, type, fnptr) {
	$(selector).unbind(type, fnptr);
};


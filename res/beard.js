// perhaps Beard will cat a variable declaration into the top of this with the applet id?
//   i can't think of how else to do it without resorting to global variables in the js realm that would make it darn tough to use more than one beard applet per page.
// or, you could dodge the direct issue by never creating new java objects and using that pointer to sink (or other part already from the java application) arduously.
// i guess as long as i can keep this object stateless, i could be okay with not having it namespaced.  then it would be possible for there to be two applets using beard in the same page as long as they weren't using radically different versions from one another.

window.beard.bus_bind = function(sink, selector, type) {
	var x = $(selector);
	if (x.length==0) return null;
	var fn = function() {
		sink.write(new ohGodTheAppletId.Packages.us.exultant.beard.IngressEvent(...));
	};
	x.bind(type, fn);
	return fn;
};

window.beard.bus_unbind = function(selector, type, fnptr) {
	// it seems perhaps silly to have to make a global function for such a short snippet, but it's absolutely necessarily in order for us to be able to use JSObject.call() from java in such a way to communicate that fnptr correctly.
	$(selector).unbind(type, fnptr);
};


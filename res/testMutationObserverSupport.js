(function(window) {
	//  http://stackoverflow.com/questions/2844565/is-there-a-jquery-dom-change-listener
	
	var ourDomRoot = $('<div>').attr('id','testMutationObserverSupport')
		.css('border','1px solid #F0F');
	$('#main').append(ourDomRoot);
	
	var ourDomLog = $('<div>').attr('id','testMutationObserverSupport-log')
		.css('border','1px dotted #F0F');
	ourDomRoot.append(ourDomLog);
	
	
	
	ourDomLog.append("<li>TEST window.MutationObserver ... "+window.MutationObserver);
	ourDomLog.append("<li>TEST window.WebKitMutationObserver ... "+window.WebKitMutationObserver);
	
	function TestPrototype () {
		this.evtCount = 0;
		this.resultval = "<font color=red>##test_result_not_collected##</font>"
		this.name = function () { return "##unnamed_test##"; };
		this.runTest = function() { this.resultval = this.test(); };
		this.test = function () { return "<font color=red>##test_method_not_implemented##</font>"; };
		this.result = function () { return this.resultval; };
	};
	var tests = [];
	function newDomSandbox() {
		$('#testMutationObserverSupport-sandbox').remove();
		var sandbox = $('<div>').attr('id','testMutationObserverSupport-sandbox').hide();
		ourDomRoot.append(sandbox);
		return sandbox;
	}
	
	
	
	tests.push((function () {
		var aTest = new TestPrototype();
		aTest.name = function () {
			return "testMutationObserverSupport";
		}
		aTest.test = function () {
			MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
			if (!MutationObserver) return "<font color=red>NOPE</font>, not even an option";
			
			var sandbox = newDomSandbox();
			
			aMutationObserver = new MutationObserver(
				function(mutations, observer) {
					aTest.evtCount++;
				}
			);
			aMutationObserver.observe(sandbox[0], {
				  subtree: true,
				childList: true
			});
			
			sandbox.append("<span>hey there</span>");
			sandbox.append("<span>hey there</span>");
			sandbox.append("<span>hey there</span>");
		};
		aTest.result = function () {
			return (aTest.evtCount > 0 ? "<font color=green>POSSIBLE</font>" : "<font color=red>NOPE</font>")+": evtCount="+aTest.evtCount;
		};
		return aTest;
	})());
	
	
	
	tests.push((function () {
		var aTest = new TestPrototype();
		aTest.name = function () {
			return "testDOMSubtreeModified";
		}
		aTest.test = function () {
			aTest.evtCount = 0;
			var sandbox = newDomSandbox();
			
			sandbox.bind("DOMSubtreeModified", function() {
				console.log("!!! DOMSubtreeModified");
				aTest.evtCount++;
			});
			
			sandbox.append("<span>hey there</span>");
		};
		aTest.result = function () {
			return (aTest.evtCount > 0 ? "<font color=green>POSSIBLE</font>" : "<font color=red>NOPE</font>")+": evtCount="+aTest.evtCount;
		};
		return aTest;
	})());
	// DOMSubtreeModified doesn't give you the right element
	
	
	
	tests.push((function () {
		var aTest = new TestPrototype();
		aTest.name = function () {
			return "testDOMNodeInserted";
		}
		aTest.test = function () {
			aTest.evtCount = 0;
			var sandbox = newDomSandbox();
			
			sandbox.bind("DOMNodeInserted", function() {
				console.log("!!! DOMNodeInserted");
				aTest.evtCount++;
			});
			
			sandbox.append("<span>hey there</span>");
			sandbox.append("<span>hey there</span>");
		};
		aTest.result = function () {
			return (aTest.evtCount > 0 ? "<font color=green>POSSIBLE</font>" : "<font color=red>NOPE</font>")+": evtCount="+aTest.evtCount;
		};
		return aTest;
	})());
	// DOMNodeInserted does give the right element, but it also fires for every mutation, which. whut.  attributes and text.
	//  ohgod, it's stupider than that even.  it appears to give the child of the node you put the watcher on... when any child of that child changes in any way.  unless this is an artifact of how firebug applies its changes, which i'm doubting.
	
	
	
	for (var i in tests) {
		var test = tests[i];
		test.resultDomId = "testMutationObserverSupport-result-"+test.name();
		ourDomLog.append("<li>RUNNING TEST " + test.name() + " ... <span id='"+test.resultDomId+"'></span>");
		test.runTest();
		setTimeout(function(test, resultDomId) {
			$('#'+test.resultDomId).html(test.result());
		}, 500, test);
	}
})(this);

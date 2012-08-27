package us.exultant.beard;

import us.exultant.ahs.util.*;

public class BeardStandalone extends Beard {
	public BeardStandalone() {
		super(BeardStandaloneWindow.make());
	}
	
	public static void main(String[] args) {
		Beard $beard = new BeardStandalone();
		$beard.normalizePage();
		
		// prove beard works and can touch js at all
		$beard.eval("$('#main').html('ohai!');");
		
		while (true) X.chill(1);
	}
}

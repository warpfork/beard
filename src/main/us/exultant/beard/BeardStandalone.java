package us.exultant.beard;

public class BeardStandalone extends BeardWebview {
	public BeardStandalone() {
		super(BeardStandaloneWindow.make());
	}
	
	public static void main(String[] args) {
		Beard $beard = new BeardStandalone();
	}
}

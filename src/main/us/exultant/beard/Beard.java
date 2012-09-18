package us.exultant.beard;

public interface Beard {
	public Object eval(String... $strs);
	public BeardBus bus();
	public void console_log(Object... $msgs);
}

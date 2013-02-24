package us.exultant.beard;

public interface Beard {
	public Object eval(String... $strs);
	public BeardBus bus();
	public BeardAssetLoader assetLoader();
	public void console_log(Object... $msgs);
}

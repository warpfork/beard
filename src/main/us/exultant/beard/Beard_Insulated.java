package us.exultant.beard;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import java.util.concurrent.*;
import netscape.javascript.*;
import javafx.application.*;

class Beard_Insulated extends BeardImpl {
	Beard_Insulated(BeardImpl $direct) {
		this.$direct = $direct;
		this.$pipe = new DataPipe<Ackable<Cmd>>();
		this.$doer = new Doer();
		$pipe.source().setListener(new Listener<ReadHead<Ackable<Cmd>>>() {
			public void hear(ReadHead<Ackable<Cmd>> $arg0) {
				Platform.runLater($doer);
			}
		});
	}
	
	private final BeardImpl $direct;
	private final Pipe<Ackable<Cmd>> $pipe;
	private final Doer $doer;
	
	private static interface Cmd {}
	private static class CmdEval implements Cmd {
		String[] $strs;
		Object $ret;
	}
	private static class CmdLog implements Cmd {
		Object[] $msgs;
	}
	private static class CmdCall implements Cmd {
		JSObject $context;
		String $function;
		Object[] $args;
		Object $ret;
	}
	
	public Object eval(String... $strs) {
		CmdEval $cmd = new CmdEval();
		$cmd.$strs = $strs;
		execAndWait($cmd);
		return $cmd.$ret;
	}
	
	Object call(JSObject $context, String $function, Object... $args) {
		CmdCall $cmd = new CmdCall();
		$cmd.$context = $context;
		$cmd.$function = $function;
		$cmd.$args = $args;
		execAndWait($cmd);
		return $cmd.$ret;
	}
	
	JSObject jsb() {
		return $direct.jsb();
	}
	
	public BeardBus bus() {
		return $direct.bus();
	}
	
	public void console_log(Object... $msgs) {
		CmdLog $cmd = new CmdLog();
		$cmd.$msgs = $msgs;
		execAndWait($cmd);
	}
	
	private void execAndWait(Cmd $cmd) {
		Ackable<Cmd> $ackable = new Ackable<Cmd>($cmd);
		$pipe.sink().write($ackable);
		try {
			$ackable.getWorkFuture().get();
		} catch (ExecutionException $e) {
			throw new MajorBug($e);
		} catch (InterruptedException $e) {
			throw new Error($e);
		}
	}
	
	private class Doer implements Runnable {
		public void run() {
			Ackable<Cmd> $ackable = $pipe.source().readNow();
			if ($ackable == null) return;
			Cmd $cmd = $ackable.getPayload();
			if ($cmd.getClass() == CmdEval.class) {
				((CmdEval)$cmd).$ret = $direct.eval(((CmdEval)$cmd).$strs);
			} else if ($cmd.getClass() == CmdLog.class) {
				$direct.console_log(((CmdLog)$cmd).$msgs);
			} else if ($cmd.getClass() == CmdCall.class) {
				CmdCall $call = (CmdCall)$cmd;
				$call.$ret = $call.$context.call($call.$function, $call.$args);
			}
			$ackable.ack();
		}
	}
	
	/** This method can be used to run an item from this insulator's queue of commands, but it is only safe to run from the JavaFX thread. */
	void push() {
		$doer.run();
	}
}

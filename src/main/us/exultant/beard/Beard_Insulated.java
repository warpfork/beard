package us.exultant.beard;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import java.util.concurrent.*;
import javafx.application.*;

class Beard_Insulated implements Beard {
	Beard_Insulated(Beard_Direct $direct) {
		// save the pointer to the direct implementation, because we need it to actually act on
		this.$direct = $direct;

		// initialize structures used in thread insulation
		this.$pipe = new DataPipe<Ackable<Cmd>>();
		this.$doer = new Doer();
		$pipe.source().setListener(new Listener<ReadHead<Ackable<Cmd>>>() {
			public void hear(ReadHead<Ackable<Cmd>> $arg0) {
				Platform.runLater($doer);
			}
		});
		
		// initialize other subsystems
		$bus = new BeardBus_Insulated();
		$assetLoader = new BeardAssetLoader(this);
	}
	
	private final Beard_Direct $direct;
	private final Pipe<Ackable<Cmd>> $pipe;
	private final Doer $doer;
	private final BeardBus_Insulated $bus;
	private final BeardAssetLoader $assetLoader;
	
	private static interface Cmd {}
	private static class CmdEval implements Cmd {
		String[] $strs;
		Object $ret;
	}
	private static class CmdLog implements Cmd {
		Object[] $msgs;
	}
	private static class CmdBusBind implements Cmd {
		String $selectorString;
		DomEvent.Type $type;
		ReadHead<DomEvent> $ret;
	}
	private static class CmdBusUnbind implements Cmd {
		ReadHead<DomEvent> $bound;
		boolean $ret;
	}
	
	public Object eval(String... $strs) {
		CmdEval $cmd = new CmdEval();
		$cmd.$strs = $strs;
		execAndWait($cmd);
		return $cmd.$ret;
	}
	
	public void console_log(Object... $msgs) {
		CmdLog $cmd = new CmdLog();
		$cmd.$msgs = $msgs;
		execAndWait($cmd);
	}
	
	public BeardBus bus() {
		return $bus;
	}

	public BeardAssetLoader assetLoader() {
		return $assetLoader;
	}
	
	private class BeardBus_Insulated extends BeardBus {
		public ReadHead<DomEvent> bind(String $selectorString, DomEvent.Type $type) {
			CmdBusBind $cmd = new CmdBusBind();
			$cmd.$selectorString = $selectorString;
			$cmd.$type = $type;
			execAndWait($cmd);
			return $cmd.$ret;
		}
		
		public boolean unbind(ReadHead<DomEvent> $bound) {
			CmdBusUnbind $cmd = new CmdBusUnbind();
			$cmd.$bound = $bound;
			execAndWait($cmd);
			return $cmd.$ret;
		}

		WorkTarget<Void> getWorkTarget() {
			return $direct.bus().getWorkTarget();
		}
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
			} else if ($cmd.getClass() == CmdBusBind.class) {
				CmdBusBind $cmd2 = (CmdBusBind)$cmd;
				$cmd2.$ret = $direct.bus().bind($cmd2.$selectorString, $cmd2.$type);
			} else if ($cmd.getClass() == CmdBusUnbind.class) {
				CmdBusUnbind $cmd2 = (CmdBusUnbind)$cmd;
				$cmd2.$ret = $direct.bus().unbind($cmd2.$bound);
			}
			$ackable.ack();
		}
	}
	
	/** This method can be used to run an item from this insulator's queue of commands, but it is only safe to run from the JavaFX thread. */
	void push() {
		$doer.run();
	}
}

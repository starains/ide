package com.teamide.deployer.processor;

public class TaskProcessor extends Processor implements Runnable {

	public TaskProcessor(TaskBean task) {
		super(task);
	}

	@Override
	public void run() {
		process();
	}

	public void process() {
		if (task == null) {
			return;
		}
		if (task.getType() == null) {
			return;
		}

		switch (task.getType()) {
		case START:
			processStart();
			break;
		case STOP:
			processStop();
			break;
		case RESTART:
			processRestart();
			break;

		}
	}

	private void processStart() {
		new StartProcessor(task).process();
	}

	private void processStop() {
		new StopProcessor(task).process();
	}

	private void processRestart() {
		new RestartProcessor(task).process();
	}

}

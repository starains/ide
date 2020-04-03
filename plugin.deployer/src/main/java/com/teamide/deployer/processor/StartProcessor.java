package com.teamide.deployer.processor;

public class StartProcessor extends Processor {

	public StartProcessor(TaskBean task) {
		super(task);
	}

	public void process() {

	}

	public void download() {
		if (task.getPaths() == null || task.getPaths().size() == 0) {
			return;
		}

	}
}

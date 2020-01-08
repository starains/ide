package com.teamide.deploer.processor;

public abstract class Processor {

	protected final TaskBean task;

	public Processor(TaskBean task) {
		this.task = task;
	}

	public abstract void process();

}

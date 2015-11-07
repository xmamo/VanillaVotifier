package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.OutputWriter;

public class ConsoleOutputWriter implements OutputWriter {

	@Override
	public void print(Object object) {
		System.out.print(object);
	}

	@Override
	public void println(Object object) {
		System.out.println(object);
	}
}

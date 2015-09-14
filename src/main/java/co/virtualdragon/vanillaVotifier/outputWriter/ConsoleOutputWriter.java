package co.virtualdragon.vanillaVotifier.outputWriter;

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

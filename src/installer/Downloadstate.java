package installer;

import java.io.File;

public class Downloadstate implements Runnable {
	private Download dow;
	private File Speicherort;
	private Install i;

	public Downloadstate(Download dow, File Speicherort, Install i) {
		this.dow = dow;
		this.Speicherort = Speicherort;
		this.i = i;
	}

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				int ist = (int) Speicherort.length();
				if (ist > 1) {
					int soll = dow.getGroesse();
					if (soll > 1) {
						double proz = Math.round(
								((double) ist / (double) soll) * 1000.) / 10.;
						i.status(proz * 0.9);
					}
				}

				Thread.sleep(50);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
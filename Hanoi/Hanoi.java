// Hanoi.java
class Hanoi {
	public static void main(String[] args) {
		// 棒、円盤の変数を初期化
		char a = 'a';
		char b = 'b';
		char c = 'c';
		int n = 4;
		System.out.println("円盤の枚数 4 枚");
		// hanoi( ) メソッドの呼び出し
		hanoi(n, a, b, c);
	}

	// hanoi( ) メソッドの定義
	static void hanoi(int n, char a, char b, char c) {
		if (n > 0) {
			hanoi(n-1, a, c, b);
			System.out.println(n + " 番目の板を " + a + " から " + b + " に移動 ");
			hanoi(n-1, c, b, a);
		}
	}
}
/*
public class Hanoi {
	private static final int DEFAULT_PILLAR_COUNT = 3;
	private static final int DEFAULT_PLATE_COUNT = 5;

	public static void main(String[] args) {
		int pillarCount;
		int plateCount;
		
		switch (args.length) {
			case 0:
				pillarCount = DEFAULT_PILLAR_COUNT;
				plateCount = DEFAULT_PLATE_COUNT;
				break;

			case 2:
				pillarCount = Integer.parseInt(args[0]);
				plateCount = Integer.parseInt(args[1]);
				break;

			default:
				System.out.println("illegal argument!");
				return;
		}

		Hanoi hanoi = new Hanoi(pillarCount, plateCount);
		hanoi.start();
	}

	private int pillarCount;
	private int plateCount;
	private int[][] hanoiWorld;

	public Hanoi(int pillarCount, int plateCount) {
		this.pillarCount = pillarCount;
		this.plateCount = plateCount;
	}

	public void start() {
		initialWorld();
		showWorld();
	}

	private void initialWorld() {
		hanoiWorld = new int[pillarCount][plateCount];

		for (int plate = 0; plate < plateCount; plate++) {
			hanoiWorld[2][plate] = plate + 1;
		}
	}

	private void showWorld() {
		for (int plate = 0; plate < plateCount; plate++) {
			System.out.print("a");
			for (int pillar = 0; pillar < pillarCount; pillar++) {
				System.out.print(hanoiWorld[pillar][plate]==0? " ": hanoiWorld[pillar][plate]);
			}
			System.out.println("b");
		}

		for (int pillar = 0; pillar < pillarCount; pillar++) {
			System.out.print("--- ");
		}
	}
}
*/
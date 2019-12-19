package game;

public class Chess {
	private int x;
	private int y;
	private boolean isBlack;// 棋子颜色

	public Chess(int x, int y, boolean isBlack) {
		this.x = x;
		this.y = y;
		this.isBlack = isBlack;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isBlack() {
		return isBlack;
	}
}
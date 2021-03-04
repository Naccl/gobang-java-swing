package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class ChessBoard extends JComponent implements MouseListener {
	private static final int MARGIN = 30;// 边距
	private static final int GRID_SPACING = 36;// 网格间距
	private static final int ROWS = 14;// 15行
	private static final int COLS = 14;// 15列

	private Chess[] chessArray = new Chess[(ROWS + 1) * (COLS + 1)];// 总225个棋子
	private int[][] matrixChessBoard = new int[ROWS + 1][COLS + 1];// 记录棋盘使用情况
	private int chessCount = 0;// 当前已下棋子个数
	private boolean isBlackNow = true;// 当前棋子黑白
	private boolean win = false; // 当前是否分出胜负
	private int sameColorCount = 0;// 深搜记录连珠个数

	// 当前鼠标在棋盘上对应的格子，-1为不在棋盘上
	private int mouseX = -1;
	private int mouseY = -1;

	// 深搜判断胜负用到的八个搜索方向
	private static final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
	private static final int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};

	public ChessBoard() {
		// 监听鼠标的点击情况
		this.addMouseListener(this);
		// 监听鼠标在棋盘上的移动
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// 胜负已分后不再显示落点
				if (win) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					return;
				}
				// 获取当前鼠标在棋盘上对应的落点
				int tmpMouseX = getX(e);
				int tmpMouseY = getY(e);
				if (isClick(tmpMouseX, tmpMouseY)) {
					// 可以落子时设置鼠标样式
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					// 画预落棋子红框
					mouseX = tmpMouseX;
					mouseY = tmpMouseY;
				} else {
					// 不可以落子时恢复鼠标样式
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					mouseX = -1;
					mouseY = -1;
				}
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// 设置棋盘背景颜色
		g2.setColor(new Color(255, 213, 119));
		g2.fillRect(0, 0, getWidth(), getHeight());

		// 画棋盘
		g2.setColor(Color.BLACK);
		for (int i = 0; i <= ROWS; i++) //画横线
			g2.drawLine(MARGIN, MARGIN + i * GRID_SPACING, MARGIN + COLS * GRID_SPACING, MARGIN + i * GRID_SPACING);
		for (int i = 0; i <= COLS; i++) //画竖线
			g2.drawLine(MARGIN + i * GRID_SPACING, MARGIN, MARGIN + i * GRID_SPACING, MARGIN + ROWS * GRID_SPACING);

		// 画五个点
		final int dotRadius = GRID_SPACING / 9;
		final int dotCenterX = toChess(COLS / 2), dotCenterY = toChess(ROWS / 2);
		final int dotTopLeftX = toChess(3), dotTopLeftY = toChess(3);
		final int dotTopRightX = toChess(COLS - 3), dotTopRightY = toChess(3);
		final int dotLowerLeftX = toChess(3), dotLowerLeftY = toChess(ROWS - 3);
		final int dotLowerRightX = toChess(COLS - 3), dotLowerRightY = toChess(ROWS - 3);

		g2.setColor(Color.BLACK);
		Ellipse2D dot = new Ellipse2D.Double();
		setDot(dot, dotCenterX, dotCenterY, dotRadius);
		g2.fill(dot);
		setDot(dot, dotTopLeftX, dotTopLeftY, dotRadius);
		g2.fill(dot);
		setDot(dot, dotTopRightX, dotTopRightY, dotRadius);
		g2.fill(dot);
		setDot(dot, dotLowerLeftX, dotLowerLeftY, dotRadius);
		g2.fill(dot);
		setDot(dot, dotLowerRightX, dotLowerRightY, dotRadius);
		g2.fill(dot);

		// 画棋子
		for (int i = 0; i < chessCount; i++) {
			int centerX = toChess(chessArray[i].getX());
			int centerY = toChess(chessArray[i].getY());
			boolean chessColor = chessArray[i].isBlack();
			Ellipse2D circle = new Ellipse2D.Double();
			circle.setFrameFromCenter(centerX, centerY, centerX + GRID_SPACING / 2, centerY + GRID_SPACING / 2);
			if (chessColor) g2.setColor(Color.BLACK);
			else g2.setColor(Color.WHITE);
			g2.fill(circle);

			//最后一个棋子用红框标出
			if (i == chessCount - 1) {
				g2.setColor(Color.red);
				g2.drawRect(centerX - 18, centerY - 18, 36, 36);
			}
		}

		//画预落棋子红框
		final int length = GRID_SPACING / 4;
		final int frameRadius = GRID_SPACING / 2;

		if (mouseX != -1 && mouseY != -1) {// 默认-1不显示
			int absMouseX = toChess(mouseX), absMouseY = toChess(mouseY);
			int frameTopLeftX = absMouseX - frameRadius, frameTopLeftY = absMouseY - frameRadius;
			int frameTopRightX = absMouseX + frameRadius, frameTopRightY = absMouseY - frameRadius;
			int frameLowerLeftX = absMouseX - frameRadius, frameLowerLeftY = absMouseY + frameRadius;
			int frameLowerRightX = absMouseX + frameRadius, frameLowerRightY = absMouseY + frameRadius;

			g2.setColor(Color.red);
			//左上角
			g2.drawLine(frameTopLeftX, frameTopLeftY, frameTopLeftX, frameTopLeftY + length);
			g2.drawLine(frameTopLeftX, frameTopLeftY, frameTopLeftX + length, frameTopLeftY);
			//右上角
			g2.drawLine(frameTopRightX, frameTopRightY, frameTopRightX, frameTopRightY + length);
			g2.drawLine(frameTopRightX, frameTopRightY, frameTopRightX - length, frameTopRightY);
			//左下角
			g2.drawLine(frameLowerLeftX, frameLowerLeftY, frameLowerLeftX, frameLowerLeftY - length);
			g2.drawLine(frameLowerLeftX, frameLowerLeftY, frameLowerLeftX + length, frameLowerLeftY);
			//右下角
			g2.drawLine(frameLowerRightX, frameLowerRightY, frameLowerRightX, frameLowerRightY - length);
			g2.drawLine(frameLowerRightX, frameLowerRightY, frameLowerRightX - length, frameLowerRightY);
		}
	}

	// 落子
	@Override
	public void mousePressed(MouseEvent e) {
		// 胜负已分后不可再下
		if (win) {
//			JOptionPane.showMessageDialog(this, "对局已经结束，请重新开始！", "Game over !", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 获取落下的棋子在棋盘上对应的坐标
		int chessX = getX(e);
		int chessY = getY(e);
		System.out.println(chessX + "," + chessY);

		if (!isClick(chessX, chessY)) return;// 判断是否可以落子

		// 可以落子时，记录棋子
		Chess ch = new Chess(chessX, chessY, isBlackNow);
		chessArray[chessCount++] = ch;

		if (isBlackNow) matrixChessBoard[chessX][chessY] = 1;// 记录棋子，0为空，1为黑棋，2为白棋
		else matrixChessBoard[chessX][chessY] = 2;

		repaint();

		isWin(chessX, chessY, 8);
		if (win) {
			JOptionPane.showMessageDialog(this, isBlackNow ? "黑棋获胜！" : "白棋获胜！", "Game over !", JOptionPane.INFORMATION_MESSAGE);
			System.out.println(isBlackNow ? "black win!" : "white win!");
		}

		isBlackNow = !isBlackNow;// 设置下一个棋子颜色
	}

	// 深搜判断是否五连珠
	private void isWin(int x, int y, int d) {
		// 起始四个方向
		if (d == 8) {
			for (int i = 0; i < 4; i++) {
				sameColorCount = 1;
				int nx1 = x + dx[i], ny1 = y + dy[i];
				int nx2 = x + dx[i + 4], ny2 = y + dy[i + 4];
				if (nx1 >= 0 && ny1 >= 0 && nx1 <= ROWS && ny1 <= COLS) isWin(nx1, ny1, i);
				if (nx2 >= 0 && ny2 >= 0 && nx2 <= ROWS && ny2 <= COLS) isWin(nx2, ny2, i + 4);
			}
		} else {
			// 当前方向下一个位置是否连珠
			if ((isBlackNow && matrixChessBoard[x][y] == 1) || (!isBlackNow && matrixChessBoard[x][y] == 2)) {
				sameColorCount++;
				int nx = x + dx[d];
				int ny = y + dy[d];
				if (nx >= 0 && ny >= 0 && nx <= ROWS && ny <= COLS) isWin(nx, ny, d);
			} else return;
		}
		if (sameColorCount >= 5) {
			win = true;
		}
		return;
	}

	private void setDot(Ellipse2D dot, int dotX, int dotY, int dotRadius) {
		dot.setFrameFromCenter(dotX, dotY, dotX + dotRadius, dotY + dotRadius);
	}

	// 获取在棋盘上对应的坐标
	private int getX(MouseEvent e) {
		return (e.getX() - MARGIN + GRID_SPACING / 2) / GRID_SPACING;
	}

	private int getY(MouseEvent e) {
		return (e.getY() - MARGIN + GRID_SPACING / 2) / GRID_SPACING;
	}

	// 获取在面板上对应的坐标
	private int toChess(int i) {
		return i * GRID_SPACING + MARGIN;
	}

	// x,y坐标是否存在棋子
	private boolean isExistChess(int x, int y) {
//		for (int i = 0; i < chessCount; i++) {
//			if (chessArray[i].getX() == x && chessArray[i].getY() == y) return true;
//		}
		if (matrixChessBoard[x][y] != 0) return true;
		return false;
	}

	// x,y坐标是否可点击
	private boolean isClick(int x, int y) {
		if (x >= 0 && y >= 0 && x <= ROWS && y <= COLS && !isExistChess(x, y)) return true;
		return false;
	}

	// 重新开始
	public void reStart() {
		for (int i = 0; i < chessCount; i++) chessArray[i] = null;
		matrixChessBoard = new int[ROWS + 1][COLS + 1];
		chessCount = 0;
		win = false;
		isBlackNow = true;
		repaint();
	}

	// 悔棋
	public void goBack() {
		if (chessCount == 0) return;// 如果棋盘上未落子，不可以悔棋
		// 胜负已分，不可悔棋
		if (win) {
			JOptionPane.showMessageDialog(this, "对局已经结束，请重新开始！", "Game over !", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		matrixChessBoard[chessArray[chessCount - 1].getX()][chessArray[chessCount - 1].getY()] = 0;
		chessArray[chessCount - 1] = null;
		chessCount--;
		isBlackNow = !isBlackNow;
		repaint();
	}

	// 认输
	public void giveUp() {
		// 胜负已分，不可认输
		if (win) {
			JOptionPane.showMessageDialog(this, "对局已经结束，请重新开始！", "Game over !", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		win = true;
		JOptionPane.showMessageDialog(this, isBlackNow ? "白棋获胜！" : "黑棋获胜！", "Game over !", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
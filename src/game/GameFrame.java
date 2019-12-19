package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame implements ActionListener {
	private static final int DEFAULT_WIDTH = 580;
	private static final int DEFAULT_HEIGHT = 640;

	private ChessBoard chessBoard = new ChessBoard();

	private JPanel panel = new JPanel();
	private JButton reStartGameButton = new JButton("重新开始");
	private JButton giveUpButton = new JButton("认输");
	private JButton goBackButton = new JButton("悔棋");

	private void addButton() {
		panel.add(reStartGameButton);
		panel.add(giveUpButton);
		panel.add(goBackButton);
		this.getContentPane().add(panel, BorderLayout.SOUTH);

		reStartGameButton.addActionListener(this);
		giveUpButton.addActionListener(this);
		goBackButton.addActionListener(this);
	}

	public GameFrame() {
		this.add(chessBoard);
		addButton();

		this.setTitle("对局");
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reStartGameButton) {
			chessBoard.reStart();
		} else if (e.getSource() == giveUpButton) {
			chessBoard.giveUp();
		} else if (e.getSource() == goBackButton) {
			chessBoard.goBack();
		}
	}
}
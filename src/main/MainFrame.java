package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame implements ActionListener {
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 400;

	private JMenuBar menuBar = new JMenuBar();

	// 用户菜单
	private JMenu userMenu = new JMenu("用户");
	private JMenuItem changePasswordMenuItem = new JMenuItem("修改密码");
	private JMenuItem exitMenuItem = new JMenuItem("退出游戏");

	// 游戏菜单
	private JMenu gameMenu = new JMenu("游戏");
	private JMenuItem createRoomItem = new JMenuItem("创建房间");
	private JMenuItem enterRoomItem = new JMenuItem("进入房间");

	private JPanel panel = new JPanel();
	private JButton createRoomButton = new JButton("创建房间");
	private JButton enterRoomButton = new JButton("进入房间");

	// 欢迎界面
	private JLabel welcomeLabel = new JLabel("五子棋");


	private void addMenu() {
		userMenu.add(changePasswordMenuItem);
		userMenu.add(exitMenuItem);
		menuBar.add(userMenu);

		gameMenu.add(createRoomItem);
		gameMenu.add(enterRoomItem);
		menuBar.add(gameMenu);

		this.setJMenuBar(menuBar);

		exitMenuItem.addActionListener(this);
		createRoomItem.addActionListener(this);
	}

	private void addButton() {
		panel.add(createRoomButton);
		panel.add(enterRoomButton);

		this.getContentPane().add(panel, BorderLayout.SOUTH);

		// 注册监听 Button
		createRoomButton.addActionListener(this);
	}

	private void addLabel() {
		welcomeLabel.setFont(new java.awt.Font("welcome", 1, 48));
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.getContentPane().add(welcomeLabel);
	}

	public MainFrame() throws HeadlessException {
		addMenu();
		addButton();
		addLabel();

		this.setTitle("五子棋");
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private game.GameFrame game;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitMenuItem) {//退出
			if (JOptionPane.showConfirmDialog(this, "确认要退出游戏？", "退出", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		} else if (e.getSource() == createRoomButton || e.getSource() == createRoomItem) {// 创建游戏
			if (game == null) game = new game.GameFrame();
			game.setVisible(true);
		}
	}
}
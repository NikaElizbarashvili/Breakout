import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BreakoutPanel extends JPanel implements KeyListener, ActionListener {

	private int[][][] bricks = new int[Main.brickYCount][Main.brickXCount][5]; // third dimension, 1 if the brick exists
																				// + coordinates of each brick
	private Graphics2D g2D;
	private int ballX, ballY, ballXDirection, ballYDirection;
	private int paddleX1, paddleX2, paddleY1;
	private int tries;
	private Timer timer;
	private boolean gameStarted;
	private int brickCount;

	public BreakoutPanel() {
		timer = new Timer(Main.delay, this);
		timer.start();
		this.setPreferredSize(new Dimension(Main.panelXSize, Main.panelYSize));
		addKeyListener(this);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);

		setupPanel();
	}

	private void setupPanel() {
		setupBricks();
		setupBall();
		setupPaddle();

		tries = Main.tries;
		gameStarted = false;
	}

	private void setupBricks() {
		for (int i = 0; i < Main.brickYCount; i++)
			for (int j = 0; j < Main.brickXCount; j++) {
				bricks[i][j][0] = 1;
				bricks[i][j][1] = j * Main.brickXSize + (j + 1) * Main.brickDistance;
				bricks[i][j][2] = Main.panelYTop + i * Main.brickYSize + (i + 1) * Main.brickDistance;
				bricks[i][j][3] = (j + 1) * Main.brickXSize + (j + 1) * Main.brickDistance;
				bricks[i][j][4] = Main.panelYTop + (i + 1) * Main.brickYSize + (i + 1) * Main.brickDistance;
			}
		brickCount = Main.brickXCount * Main.brickYCount;
	}

	private void setupBall() {
		ballX = Main.panelXSize / 2;
		ballY = Main.panelYSize / 2;
		int randomDir;
		randomDir = ThreadLocalRandom.current().nextInt(0, 2);
		ballXDirection = ThreadLocalRandom.current().nextInt(Main.ballMinSpeed, Main.ballMaxSpeed + 1);
		if (randomDir == 1)
			ballXDirection = -ballXDirection;
		randomDir = ThreadLocalRandom.current().nextInt(0, 2);
		ballYDirection = ThreadLocalRandom.current().nextInt(Main.ballMinSpeed, Main.ballMaxSpeed + 1);
		if (randomDir == 1)
			ballYDirection = -ballYDirection;
	}

	private void setupPaddle() {
		paddleX1 = Main.panelXSize / 2 - Main.paddleXSize / 2;
		paddleY1 = Main.panelYSize - Main.paddleYSize - Main.paddleDistance;
		paddleX2 = Main.panelXSize / 2 + Main.paddleXSize / 2;
	}

	private void moveBall() {
		paintBall(Main.backgroundColor);
		if (((ballX + Main.ballR + ballXDirection) > (Main.panelXSize - Main.brickDistance))
				|| ((ballX - Main.ballR + ballXDirection) < (0 + Main.brickDistance)))
			ballXDirection *= -1;
		else
			ballX += ballXDirection;
		if (((ballY - Main.ballR + ballYDirection) < (Main.panelYTop + Main.brickDistance))
				|| ((ballX >= paddleX1) && (ballX <= paddleX2) && (ballY + Main.ballR + ballYDirection >= paddleY1)
						&& (ballY + Main.ballR + ballYDirection - paddleY1 <= ballYDirection)))
			ballYDirection *= -1;
		else
			ballY += ballYDirection;
		paintBall(Main.ballColor);
		checkHit();
		checkStatus();
	}

	public void paint(Graphics g) {
		g2D = (Graphics2D) g;
		g2D.setColor(Main.backgroundColor);
		g2D.fillRect(0, 0, Main.panelXSize, Main.panelYSize);
		g2D.setColor(Main.systemColor);
		g2D.drawLine(0, Main.panelYTop, Main.panelXSize, Main.panelYTop);
		g2D.setFont(Main.font);
		g2D.drawString(Integer.toString(tries), Main.panelXSize - 10, Main.textYBottom);
		g2D.drawString("Restart - Space", 0, Main.textYBottom);

		paintBricks();
		paintBall(Main.ballColor);
		paintpaddle();
	}

	private void paintBricks() {
		Color fill;
		for (int i = 0; i < Main.brickYCount; i++) {
			if ((i % 2) == 0)
				fill = Main.brickColor1;
			else
				fill = Main.brickColor2;
			for (int j = 0; j < Main.brickXCount; j++)
				if (bricks[i][j][0] == 1)
					paintBrick(fill, bricks[i][j][1], bricks[i][j][2], Main.brickXSize, Main.brickYSize);
		}
	}

	private void paintBrick(Color fill, int x, int y, int w, int h) {
		g2D.setColor(fill);
		g2D.fillRect(x, y, w, h);

	}

	private void paintBall(Color c) {
		g2D.setColor(c);
		g2D.fillOval(ballX - Main.ballR, ballY - Main.ballR, Main.ballR * 2, Main.ballR * 2);
	}

	private void paintpaddle() {
		g2D.setColor(Main.paddleColor);
		g2D.fillRect(paddleX1, paddleY1, Main.paddleXSize, Main.paddleYSize);
		;
	}

	private void movePaddle(int moveIncrement) {
		paddleX1 += moveIncrement;
		paddleX2 += moveIncrement;
		if (paddleX2 > Main.panelXSize - Main.brickDistance) {
			paddleX1 -= (paddleX2 - (Main.panelXSize - Main.brickDistance));
			paddleX2 = Main.panelXSize - Main.brickDistance;
		}
		if (paddleX1 < Main.brickDistance) {
			paddleX2 += (Main.brickDistance - paddleX1);
			paddleX1 = Main.brickDistance;
		}
	}

	private void checkHit() {
		for (int i = 0; i < Main.brickYCount; i++)
			for (int j = 0; j < Main.brickXCount; j++)
				if (bricks[i][j][0] == 1) {
					// hit from the bottom
					if ((ballYDirection < 0) && (ballX >= bricks[i][j][1]) && (ballX <= bricks[i][j][3])
							&& (ballY - Main.ballR >= bricks[i][j][2]) && (ballY - Main.ballR <= bricks[i][j][4])
							&& (ballY - Main.ballR - bricks[i][j][4] <= ballYDirection)) {
						ballYDirection *= -1;
						bricks[i][j][0] = 0;
						brickCount--;
					}
					// hit from the top
					if ((ballYDirection > 0) && (ballX >= bricks[i][j][1]) && (ballX <= bricks[i][j][3])
							&& (ballY + Main.ballR <= bricks[i][j][4]) && (ballY + Main.ballR >= bricks[i][j][2])
							&& (ballY + Main.ballR - bricks[i][j][2] <= ballYDirection)) {
						ballYDirection *= -1;
						bricks[i][j][0] = 0;
						brickCount--;
					}
					// hit from the left
					if ((ballXDirection > 0) && (ballY >= bricks[i][j][2]) && (ballY <= bricks[i][j][4])
							&& (ballX + Main.ballR <= bricks[i][j][3]) && (ballX + Main.ballR >= bricks[i][j][1])
							&& (ballX + Main.ballR - bricks[i][j][1] <= ballXDirection)) {
						ballXDirection *= -1;
						bricks[i][j][0] = 0;
						brickCount--;
					}
					// hit from the right
					if ((ballXDirection < 0) && (ballY >= bricks[i][j][2]) && (ballY <= bricks[i][j][4])
							&& (ballX - Main.ballR >= bricks[i][j][1]) && (ballX - Main.ballR <= bricks[i][j][3])
							&& (ballX - Main.ballR - bricks[i][j][3] <= ballXDirection)) {
						ballXDirection *= -1;
						bricks[i][j][0] = 0;
						brickCount--;
					}

				}
		if (ballY >= Main.panelYSize + Main.ballR) {
			tries--;
			gameStarted = false;
			setupBall();
			setupPaddle();
		}
	}

	private void checkStatus() {
		int result;
		if (brickCount == 0) {
			gameStarted=false;
			result = JOptionPane.showConfirmDialog(this, "YOU WON!! Restart game?", "", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION)
				setupPanel();
		}
		if (tries == 0) {
			gameStarted=false;
			result = JOptionPane.showConfirmDialog(this, "YOU LOST!! Restart game?", "", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION)
				setupPanel();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameStarted)
			moveBall();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (!gameStarted)
				gameStarted = true;
			if (!((brickCount > 0) && (tries > 0)))
				setupPanel();
			else {
				gameStarted = false;
				int result = JOptionPane.showConfirmDialog(this, "Restart game?", "", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION)
					setupPanel();
				else
					gameStarted = true;
			}
		}
		if ((brickCount > 0) && (tries > 0)) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				movePaddle(Main.paddleSpeed);
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				movePaddle(-Main.paddleSpeed);
			gameStarted = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}

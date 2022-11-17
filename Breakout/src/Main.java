import java.awt.Color;
import java.awt.Font;

public class Main {

	public static final int brickXCount = 5;
	public static final int brickYCount = 5;
	public static final int brickXSize = 30;
	public static final int brickYSize = brickXSize / 2;
	public static final int brickDistance = 2;
	public static final Color brickColor1 = Color.red;
	public static final Color brickColor2 = Color.blue;

	public static final int paddleXSize = (int) (brickXSize * 1.5);
	public static final int paddleYSize = paddleXSize / 4;
	public static final int paddleDistance = brickDistance;
	public static final Color paddleColor = Color.black;
	public static final int paddleSpeed = paddleXSize / 3;

	public static final int ballMinSpeed = 2;
	public static final int ballMaxSpeed = 2;

	public static final int ballR = brickXSize / 3;
	public static final Color ballColor = Color.black;
	public static final int delay = 8;

	public static final int tries = 3;
	public static final Color backgroundColor = Color.white;
	public static final Color systemColor = Color.blue;
	public static final Font font = new Font("Calibri", Font.BOLD, 15);
	public static final int textYBottom = brickYSize;

	public static final int panelXSize = brickXCount * brickXSize + (brickXCount + 1) * brickDistance;
	public static final int panelYSize = Math.max(brickYCount * (brickYSize + brickDistance) *3, panelXSize / 3 * 4);
	public static final int panelYTop = textYBottom + brickDistance * 2;

	public static BreakoutFrame frame;

	public static void main(String[] args) {
		frame = new BreakoutFrame();
	}
}

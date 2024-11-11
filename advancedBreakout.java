/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import sun.java2d.loops.DrawPolygons;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class advancedBreakout extends GraphicsProgram {

/** Width and height of application window in pixels */
//	public static final int APPLICATION_WIDTH = ;
//	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final double BALL_RADIUS = 5;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Number of marks while super shot is active */
	private static final int MARKS_COUNT = 100;

/** This number controls sensitivity of ball's rebound from paddle, make it 1 to turn off trajectory control */
	private static final double PADDLE_TRAJECTORY = 0.14;

/** Delay time, decrease this number to make program faster */ 
	private static final long SLEEP_TIME = 8;
	
/** Laser's width */
	private static final double LASER_WIDTH = 8;
	
/** Radius of popping in bomb mode (after popping white block bomb mode activates */
	private static final double ANNIHILATION_RADIUS = 77;
	
///////////////////// changeable global variables ////////////////////// 
	
/** Mouses X coordinate to make paddle follow */
	private double MOUSE_X;
	
/** Count of how many blocks did player pop */
	private int POPPED_COUNT = 0;
	
/** Tells if player have super shot */
	private boolean SUPER_SHOT = false;
	
/** If player pops all the blocks he goes to next level where ball moves faster */
	private int LEVEL = 1;
	
/** If player pops ball adder block, these variables tell when and where should it be added */
	private boolean ADD_BALLS = false;
	private double NEW_BALL_X;
	private double NEW_BALL_Y;
	
/** These variables tell when laser should be shoot */
	private boolean MOUSE_IS_DOWN = false;
	private boolean LASER_IS_AVALIABLE = true;
	
/** This variable tells when bomb mode should activate */
	private boolean BOMB_MODE = false;
	
	/*	Program has oval shaped paddle, which makes player able to have more control over the direction of ball.
	 *	After popping at least 10 blocks, next shot will be super shot, which pierces through every block.
	 *  On mouse click, player can shoot laser from the middle of the paddle, which pops every block in its radius
	 *  except black one. Laser shot will reset if player manages to touch both walls with paddle while ball is in air.
	 *  Popping up black block will add 3 additional balls into the game and player will have 3 more lives,
	 *  but main ball still is main ball and only that one will spawn from middle after falling out.
	 *  Also popping white block will change parameters of popping, it will activate bomb mode. 
	 *  In bomb mode ball will pop every block in ANNIHILATION_RADIUS range after each collision.
	 *  Only main ball goes into bomb mode.
	*/
	public void run() {
		/* For some reason setSize does not set size same as passed values so I needed to add 18 and 72 */
		setSize(WIDTH + 18, HEIGHT + 72);
		
		// Adding mouse listeners to tell when mouse got clicked
		addMouseListeners(); 
		
		setPlayButton();
		
		GRect [][] bricks = setBricks();
		GOval paddle = setPaddle();
		GOval ball = setBall(WIDTH/2 - BALL_RADIUS/2, HEIGHT/2 - BALL_RADIUS/2);
		
		setFrame(); 
		
		startGame(bricks, paddle, ball);
	}

	
	// Function is infinity loop which updates positions of game objects
	private void startGame(GRect [][] bricks, GOval paddle, GOval ball) {
		
		int life = NTURNS;
		
		double ballMovementDirections [] = {(Math.random()-0.5)*4, 3}; // movement on X and Y
		
		// Additional balls are spawned after breaking black brick
		double ballMovementDirections1 [] = {(Math.random()-0.5)*4, -3}; 
		double ballMovementDirections2 [] = {(Math.random()-0.5)*4, -3}; 
		double ballMovementDirections3 [] = {(Math.random()-0.5)*4, -3}; 
		
		GOval ball1 = null;
		GOval ball2 = null;
		GOval ball3 = null;
		
		// Ball leaves marks while super shot is active
		GLine marks [] = new GLine[MARKS_COUNT];
		
		// Count of loops in while true loop, i use it in order to add marks of ball's movement
		int count = 0;
		
		// X coordinates of laser shot beginning and end
		int lineStartX = 0;
		int lineEndX = (int)LASER_WIDTH*2-1;
		
		// Laser on display is many black and red lines next to each other, which disappears with little delay 
		// and laser annihilates every block other than black one in its range
		GLine [] laser = null;
		int laserDelay = 6;
		
		while(true) {
			
			// If player beat the game. (I think its impossible, but lets have this option just for show)
			if(LEVEL == SLEEP_TIME) {
				victoryEmote();
				break;
			}
			
			// Makes super shot after popping 10 blocks.  
			// Super shot makes every block ball touches pop without changing ball's direction 
			if(SUPER_SHOT) {
				
				leaveMark(marks, ballMovementDirections, count, ball);
				
				ball.setFillColor(Color.ORANGE);
				
			}
			
			// Makes laser charge on mouse click
			if(MOUSE_IS_DOWN && LASER_IS_AVALIABLE) {				
				
				LASER_IS_AVALIABLE = false;
				
				laser = shootLaser(bricks, paddle.getX() + PADDLE_WIDTH/2, paddle);
				
			}
			
			// Removes laser over time
			if(laser != null) {

				laserDelay--;
				
				if(lineStartX < lineEndX && laserDelay == 0) {
		
					remove(laser[lineStartX]);
					remove(laser[lineEndX]);
					
					lineStartX++;
					lineEndX--;
					laserDelay = 6;
					
				}
				
			}
			
			// Changing color in bomb mode
			if(BOMB_MODE) {
				ball.setFillColor(Color.RED);
			}
			
			// Adds additional 3 balls after black brick is broken
			if(ADD_BALLS) {
				
				ADD_BALLS = false;
				
				ball1 = setBall(NEW_BALL_X - BALL_RADIUS/2,NEW_BALL_Y - BALL_RADIUS/2);
				ball2 = setBall(NEW_BALL_X - BALL_RADIUS/2,NEW_BALL_Y - BALL_RADIUS/2);
				ball3 = setBall(NEW_BALL_X - BALL_RADIUS/2,NEW_BALL_Y - BALL_RADIUS/2);
				
				ball1.setFillColor(Color.YELLOW);
				ball2.setFillColor(Color.YELLOW);
				ball3.setFillColor(Color.YELLOW);
				
				life += 3;
				
			}
			
			// changing ball's position
			ball.setLocation(ball.getX() + ballMovementDirections[0], ball.getY() + ballMovementDirections[1]);
			
			if(ball1 != null) {
				ball1.setLocation(ball1.getX() + ballMovementDirections1[0], ball1.getY() + ballMovementDirections1[1]);
			}
			
			if(ball2 != null) {
				ball2.setLocation(ball2.getX() + ballMovementDirections2[0], ball2.getY() + ballMovementDirections2[1]);
			}
			
			if(ball3 != null) {
				ball3.setLocation(ball3.getX() + ballMovementDirections3[0], ball3.getY() + ballMovementDirections3[1]);
			}
			
			// Moves paddle close to mouse's x coordinate slowly
			double paddleMovementDirection = moveDirection(paddle.getX()); 
			if((paddle.getX() + PADDLE_WIDTH < WIDTH && paddleMovementDirection == 1) 
					|| (paddle.getX() > 0 && paddleMovementDirection == -1)) {
				
				paddle.setLocation(paddle.getX() + paddleMovementDirection*3, paddle.getY());
				
			}
			
			// Makes program slow to make it playable. Speed increases on each level.
			pause(SLEEP_TIME - LEVEL);
			
			// Updating balls movement directions
			ballMovementDirections = directionChanges(ballMovementDirections, paddle, ball, bricks, marks, false);
			
			if(ball1 != null) {
				ballMovementDirections1 = directionChanges(ballMovementDirections1, paddle, ball1, bricks, marks, true);
			}
			
			if(ball2 != null) {
				ballMovementDirections2 = directionChanges(ballMovementDirections2, paddle, ball2, bricks, marks, true);
			}
			
			if(ball3 != null) {
				ballMovementDirections3 = directionChanges(ballMovementDirections3, paddle, ball3, bricks, marks, true);
			}
			
			// If there is no bricks left player gets to next level and everything resets
			if(ballMovementDirections[1] == 0) {
				
				// Reseting everything
				LEVEL++;
				
				LASER_IS_AVALIABLE = true;
				laser = null;
				
				BOMB_MODE = false;
				
				lineStartX = 0;
				lineEndX = (int)LASER_WIDTH*2 - 1;
				
				removeAll();
				
				ball1 = null;
				ball2 = null;
				ball3 = null;
				
				setPlayButton();
				
				bricks = setBricks();
				paddle = setPaddle();
				ball = setBall(WIDTH/2, HEIGHT/2);
				
				setFrame();
				
				// making little delay until next game starts
				makeDelay();
				
				// Starting game again with different speed and ending current one
				startGame(bricks, paddle, ball);
				break;
			
			}
			
			// If ball is unreachable player loses one of the lives
			life = looseBall(ball, paddle, ballMovementDirections, life, ball1, ball2, ball3, false);
			
			if(ball1 != null) {
				
				int tempLife = life;
				life = looseBall(ball1, paddle, ballMovementDirections1, life, ball1, ball2, ball3, true);
				
				// If added ball got out it should not spawn again so i set it to null
				if(tempLife != life) {
					ball1 = null;
				}
				
			}
			
			if(ball2 != null) {
				
				int tempLife = life;
				life = looseBall(ball2, paddle, ballMovementDirections2, life, ball1, ball2, ball3, true);
				
				if(tempLife != life) {
					ball2 = null;
				}
				
			}
			
			if(ball3 != null) {
				
				int tempLife = life;
				life = looseBall(ball3, paddle, ballMovementDirections3, life, ball1, ball2, ball3, true);
				
				if(tempLife != life) {
					ball3 = null;
				}
				
			}
			
			// After player loses showing end screen
			if(life == 0) {
				loserEmote();
				break;
			}
			
			count++;
		
		}
	}
	
	// Mouse tracker to make paddle follow
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		MOUSE_X = e.getX();
	}	
	
	
	// If mouse is clicked player should shot laser if he has it
	public void mouseClicked(MouseEvent e) {
		
		super.mouseClicked(e);
		MOUSE_IS_DOWN = true;
		
	}
	
	private GLine [] shootLaser(GRect [][] bricks, double x, GOval paddle) {
		
		// To check if block or white is black I use these
		GRect temp = new GRect(0,0,0,0);
		temp.setFillColor(Color.BLACK);
		
		GRect temp1 = new GRect(0,0,0,0);
		temp1.setFillColor(Color.WHITE);
		
		// Removing every block other than black one in laser's range
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				
				if(bricks[i][j].getX() <= x + LASER_WIDTH 
						&& bricks[i][j].getX() + BRICK_WIDTH >= x - LASER_WIDTH 
						&& bricks[i][j].getFillColor() != temp.getFillColor()
						&& bricks[i][j].getFillColor() != temp1.getFillColor()) {
					
					bricks[i][j].setFilled(false);
					
					remove(bricks[i][j]);
				}
				
			}
		}
		
		// Adding laser on screen which is many black and red lines next to each other
		GLine laser [] = new GLine[(int)LASER_WIDTH*2];
		for(int i = 0; i < LASER_WIDTH*2; i++) {
			
			laser[i] = new GLine(paddle.getX() + PADDLE_WIDTH/2 - LASER_WIDTH + i,
					paddle.getY(),
					paddle.getX() + PADDLE_WIDTH/2 - LASER_WIDTH + i,
					0);
			
			if(i%2 == 0) {
			
				laser[i].setColor(Color.RED);
			
			} else {
			
				laser[i].setColor(Color.BLACK);
			
			}
			
			add(laser[i]);
			
		}
		
		return laser;
	
	}

	// function sets up play button and waits until player clicks mouse to start the game
	private void setPlayButton() {
		
		int R = 40;
		int x = WIDTH/2 - R;
		int y = HEIGHT/2 - R;
		
		setLevelLabel();
		
		GOval playButton = new GOval(x,y,R*2,R*2);
		playButton.setFilled(true);
		playButton.setColor(Color.GREEN);
		add(playButton);
		
		decoratePlayButton();
		
		waitForClick();
		removeAll();
		MOUSE_IS_DOWN = false;
	
	}
	
	// I tried to add triangle to make button look like play button,
	// but its just too many white lines, because I do not know how to make triangle
	private void decoratePlayButton() {
		
		// Coordinates of top left bottom of triangle
		int x = WIDTH/2 - 18; 
		int y = HEIGHT/2 - 30;
		
		// Coordinates of bottom left corner of triangle
		int x1 = x + 50;
		int y1 = y + 30;
		
		// Makes line from middle right corner of triangle to everywhere on the side between other two corners
		while(y != y1+30) {
			
			GLine line = new GLine(x,y,x1,y1);
			line.setColor(Color.WHITE);
			add(line);
			
			y++;
		
		}
	}
	
	// Sets starting text and which level player is entering to
	private void setLevelLabel() {
		
		GLabel level = new GLabel("LEVEL----> " + LEVEL, WIDTH/2 - 30, HEIGHT/2 - 45);
		level.setColor(Color.BLUE);
		
		GLabel text = new GLabel("Lets see how far will you get. My personal best is level 4. Good luck!", WIDTH/2 - 180, HEIGHT/2 - 120);
		text.setColor(Color.BLACK);
		
		GLabel reminderSuperShot = new GLabel("Remember, after every 10 blocks popped you will get super shot!", WIDTH/2 - 180, HEIGHT/2 - 100);
		reminderSuperShot.setColor(Color.MAGENTA);
		
		GLabel reminderLaser = new GLabel("Use paddle's builtin laser with mouseclick. You get 1 shot each level.", WIDTH/2 - 180, HEIGHT/2 - 80);
		reminderLaser.setColor(Color.MAGENTA);
		
		GLabel reminderBallAdder = new GLabel("Also popping black block will spawn 3 additional balls.", WIDTH/2 - 160, HEIGHT/2 - 60);
		reminderBallAdder.setColor(Color.MAGENTA);
		
		if(LEVEL == 1) {
			add(text);
			add(reminderSuperShot);
			add(reminderLaser);
			add(reminderBallAdder);
		}
		
		add(level);
	}
	
	private GRect [][] setBricks() {
		GRect [][] bricks = new GRect[NBRICK_ROWS][NBRICKS_PER_ROW];
		
		int ballAdderBlock = NBRICK_ROWS*NBRICKS_PER_ROW-1;
		ballAdderBlock *= Math.random();
		
		int bombModeBlock = NBRICK_ROWS*NBRICKS_PER_ROW-1;
		bombModeBlock *= Math.random();
		
		// Colors for each row of bricks
		float [][] colors = {
				{0.0f,0.99f,0.99f},
				{0.0f,0.99f,0.99f},
				{0.1f,0.99f,0.99f},
				{0.1f,0.99f,0.99f},
				{0.15f,0.99f,0.99f},
				{0.15f,0.99f,0.99f},
				{0.3f,0.99f,0.99f},
				{0.3f,0.99f,0.99f},
				{0.5f,0.99f,0.99f},
				{0.5f,0.99f,0.99f}
		};
		
		// Creates bricks one by one on correct location and adds them into bricks[][] array
		for(int y = 70,row = 0,i = 0; y < BRICK_Y_OFFSET+NBRICK_ROWS*BRICK_HEIGHT + (NBRICK_ROWS-1)*BRICK_SEP; y += BRICK_HEIGHT + BRICK_SEP, row++, i++) {
			for(int x = BRICK_SEP/2,j = 0; x < WIDTH; x += BRICK_WIDTH + BRICK_SEP, j++) {
				
				bricks[i][j] = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				bricks[i][j].setFilled(true);
				
				if(j + i*NBRICKS_PER_ROW != ballAdderBlock) {
					bricks[i][j].setColor(Color.getHSBColor(colors[row][0], colors[row][1], colors[row][2]));
				}
				
				if(j + i*NBRICKS_PER_ROW == bombModeBlock) {
					bricks[i][j].setFillColor(Color.white);
					bricks[i][j].setColor(Color.BLACK);
				}
				
				add(bricks[i][j]);
				
			}
		}
		
		return bricks;
		
	}

	private GOval setPaddle() {
		
		GOval padle = new GOval(getWidth()/2 - PADDLE_WIDTH/2,
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH,
				PADDLE_HEIGHT);
		
		padle.setFilled(true);
		add(padle);
		
		return padle;
		
	}
	
	private GOval setBall(double x, double y) {
		
		GOval ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		
		return ball;
		
	}
	
	// Function sets up walls.
	// In reality wall is 4 GRects, black, white, grey and black again.
	private void setFrame() {
		
		GRect frame1 = new GRect(0,0,WIDTH,HEIGHT); 
		GRect frame2 = new GRect(1,1,WIDTH-2,HEIGHT-2); 
		GRect frame3 = new GRect(2,2,WIDTH-4,HEIGHT-4);
		GRect frame4 = new GRect(3,3,WIDTH-6,HEIGHT-6);
		
		frame1.setColor(Color.BLACK);
		frame2.setColor(Color.WHITE);
		frame3.setColor(Color.gray);
		frame4.setColor(Color.BLACK);
		
		add(frame1);
		add(frame2);
		add(frame3);
		add(frame4);
		
	}
	
	// function returns in which direction paddle should move
	private double moveDirection(double padleX) {
		
		// If mouse pointer is in the middle of paddle, in order for paddle to not start shaking left and right,
		// I made it such that mouse pointer should be away from paddles middle by at least 1 pixels to make paddle move
		if(MOUSE_X - (padleX + PADDLE_WIDTH/2) > 1) {
		
			return 1;
		
		} else if (MOUSE_X - (padleX + PADDLE_WIDTH/2) < -1){
		
			return -1;
		
		}
		
		return 0;
	}
	
	// function leaves lines so you know which movements ball did, it leaves up to 100 lines
	private void leaveMark(GLine [] marks, double [] ballMovementDirections, int count, GOval ball) {
		
		// Each time ball moves, count is increased by one, so index will delete mark in array according to when it was added
		int index = count%MARKS_COUNT; 
		
		if(marks[index] != null) {
			remove(marks[index]);
		}
		
		// Making mark from center location to next location of center after movement
		double r = BALL_RADIUS;
		marks[index] = new GLine(ball.getX() + r, ball.getY() + r,
				ball.getX() + ballMovementDirections[0] + r, ball.getY() + ballMovementDirections[1] + r);
		
		marks[index].setColor(Color.ORANGE);
		add(marks[index]);
		
	} 
	
	// Function changes ball's directions according to where did it hit
	private double [] directionChanges(double [] ballMovementDirections, GOval paddle, GOval ball, GRect [][] bricks, GLine [] marks, boolean addedBall) {
		
		// These parameters are used to make sound on collision, but it lags on my pc so I dont use it.
		
		// double temp0 = ballMovementDirections[0], temp1 = ballMovementDirections[1];
		
		// When ball hits right wall
		if(ball.getX() >= WIDTH - BALL_RADIUS*2 - 8) {
			// I added absolute values, because, otherwise, ball gets stuck on paddle going back and forth
			ballMovementDirections[0] = -Math.abs(ballMovementDirections[0]);
			
			// If ball is in bomb mode it pops blocks in radius
			if(BOMB_MODE) {
				
				if(addedBall == false) {
					bombModeExplosion(bricks, ball);
				}
				
			}
			
		}
		
		// When ball hits left wall
		if(ball.getX() <= 8) {
			ballMovementDirections[0] = Math.abs(ballMovementDirections[0]);
			
			// If ball is in bomb mode it pops blocks in radius
			if(BOMB_MODE) {
				
				if(addedBall == false) {
					bombModeExplosion(bricks, ball);
				}
				
			}
		}
		
		// When ball hits top wall
		if(ball.getY() <= 8) {
			ballMovementDirections[1] = Math.abs(ballMovementDirections[1]);
			
			// If ball is in bomb mode it pops blocks in radius
			if(BOMB_MODE) {
				
				if(addedBall == false) {
					bombModeExplosion(bricks, ball);
				}
				
			}
		}
		
		// When ball hits paddle
		if(ball.getX() + BALL_RADIUS*2 >= paddle.getX() 
				&& ball.getX() <= paddle.getX() + PADDLE_WIDTH 
				&& ball.getY() >= paddle.getY() - BALL_RADIUS*2) {
			
			
			
			double place = ball.getX() + BALL_RADIUS - paddle.getX(); // place on paddle where ball did hit
			
			// Direction changes of ball, depending on where on paddle it will hit 
			ballMovementDirections[0] = (place - PADDLE_WIDTH/2)*PADDLE_TRAJECTORY;
			
			ballMovementDirections[1] = -Math.abs(ballMovementDirections[1]);
			
			// Reseting changes after super shot
			if(SUPER_SHOT && addedBall == false) {
				
				SUPER_SHOT = false;
				paddle.setFillColor(Color.BLACK);
				ball.setFillColor(Color.BLACK);
				
				for(int i = 0; i < MARKS_COUNT; i++) {
					remove(marks[i]);
				}
				
				POPPED_COUNT = 0;
				
			}
			
			// After every 10 popped blocks player gets super shot which pierces everything
			if(POPPED_COUNT >= 10 && addedBall == false) {
			
				SUPER_SHOT = true;
				paddle.setFillColor(Color.RED);
		
			}
		}
		
		// If brick is left in game and if ball hit block these booleans become true
		boolean brickIsLeft = false;
		boolean directionChanged = false;
		
		// When ball hits blocks
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				
				// When block is already taken out
				if(bricks[i][j].isFilled() == false || directionChanged) {
					continue;
				}
				
				brickIsLeft = true;
				
				// If ball is hitting next block in row continue
				if(j < NBRICKS_PER_ROW - 1 
						&& bricks[i][j+1].isFilled() == true 
						&& bricks[i][j+1].getX() - (ball.getX() + BALL_RADIUS) < BRICK_SEP/2) {
					
					continue;
					
				}
				
				// If ball is hitting next block in column continue
				if(i < NBRICK_ROWS - 1 
						&& bricks[i+1][j].isFilled() == true 
						&& bricks[i+1][j].getY() - (ball.getY() + BALL_RADIUS) < BRICK_SEP/2) {
					
					continue;
					
				}
				
				double brickX = bricks[i][j].getX();
				double brickY = bricks[i][j].getY();
				double ballX = ball.getX();
				double ballY = ball.getY();
				
				// if (ball is touching current block)
				if(ballY < brickY + BRICK_HEIGHT 
						&& ballY + BALL_RADIUS*2 > brickY 
						&& ballX < brickX + BRICK_WIDTH 
						&& ballX + BALL_RADIUS*2 > brickX) {
					
					// If its black block this remembers where additional balls should be added at
					if(bricks[i][j].getFillColor() == Color.BLACK) {
						
						ADD_BALLS = true;
						NEW_BALL_Y = bricks[i][j].getY() + BRICK_HEIGHT/2;
						NEW_BALL_X = bricks[i][j].getX() + BRICK_WIDTH/2;
						
					}
					
					bricks[i][j].setFilled(false);
					remove(bricks[i][j]);
				
					// If its white block bomb mode activates
					if(bricks[i][j].getFillColor() == Color.WHITE) {
						
						BOMB_MODE = true;
						
					}
					
					// If its super shot direction do not changes
					if(SUPER_SHOT && addedBall == false) { 
						continue;
					}
					
					// If ball is in bomb mode it pops blocks in radius
					if(BOMB_MODE) {
						
						if(addedBall == false) {
							bombModeExplosion(bricks, ball);
						}
						
					}
					
					// If ball is touching block from left half
					if(ballX + BALL_RADIUS - brickX < BRICK_WIDTH/2) {
						
						// If ball is touching block from top half
						if(ballY + BALL_RADIUS - brickY < BRICK_HEIGHT/2) {
							
							// If ball is touching from left side
							// and ball is moving right because otherwise it can not be touching block from left
							// and there is no block on left side 
							if(ballY + BALL_RADIUS*2 - brickY > ballX + BALL_RADIUS*2 - brickX 
									&& ballMovementDirections[0] >= 0 
									&& bricks[i][j-1].isFilled() == false) {
								
								ballMovementDirections[0] *= -1;
								
							} else {
							
								ballMovementDirections[1] *= -1;
							
							}
							
						} else { 
							
							// Ball is touching from bottom left half
							
							// If ball is touching from left
							// and ball is moving right because otherwise it can not be touching block from left
							// and there is no block on left side
							if(brickY + BRICK_HEIGHT - ballY > ballX + BALL_RADIUS*2 - brickX 
									&& ballMovementDirections[0] >= 0 
									&& bricks[i][j-1].isFilled() == false) {
								
								ballMovementDirections[0] *= -1;
							
							} else {
								
								ballMovementDirections[1] *= -1;
								
							}
							
						}
						
					} else {
						// Ball is touching from right half
						
						// If ball is touching from top 
						if(ballY + BALL_RADIUS - brickY < BRICK_HEIGHT/2) {
							
							// If ball is touching from top side 
							// or ball is moving to right because that time it can not be touching block from right side
							if(brickX + BRICK_WIDTH - ballX > ballY + BALL_RADIUS*2 - brickY 
									|| ballMovementDirections[0] >= 0) {
								
								ballMovementDirections[1] *= -1;
							
							} else {
								
								ballMovementDirections[0] *= -1;
								
							}
						} else {
							// Ball is touching from bottom right half
							
							// If ball is touching from bottom side
							// or ball is moving to right because that time it can not be touching block from right side
							if(brickX + BRICK_WIDTH - ballX > brickY + BRICK_HEIGHT - ballY 
									|| ballMovementDirections[0] >= 0) {
								
								ballMovementDirections[1] *= -1;
							
							} else {
								
								ballMovementDirections[0] *= -1;
								
							}
							
						}
						
					}
					
					directionChanged = true;
					
				}
				
			}
			
		}
		
		// If direction changed that means ball popped block
		if(directionChanged) {
			POPPED_COUNT++;
		}
		
		// If there is not any bricks left this stops game and takes player to next level
		if(brickIsLeft == false) {
			ballMovementDirections[1] = 0;
		}
	
		// This should adds sound on bounce, but it lags on my computer and I do not use it
		/*
			if(temp0 != ballMovementDirections[0] || temp1 != ballMovementDirections[1]) {
				makeSound();
			}
		*/
		
		return ballMovementDirections;
	}
	
	private void bombModeExplosion(GRect [][] bricks, GOval ball) {
		if(BOMB_MODE) {
			
			GOval explosion = new GOval(ball.getX() + BALL_RADIUS - ANNIHILATION_RADIUS,
					ball.getY() + BALL_RADIUS - ANNIHILATION_RADIUS,
					ANNIHILATION_RADIUS*2, ANNIHILATION_RADIUS*2);
			explosion.setFilled(false);
			add(explosion);
			
			for(int k = 0; k < NBRICK_ROWS; k++) {
				for(int l = 0; l < NBRICKS_PER_ROW; l++) {
					
					
					
					GObject collider1 = getElementAt(bricks[k][l].getX(), bricks[k][l].getY());
					GObject collider2 = getElementAt(bricks[k][l].getX() + BRICK_WIDTH, bricks[k][l].getY());
					GObject collider3 = getElementAt(bricks[k][l].getX(), bricks[k][l].getY() + BRICK_HEIGHT);
					GObject collider4 = getElementAt(bricks[k][l].getX() + BRICK_WIDTH, bricks[k][l].getY() + BRICK_HEIGHT);
					
					if(collider1 == explosion 
							|| collider2 == explosion
							|| collider3 == explosion
							|| collider4 == explosion) {
						
						if(bricks[k][l].getFillColor() == Color.BLACK) {
							continue;
						}
						
						bricks[k][l].setFilled(false);
						remove(bricks[k][l]);
						
					}
					
				}
			}
			
			remove(explosion);
		}
	}
	
	// Makes sound when ball hits something
	private void makeSound() {
		
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
		
	}
	
	// Function checks if player did not manage save the ball and in that case, resets locations of paddle and ball
	private int looseBall(GOval ball, GOval paddle, double [] ballMovementDirections, int life, GOval ball1, GOval ball2, GOval ball3, boolean additionalBall) {
		
		if(ball.getY() > paddle.getY()) {
			
			if(additionalBall == false) {
				
				ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2);
				
				if(ball1 == null && ball2 == null && ball3 == null) {
					
					paddle.setLocation(WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
					
				}
				
				ballMovementDirections[0] = (Math.random()-0.5)*4;
				ballMovementDirections[1] = 3;
				
				// Makes little delay until next ball spawns to correct mouse position
				if(ball1 == null && ball2 == null && ball3 == null) {
					
					makeDelay();
					
				}
				
			} else {
				
				remove(ball);
				
			}
			life--;
			
		}
		
		return life; // At the end, function returns life and die >_<
		
	}
	
	private void makeDelay() {
	
		for(int i = 0; i < 100; i++) {
			pause(SLEEP_TIME - LEVEL);
		}
		
	}
	
	// Shows some text after player loses.
	private void loserEmote() {
		
		removeAll();
		
		if(LEVEL > 1) {
			
			add(new GLabel("Good job coming this far!! Last level: " + LEVEL), WIDTH/2 - 90, HEIGHT/2);
		
		} else {
		
			add(new GLabel("Nice try, better luck next time!!", WIDTH/2 - 75, HEIGHT/2));
		
		}
		
	}
	
	private void victoryEmote() {

		removeAll();
		
		add(new GLabel("Congratulations!!!!! I never throught beating this game was possible, well done!!", WIDTH/2 - 160, HEIGHT/2));
		
	}
	
}



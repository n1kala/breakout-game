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

///////////////////////////////////////////   changeable global variables
/** delay time, decrease this number to make program faster */ 
	private long SLEEP_TIME = 9;
	
/** count of how many blocks did player pop */
	private int POPPED_COUNT = 0;
	
/** tells if player have super shot */
	private boolean SUPER_SHOT = false;
	
/** if player pops all the blocks he goes to next level where ball moves faster */
	private int LEVEL = 1;
	
/** if player pops ball adder block */
	private boolean addBalls = false;
	private double newBallX;
	private double newBallY;
	
/** laser shot */
	private boolean mouseIsDown = false;
	private boolean laserIsAvaliable = true;
/* Method: run() */

	public void run() {
		/* for some reason setSize does not set size same as passed values so I needed to add 18 and 72 */
		setSize(WIDTH + 18, HEIGHT + 72);
		setPlayButton();
		GRect [][] bricks = setBricks();
		GOval paddle = setPaddle();
		GOval ball = setBall(WIDTH/2 - BALL_RADIUS/2, HEIGHT/2 - BALL_RADIUS/2);
		setFrame(); // setting walls
		startGame(bricks, paddle, ball);
	}
	
	public void run() {
		waitForClick();
		mouseIsDown = true;
	}
	
	// function is infinity loop which updates positions of game objects
	private void startGame(GRect [][] bricks, GOval paddle, GOval ball) {
		
		int life = NTURNS;
		
		double ballMovementDirections [] = {(Math.random()-0.5)*4, 3}; // movement on X and Y
		double ballMovementDirections1 [] = {(Math.random()-0.5)*4, -3}; // movement on X and Y
		double ballMovementDirections2 [] = {(Math.random()-0.5)*4, -3}; // movement on X and Y
		double ballMovementDirections3 [] = {(Math.random()-0.5)*4, -3}; // movement on X and Y
		
		GOval ball1 = null;
		GOval ball2 = null;
		GOval ball3 = null;
		
		GLine marks [] = new GLine[MARKS_COUNT];
		
		int count = 0; // count of loops in while true loop, i use it in order to add marks of ball's movement
		
		int popped = 0;
		
		while(true) {
			if(LEVEL == 9) {
				victoryEmote();
				break;
			}
			
			// makes super shot after popping 10 blocks which 
			// super shot pops up to 3 blocks until getting back
			if(SUPER_SHOT) {
				leaveMark(marks, ballMovementDirections, count, ball);
				ball.setFillColor(Color.ORANGE);
			}
			// makes laser charge
			if(mouseIsDown && laserIsAvaliable) {
				print("WORKS");
				laserIsAvaliable = false;
//				shootLaser(bricks);
			}
			
			if(addBalls) {
				addBalls = false;
				ball1 = setBall(newBallX - BALL_RADIUS/2,newBallY - BALL_RADIUS/2);
				ball2 = setBall(newBallX - BALL_RADIUS/2,newBallY - BALL_RADIUS/2);
				ball3 = setBall(newBallX - BALL_RADIUS/2,newBallY - BALL_RADIUS/2);
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
			
			// moves paddle close to mouse's x coordinate
			double padleX = moveDirection(paddle.getX()); 
			if((paddle.getX() + PADDLE_WIDTH < WIDTH && padleX == 1) || (paddle.getX() > 0 && padleX == -1)) {
				paddle.setLocation(paddle.getX() + padleX*3, paddle.getY());
			}
			
			pause(SLEEP_TIME - LEVEL);
			
			int tempPop = POPPED_COUNT;
			if(popped <= 0) {
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
			}
			popped--;
			
			if(tempPop != POPPED_COUNT) {
				popped = 2;
			}
			
			// if there is no bricks left player gets to next level and everything resets
			if(ballMovementDirections[1] == 0) {
				LEVEL++;
				laserIsAvaliable = true;
				removeAll();
				ball1 = null;
				ball2 = null;
				ball3 = null;
				setPlayButton();
				bricks = setBricks();
				paddle = setPaddle();
				ball = setBall(WIDTH/2, HEIGHT/2);
				setFrame();
				startGame(bricks, paddle, ball);
				break;
			}
			
			// if ball is out player loses one of the lives
			life = looseBall(ball, paddle, ballMovementDirections, life, ball1, ball2, ball3, false);
			if(ball1 != null) {
				int tempLife = life;
				life = looseBall(ball1, paddle, ballMovementDirections1, life, ball1, ball2, ball3, true);
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
			
			if(life == 0) {
				loserEmote();
				break;
			}
			
			count++;
			
		}
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
	}
	
	// tells which level player is on
	private void setLevelLabel() {
		GLabel level = new GLabel("LEVEL----> " + LEVEL, WIDTH/2 - 30, HEIGHT/2 - 45);
		level.setColor(Color.BLUE);
		GLabel text = new GLabel("Lets see how far will you get. My personal best is level 5. Good luck!", WIDTH/2 - 180, HEIGHT/2 - 80);
		text.setColor(Color.BLACK);
		GLabel reminder = new GLabel("Remember, after poping at least 10 blocks you will get super shot!", WIDTH/2 - 180, HEIGHT/2 - 60);
		reminder.setColor(Color.RED);
		
		if(LEVEL == 0) {
			add(text);
			add(reminder);
		}
		add(level);
	}
	
	//i tried to add triangle to make button look like play button
	// but its just too many white lines bc i dont know how to make triangle
	private void decoratePlayButton() {
		// coordinates of top left bottom of triangle
		int x = WIDTH/2 - 18; 
		int y = HEIGHT/2 - 30;
		
		// coordinates of bottom left corner of triangle
		int x1 = x + 50;
		int y1 = y + 30;
		
		// makes line from middle right corner of triangle to everywhere on the line between other two corners
		while(y != y1+30) {
			GLine line = new GLine(x,y,x1,y1);
			line.setColor(Color.WHITE);
			add(line);
			y++;
		}
	}
	
	// function sets up bricks according to color requirements and returns bricks array
	// for each row x coordinate is updated by brick's width + separation between bricks
	// after each row y coordinate is updated by brick's height + separation
	private GRect [][] setBricks() {
		GRect [][] bricks = new GRect[NBRICK_ROWS][NBRICKS_PER_ROW];
		
		int ballAdderBlock = NBRICK_ROWS*NBRICKS_PER_ROW-1;
		ballAdderBlock *= Math.random();
		
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
		
		for(int y = 70,paint = 0,i = 0; y < BRICK_Y_OFFSET+NBRICK_ROWS*BRICK_HEIGHT + (NBRICK_ROWS-1)*BRICK_SEP; y += BRICK_HEIGHT + BRICK_SEP, paint++, i++) {
			for(int x = BRICK_SEP/2,j = 0; x < WIDTH; x += BRICK_WIDTH + BRICK_SEP, j++) {
				bricks[i][j] = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				bricks[i][j].setFilled(true);
				if(j + i*NBRICKS_PER_ROW != ballAdderBlock) {
					bricks[i][j].setColor(Color.getHSBColor(colors[paint][0], colors[paint][1], colors[paint][2]));
				}
				add(bricks[i][j]);
			}
		}
		
		return bricks;
	}

	// function sets up paddle and returns GRect of paddle
	private GOval setPaddle() {
		GOval padle = new GOval(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		padle.setFilled(true);
		add(padle);
		return padle;
	}
	
	// setting up ball
	private GOval setBall(double x, double y) {
		GOval ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	
	// function sets up walls
	// wall is just 4 GRects, black, white, grey and black again.
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
		// if mouse pointer is in the middle of paddle in order for it to not start shaking left and right
		// i made it so pointer should be away from paddles middle by at least 2 pixels to make paddle move
		if(MouseInfo.getPointerInfo().getLocation().getX()-(padleX + PADDLE_WIDTH - 5) > 2) {
			return 1;
		} else if (MouseInfo.getPointerInfo().getLocation().getX()-(padleX + PADDLE_WIDTH - 5) < -2){
			return -1;
		}
		return 0;
	}
	
	// function leaves lines so you know which movements ball did, it leaves up to 100 lines
	private void leaveMark(GLine [] marks, double [] ballMovementDirections, int count, GOval ball) {
		if(marks[count%MARKS_COUNT] != null) {
			remove(marks[count%MARKS_COUNT]);
		}
		double r = BALL_RADIUS;
		marks[count%MARKS_COUNT] = new GLine(ball.getX() + r, ball.getY() + r,
				ball.getX() + ballMovementDirections[0] + r, ball.getY() + ballMovementDirections[1] + r);
		marks[count%MARKS_COUNT].setColor(Color.ORANGE);
		add(marks[count%MARKS_COUNT]);
	} 
	
	// function changes ball's directions according to where did it hit
	private double [] directionChanges(double [] ballMovementDirections, GOval paddle, GOval ball, GRect [][] bricks, GLine [] marks, boolean addedBall) {
		double temp0 = ballMovementDirections[0], temp1 = ballMovementDirections[1];
		
		// when ball hits right wall
		if(ball.getX() >= WIDTH - BALL_RADIUS*2 - 8) {
			ballMovementDirections[0] = -Math.abs(ballMovementDirections[0]);
		}
		// when ball hits left wall
		if(ball.getX() <= 8) {
			ballMovementDirections[0] = Math.abs(ballMovementDirections[0]);
		}
		// when ball hits top wall
		if(ball.getY() <= 8) {
			ballMovementDirections[1] = Math.abs(ballMovementDirections[1]);
		}
		// when ball hits paddle
		if(ball.getX() + BALL_RADIUS*2 >= paddle.getX() && ball.getX() <= paddle.getX() + PADDLE_WIDTH && ball.getY() >= paddle.getY() - BALL_RADIUS*2) {
			
			double place = ball.getX() + BALL_RADIUS - paddle.getX(); // place on paddle where ball did hit
			
			// direction changes of ball depending on where on paddle it will hit
			ballMovementDirections[0] = (place - PADDLE_WIDTH/2)*0.14;
			
			ballMovementDirections[1] = -Math.abs(ballMovementDirections[1]);
			
			// reseting everything after super shot
			if(SUPER_SHOT && addedBall == false) {
				SUPER_SHOT = false;
				paddle.setFillColor(Color.BLACK);
				ball.setFillColor(Color.BLACK);
				for(int i = 0; i < MARKS_COUNT; i++) {
					remove(marks[i]);
				}
				POPPED_COUNT = 0;
			}
			
			// after every 10 popped blocks player gets super shot which pierces everything
			if(POPPED_COUNT >= 10 && addedBall == false) {
				SUPER_SHOT = true;
				paddle.setFillColor(Color.RED);
			}
		}
		// when ball hits blocks
		boolean brickIsLeft = false;
		boolean directionChanged = false;
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				
				// when block is already taken out
				if(bricks[i][j].isFilled() == false || directionChanged) {
					continue;
				}
				brickIsLeft = true;
				
				// if ball is hitting next block in row continue
				if(j < NBRICKS_PER_ROW - 1 && bricks[i][j+1].isFilled() == true && bricks[i][j+1].getX() - (ball.getX() + BALL_RADIUS) < BRICK_SEP/2) {
					continue;
				}
				
				// if ball is hitting next block in column continue
				if(i < NBRICK_ROWS - 1 && bricks[i+1][j].isFilled() == true && bricks[i+1][j].getY() - (ball.getY() + BALL_RADIUS) < BRICK_SEP/2) {
					continue;
				}
				
				// if (ball is touching current block)
				if(ball.getY() < bricks[i][j].getY() + BRICK_HEIGHT && ball.getY() + BALL_RADIUS*2 > bricks[i][j].getY() && 
						ball.getX() < bricks[i][j].getX() + BRICK_WIDTH && ball.getX() + BALL_RADIUS*2 > bricks[i][j].getX()) {
					
					if(bricks[i][j].getFillColor() == Color.BLACK) {
						addBalls = true;
						newBallY = bricks[i][j].getY() + BRICK_HEIGHT/2;
						newBallX = bricks[i][j].getX() + BRICK_WIDTH/2;
					}
					
					bricks[i][j].setFilled(false);
					remove(bricks[i][j]);
					
					if(SUPER_SHOT && addedBall == false) { // if its super shot direction do not changes
						continue;
					}
					double brickX = bricks[i][j].getX();
					double brickY = bricks[i][j].getY();
					double ballX = ball.getX();
					double ballY = ball.getY();
					
					// if (ball is touching current block)
					if(ball.getY() < brickY + BRICK_HEIGHT && ballY + BALL_RADIUS*2 > brickY && 
							ball.getX() < brickX + BRICK_WIDTH && ballX + BALL_RADIUS*2 > brickX) {

						bricks[i][j].setFilled(false);
						remove(bricks[i][j]);
						
						// if ball is touching block from left half
						if(ballX + BALL_RADIUS - brickX < BRICK_WIDTH/2) {
							// if ball is touching block from top half
							if(ballY + BALL_RADIUS - brickY < BRICK_HEIGHT/2) {
								// if ball is touching from left side
								// and ball is moving right because otherwise it can not be touching block from left
								// and there is not a block on the left side
								if(ballY + BALL_RADIUS*2 - brickY > ballX + BALL_RADIUS*2 - brickX && ballMovementDirections[0] >= 0 && bricks[i][j-1].isFilled() == false) {
									ballMovementDirections[0] *= -1;
								} else {
									ballMovementDirections[1] *= -1;
								}
							} else { 
								// ball is touching from bottom left half
								
								// if ball is touching from left
								// and ball is moving right because otherwise it can not be touching block from left
								// and there is not a block on the left side
								if(brickY + BRICK_HEIGHT - ballY > ballX + BALL_RADIUS*2 - brickX && ballMovementDirections[0] >= 0 && bricks[i][j-1].isFilled() == false) {
									ballMovementDirections[0] *= -1;
								} else {
									ballMovementDirections[1] *= -1;
								}
							}
						} else {
							// ball is touching from right half
							
							// if ball is touching from top 
							if(ballY + BALL_RADIUS - brickY < BRICK_HEIGHT/2) {
								// if ball is touching from top side 
								// or ball is moving to right because that time it can not be touching block from right side
								if(brickX + BRICK_WIDTH - ballX >= ballY + BALL_RADIUS*2 - brickY || ballMovementDirections[0] >= 0) {
									ballMovementDirections[1] *= -1;
								} else {
									ballMovementDirections[0] *= -1;
								}
							} else {
								// ball is touching from bottom right half
								
								// if ball is touching from bottom side
								// or ball is moving to right because that time it can not be touching block from right side
								if(brickX + BRICK_WIDTH - ballX > brickY + BRICK_HEIGHT - ballY || ballMovementDirections[0] >= 0) {
									ballMovementDirections[1] *= -1;
								} else {
									ballMovementDirections[0] *= -1;
								}
							}
						}
					}
					directionChanged = true;
				}
				
			}
			
		}
		// if direction changed that means ball popped block
		if(directionChanged) {
			POPPED_COUNT++;
		}
		// if there is not any bricks left this stops game and takes player to next level
		if(brickIsLeft == false) {
			ballMovementDirections[1] = 0;
		}
		
		if(temp0 != ballMovementDirections[0] || temp1 != ballMovementDirections[1]) {
//			 makeSound();
			// line above this will make sound whlie ball hits something, but it lags on my computer for some reason
		}
		
		return ballMovementDirections;
	}
	
	// makes sound when ball hits something, sounds lag on my pc so i turned it off
	private void makeSound() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}
	
	// function checks if player did not manage save the ball and in that case resets locations of paddle and ball
	private int looseBall(GOval ball, GOval paddle, double [] ballMovementDirections, int life, GOval ball1, GOval ball2, GOval ball3, boolean additionalBall) {
		if(ball.getY() > paddle.getY()) {
			if(additionalBall == false) {
				ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2);
				if(ball1 == null && ball2 == null && ball3 == null) {
					paddle.setLocation(WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
				}
				ballMovementDirections[0] = (Math.random()-0.5)*4;
				ballMovementDirections[1] = 3;
				// makes little delay until next ball spawns to correct mouse position
				if(ball1 == null && ball2 == null && ball3 == null) {
					for(int i = 0; i < 100; i++) {
						pause(SLEEP_TIME - LEVEL);
					}
				}
			} else {
				remove(ball);
			}
			life--;
			
		}
		return life; // function is real for this one... at the end, we return life and die >_<
	}

	// shows some text after player loses.
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



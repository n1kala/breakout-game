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
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

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
	
/* Method: run() */

	public void run() {
		/* for some reason setSize does not set size same as passed values so I needed to add 18 and 72 */
		setSize(APPLICATION_WIDTH + 18, APPLICATION_HEIGHT + 72);
		setPlayButton();
		GRect [][] bricks = setBricks();
		GOval paddle = setPaddle();
		GOval ball = setBall();
		setFrame(); // setting walls
		startGame(bricks, paddle, ball);
	}
	
	// function is infinity loop which updates positions of game objects
	private void startGame(GRect [][] bricks, GOval paddle, GOval ball) {
		
		int life = NTURNS;
		
		double ballMovementDirections [] = {(Math.random()-0.5)*4, 3}; // movement on X and Y
		
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
			
			// changing ball's position
			ball.setLocation(ball.getX() + ballMovementDirections[0], ball.getY() + ballMovementDirections[1]);
			
			// moves paddle close to mouse's x coordinate
			double padleX = moveDirection(paddle.getX()); 
			if((paddle.getX() + PADDLE_WIDTH < WIDTH && padleX == 1) || (paddle.getX() > 0 && padleX == -1)) {
				paddle.setLocation(paddle.getX() + padleX*3, paddle.getY());
			}
			
			delay();
			
			int tempPop = POPPED_COUNT;
			if(popped <= 0) {
				ballMovementDirections = directionChanges(ballMovementDirections, paddle, ball, bricks, marks);
			}
			popped--;
			
			if(tempPop != POPPED_COUNT) {
				popped = 2;
			}
			
			// if there is no bricks left player gets to next level and everything resets
			if(ballMovementDirections[1] == 0) {
				LEVEL++;
				removeAll();
				setPlayButton();
				bricks = setBricks();
				paddle = setPaddle();
				ball = setBall();
				setFrame();
				startGame(bricks, paddle, ball);
				break;
			}
			
			// if ball is out player loses one of the lives
			life = looseBall(ball, paddle, ballMovementDirections, life);
			
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
			for(int x = BRICK_SEP/2,j = 0; x < APPLICATION_WIDTH; x += BRICK_WIDTH + BRICK_SEP, j++) {
				bricks[i][j] = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				bricks[i][j].setFilled(true);
				bricks[i][j].setColor(Color.getHSBColor(colors[paint][0], colors[paint][1], colors[paint][2]));
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
	private GOval setBall() {
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS*2, getHeight()/2, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	
	// function sets up walls
	// wall is just 4 GRects, black, whilte, grey and black again.
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
	
	// function makes program have little delay to make it playable, otherwise everything will happen too fast
	// i copied delay()'s code from Microsoft Copilot
	private void delay() {
		try {
		    Thread.sleep(SLEEP_TIME - LEVEL); 
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	}

	// function changes ball's directions according to where did it hit
	private double [] directionChanges(double [] ballMovementDirections, GOval paddle, GOval ball, GRect [][] bricks, GLine [] marks) {
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
			if(SUPER_SHOT) {
				SUPER_SHOT = false;
				paddle.setFillColor(Color.BLACK);
				ball.setFillColor(Color.BLACK);
				for(int i = 0; i < MARKS_COUNT; i++) {
					remove(marks[i]);
				}
				POPPED_COUNT = 0;
			}
			
			// after every 10 popped blocks player gets super shot which pierces everything
			if(POPPED_COUNT >= 10) {
				SUPER_SHOT = true;
				paddle.setFillColor(Color.RED);
			}
		}
		// when ball hits blocks
		boolean brickIsLeft = false;
		boolean directionChanged = false;
		for(int i = 0; i < NBRICK_ROWS; i++) {
			boolean ballIsMovingRight = ballMovementDirections[0] > 0;
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				
				// when block is already taken out
				if(bricks[i][j].isFilled() == false || directionChanged) {
					continue;
				}
				brickIsLeft = true;
				
				if(ballIsMovingRight == false) {
					j = NBRICKS_PER_ROW - 1 - j;
				}
				
				// if (ball is touching current block)
				if(ball.getY() < bricks[i][j].getY() + BRICK_HEIGHT && ball.getY() + BALL_RADIUS*2 > bricks[i][j].getY() && 
						ball.getX() < bricks[i][j].getX() + BRICK_WIDTH && ball.getX() + BALL_RADIUS*2 > bricks[i][j].getX()) {

					bricks[i][j].setFilled(false);
					remove(bricks[i][j]);
					
					if(SUPER_SHOT) { // if its super shot direction do not changes
						continue;
					}
					
					// if ball is touching block from left half
					if(ball.getX() + BALL_RADIUS - bricks[i][j].getX() < BRICK_WIDTH/2) {
						// if ball is touching block from top half
						if(ball.getY() + BALL_RADIUS - bricks[i][j].getY() < BRICK_HEIGHT/2) {
							// if ball is touching from left side
							// and ball is moving right because otherwise it can not be touching block from left
							if(ball.getY() + BALL_RADIUS*2 - bricks[i][j].getY() > ball.getX() + BALL_RADIUS*2 - bricks[i][j].getX() && ballMovementDirections[0] >= 0) {
								ballMovementDirections[0] *= -1;
							} else {
								ballMovementDirections[1] *= -1;
							}
						} else { 
							// ball is touching from bottom left half
							
							// if ball is touching from left
							// and 
							if(bricks[i][j].getY() + BRICK_HEIGHT - ball.getY() > ball.getX() + BALL_RADIUS*2 - bricks[i][j].getX() && ballMovementDirections[0] >= 0) {
								ballMovementDirections[0] *= -1;
							} else {
								ballMovementDirections[1] *= -1;
							}
						}
					} else {
						// ball is touching from right half
						
						// if ball is touching from top 
						if(ball.getY() + BALL_RADIUS - bricks[i][j].getY() < BRICK_HEIGHT/2) {
							// if ball is touching from top side 
							// or ball is moving to right because that time it can nott be touching block from right side
							if(bricks[i][j].getX() + BRICK_WIDTH - ball.getX() > ball.getY() + BALL_RADIUS*2 - bricks[i][j].getY() || ballMovementDirections[0] >= 0) {
								ballMovementDirections[1] *= -1;
							} else {
								ballMovementDirections[0] *= -1;
							}
						} else {
							// ball is touching from bottom right half
							
							// if ball is touching from bottom side
							// or ball is moving to right because that time it can not be touching block from right side
							if(bricks[i][j].getX() + BRICK_WIDTH - ball.getX() > bricks[i][j].getY() + BRICK_HEIGHT - ball.getY() || ballMovementDirections[0] >= 0) {
								ballMovementDirections[1] *= -1;
							} else {
								ballMovementDirections[0] *= -1;
							}
						}
					}
					directionChanged = true;
				}
				
				if(ballIsMovingRight == false) {
					j = NBRICKS_PER_ROW - 1 - j;
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
	
	// makes sound when ball hits something
	private void makeSound() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}
	
	// function checks if player did not manage save the ball and in that case resets locations of paddle and ball
	private int looseBall(GOval ball, GOval paddle, double [] ballMovementDirections, int life) {
		if(ball.getY() > paddle.getY()) {
			ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2);
			paddle.setLocation(WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			ballMovementDirections[0] = (Math.random()-0.5)*4;
			ballMovementDirections[1] = 3;
			life--;
			// makes little delay until next ball spawns to correct mouse position
			for(int i = 0; i < 100; i++) {
				delay();
			}
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



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

public class Breakout extends GraphicsProgram {

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
	private static final double BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Sleep time of thread, decrease this to make program faster */
	private static final int SLEEP_TIME = 7;
	
///// Changeable variable /////
	
	// I needed to initialize this object globally, because mouseMoved can not have access on this otherwise
	private GRect paddle; 
	
/** Runs the Breakout program. */
	public void run() {
		
		/* For some reason setSize does not set size same as passed values so I needed to add 18 and 72*/
		setSize(APPLICATION_WIDTH + 18, APPLICATION_HEIGHT + 72);
		
		// Adding mouse listeners to know when it moved
		addMouseListeners();
		
		GRect [][] bricks = setBricks();
		paddle = setPaddle();
		GOval ball = setBall();
		
		setFrame();
		
		startGame(bricks, ball);
		
	}
	
	private GRect [][] setBricks() {
	
		GRect [][] bricks = new GRect[NBRICK_ROWS][NBRICKS_PER_ROW];
		
		// Colors of each row
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
		
		// X and Y coordinate tells where brick should be added and row is used to determine which color brick should be.
		for(int y = 70,row = 0,i = 0; y < BRICK_Y_OFFSET + NBRICK_ROWS*BRICK_HEIGHT + (NBRICK_ROWS-1)*BRICK_SEP; y += BRICK_HEIGHT + BRICK_SEP, row++, i++) {
			for(int x = BRICK_SEP/2,j = 0; x < APPLICATION_WIDTH; x += BRICK_WIDTH + BRICK_SEP, j++) {
				
				bricks[i][j] = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				bricks[i][j].setFilled(true);
				bricks[i][j].setColor(Color.getHSBColor(colors[row][0], colors[row][1], colors[row][2]));
				
				add(bricks[i][j]);
				
			}
		}
		
		return bricks;
	}

	private GRect setPaddle() {
		
		GRect padle = new GRect(getWidth()/2 - PADDLE_WIDTH/2,
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, 
				PADDLE_HEIGHT);
		
		padle.setFilled(true);
		add(padle);
		
		return padle;
	}
	
	private GOval setBall() {
		
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS*2,
				getHeight()/2,
				BALL_RADIUS*2,
				BALL_RADIUS*2);
		
		ball.setFilled(true);
		add(ball);
		
		return ball;
	}
	
	// Function sets up walls.
	// In reality its just four GRect next to each other with different colors
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
	
	// Function is infinity loop which updates positions of game objects
	private void startGame(GRect [][] bricks, GOval ball) {
		
		int life = NTURNS;
		
		// Ball's movement on X and Y
		double ballMovementDirections [] = {(Math.random()-0.5)*4, 3}; 
		
		while(true) {
		
			ball.setLocation(ball.getX() + ballMovementDirections[0], ball.getY() + ballMovementDirections[1]);
			
			pause(SLEEP_TIME);
	
			ballMovementDirections = directionChanges(ballMovementDirections, ball, bricks);
			
			// If there is no bricks left
			if(ballMovementDirections[1] == 0) {
				break;
			}
			
			// If ball is out player loses one of the lives
			life = looseBall(ball, ballMovementDirections, life);
			
			if(life == 0) {
				removeAll();
				break;
			}
			
		}
	}

	//Mouse tracker to make paddle follow mouse
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		
		double mouseX = e.getX();
		
		if(mouseX + PADDLE_WIDTH/2 < WIDTH && mouseX > PADDLE_WIDTH/2) {
		
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, paddle.getY());
	
		}
	}
	
	// Function changes ball's directions according to where did it hit
	private double [] directionChanges(double [] ballMovementDirections, GOval ball, GRect [][] bricks) {
		
		// When ball hits right wall
		if(ball.getX() >= WIDTH - BALL_RADIUS*2) {
			ballMovementDirections[0] = -Math.abs(ballMovementDirections[0]);
		}
		
		// When ball hits left wall
		if(ball.getX() <= 0) {
			ballMovementDirections[0] = Math.abs(ballMovementDirections[0]);
		}
		
		// When ball hits top wall
		if(ball.getY() <= 5) {
			ballMovementDirections[1] = Math.abs(ballMovementDirections[1]);
		}
		
		// When ball hits paddle
		if(ball.getX() + BALL_RADIUS*2 >= paddle.getX() 
				&& ball.getX() <= paddle.getX() + PADDLE_WIDTH 
				&& ball.getY() >= paddle.getY() - BALL_RADIUS*2) {
			
			ballMovementDirections[1] = -Math.abs(ballMovementDirections[1]);
			
		}
		
		// These variables will tell if any brick is left and if ball did hit one of the bricks
		boolean brickIsLeft = false;
		boolean directionChanged = false;
		
		
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
				if(ball.getY() < brickY + BRICK_HEIGHT 
						&& ballY + BALL_RADIUS*2 > brickY 
						&& ball.getX() < brickX + BRICK_WIDTH 
						&& ballX + BALL_RADIUS*2 > brickX) {

					bricks[i][j].setFilled(false);
					remove(bricks[i][j]);
					
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
		
		// If there is not any bricks left this stops game
		if(brickIsLeft == false) {
		
			ballMovementDirections[1] = 0;
		
		}
		
		return ballMovementDirections;
		
	}
	
	// Function checks if player did not manage save the ball and in that case resets locations of paddle and ball
	private int looseBall(GOval ball, double [] ballMovementDirections, int life) {
		
		if(ball.getY() + BALL_RADIUS*2 >= HEIGHT) {
			
			ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2);
			
			paddle.setLocation(WIDTH/2 - PADDLE_WIDTH/2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			
			ballMovementDirections[0] = (Math.random()-0.5)*4;
			ballMovementDirections[1] = 3;
			
			life--;
			
		}
		
		return life; // At the end, function returns life and die >_<
		
	}
}



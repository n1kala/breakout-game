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
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		/* You fill this in, along with any subsidiary methods */
		setSize(APPLICATION_WIDTH + 18, APPLICATION_HEIGHT + 72);
		setPlayButton();
		GRect [][] bricks = setBricks();
		GRect paddle = setPaddle();
		GOval ball = setBall();
		setFrame();
		startGame(bricks, paddle, ball);
	}
	
	private void setPlayButton() {
		int R = 40;
		int x = WIDTH/2 - R;
		int y = HEIGHT/2 - R;
		
		GOval playButton = new GOval(x,y,R*2,R*2);
		playButton.setFilled(true);
		playButton.setColor(Color.GREEN);
		add(playButton);
		
		decoratePlayButton();
		
		waitForClick();
		removeAll();
	}
	
	private void decoratePlayButton() {
		int x = WIDTH/2 - 18;
		int y = HEIGHT/2 - 30;
		int x1 = x + 50;
		int y1 = y + 30;
		while(y != y1+30) {
			GLine line = new GLine(x,y,x1,y1);
			line.setColor(Color.WHITE);
			add(line);
			y++;
		}
	}
	
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

	private GRect setPaddle() {
		GRect padle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		padle.setFilled(true);
		add(padle);
		return padle;
	}
	
	private GOval setBall() {
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	
	// not yet added
	private void setFrame() {
		
	}
	
	private void startGame(GRect [][] bricks, GRect paddle, GOval ball) {
		int life = NTURNS;
		double ballMovementDirections [] = {(Math.random()-0.5)*4, 3}; // movement on X and Y
		
		while(true) {
			ball.setLocation(ball.getX() + ballMovementDirections[0], ball.getY() + ballMovementDirections[1]);
			
			double padleX = moveDirection(paddle.getX()); 
			if((paddle.getX() + PADDLE_WIDTH < WIDTH && padleX == 1) || (paddle.getX() > 0 && padleX == -1)) {
				paddle.setLocation(paddle.getX() + padleX*3, paddle.getY());
			}
			
			delay();
			
			ballMovementDirections = directionChanges(ballMovementDirections, paddle, ball, bricks);
			
			life = looseBall(ball, paddle, ballMovementDirections, life);
			
			if(life == 0) {
				break;
			}
		}
		
	}
	
	private double moveDirection(double padleX) {
		if(MouseInfo.getPointerInfo().getLocation().getX()-(padleX + PADDLE_WIDTH - 5) > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	// program will have 100 fps if you want to increase it, decrease sleep time 
	private void delay() {
		try {
		    Thread.sleep(10); // Pause for 0.01 second
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	}

	private double [] directionChanges(double [] ballMovementDirections, GRect paddle, GOval ball, GRect [][] bricks) {
		if(ball.getX() >= WIDTH - BALL_RADIUS) {
			ballMovementDirections[0] = -ballMovementDirections[0];
		}
		if(ball.getX() <= 0) {
			ballMovementDirections[0] = -ballMovementDirections[0];
		}
		if(ball.getY() <= 5) {
			ballMovementDirections[1] = -ballMovementDirections[1];
		}
		if(ball.getX() > paddle.getX() && ball.getX() < paddle.getX() + PADDLE_WIDTH && ball.getY() >= paddle.getY() - BALL_RADIUS) {
			ballMovementDirections[1] = -ballMovementDirections[1];
		}
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				if(bricks[i][j].isFilled() == false) {
					continue;
				}
				
				if(ball.getX() >= bricks[i][j].getX() && ball.getX() <= bricks[i][j].getX() + BRICK_WIDTH &&
						ball.getY() <= bricks[i][j].getY() + BRICK_HEIGHT && ball.getY() >= bricks[i][j].getY()) {
					remove(bricks[i][j]);
					ballMovementDirections[1] = -ballMovementDirections[1];
				}
				
				
			}
		}
		
		return ballMovementDirections;
	}
	
	private int looseBall(GOval ball, GRect paddle, double [] ballMovementDirections, int life) {
		if(ball.getY() > paddle.getY() + 1) {
			ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2);
			paddle.setLocation(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			life--;
			ballMovementDirections[0] = (Math.random()-0.5)*4;
			ballMovementDirections[1] = 3;
		}
		return life;
	}
}



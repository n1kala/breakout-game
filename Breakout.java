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
		waitForClick();
		removeAll();
		setBricks();
		startGame();
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
	
	private void setBricks() {
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
		for(int j = 70,paint = 0; j < BRICK_Y_OFFSET+NBRICK_ROWS*BRICK_HEIGHT + (NBRICK_ROWS-1)*BRICK_SEP; j += BRICK_HEIGHT + BRICK_SEP, paint++) {
			for(int i = BRICK_SEP/2; i < APPLICATION_WIDTH; i += BRICK_WIDTH + BRICK_SEP) {
				GRect rect = new GRect(i,j,BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);
				rect.setColor(Color.getHSBColor(colors[paint][0], colors[paint][1], colors[paint][2]));
				add(rect);
			}
		}
	}

	private void startGame() {
		int life = NTURNS;
		
		GRect padle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		padle.setFilled(true);
		add(padle);
		
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		double arr [] = {(Math.random()-0.5)*4, 3};
		while(true) {

			ball.setLocation(ball.getX() + arr[0], ball.getY() + arr[1]);
			double padleX = MouseInfo.getPointerInfo().getLocation().getX()-(padle.getX() + PADDLE_WIDTH/2); 
			if(padleX > 0) padleX = 1; 
			else padleX = -1;
			padle.setLocation(padle.getX() + padleX*3, padle.getY());
			try {
			    Thread.sleep(10); // Pause for 1 second
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
			if(ball.getX() >= WIDTH) {
				arr[0] = -arr[0];
			}
			if(ball.getX() <= 0) {
				arr[0] = -arr[0];
			}
			if(ball.getY() <= 5) {
				arr[1] = -arr[1];
 			}
			if(ball.getX() > padle.getX()) {
				if(ball.getX() < padle.getX() + PADDLE_WIDTH) {
					if(ball.getY() >= padle.getY()) {
						arr[1] = -arr[1];
					}
				}
			}
			if(ball.getY() > padle.getY() + 10) {
				ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2);
				padle.setLocation(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
				life--;
				arr[0] = (Math.random()-0.5)*4;
				arr[1] = 3;
			}
			if(life == 0) {
				break;
			}
		}
		
	}
	
}

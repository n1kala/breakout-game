# Setup Guide for Advanced Breakout Game

This guide will help you get the game running on your system.

## Step 1: Check if Java is Installed

Open Command Prompt (Windows), Terminal (Mac), or Terminal (Linux) and type:
```bash
java -version
```

You should see output like:
```
java version "1.8.0_xxx" or "11.0.x" or "17.0.x" etc.
```

**If Java is NOT installed**, go to [Java Downloads](https://www.oracle.com/java/technologies/downloads/) and install Java 8 or later.

## Step 2: Download/Clone the Repository

If you don't have the files yet:
```bash
git clone <repository-url>
cd Assignment3
```

## Step 3: Run the Game

### Option A: Windows (Easiest)
Simply **double-click `run.bat`**

The game will compile and start automatically!

### Option B: Manual (All Platforms)

Open Terminal/Command Prompt in the `Assignment3` folder and run:

**Windows:**
```bash
javac -cp .;acm.jar -source 8 -target 8 advancedBreakout.java
java -cp .;acm.jar advancedBreakout
```

**Mac/Linux:**
```bash
javac -cp .:acm.jar -source 8 -target 8 advancedBreakout.java
java -cp .:acm.jar advancedBreakout
```

## Step 4: Play!

- Move your mouse to control the paddle
- Click to launch the ball
- Break all the bricks to win!

## Common Issues

**"Could not find or load main class"**
- Make sure you're in the `Assignment3` folder
- Make sure `acm.jar` is in the same folder

**"javac: command not found"**
- Java is not installed or not in your PATH
- Restart your terminal after installing Java

**Game doesn't display**
- Make sure you have a graphical display available
- Linux users: You may need to install X11 display server

## Need More Help?

See `README.md` for detailed troubleshooting.

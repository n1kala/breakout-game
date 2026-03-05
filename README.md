# Advanced Breakout Game

A Java implementation of the Breakout game using the ACM Graphics Library.

## Requirements

- **Java 8 or later** installed on your system
- Command Prompt/Terminal (Windows/Mac/Linux)

## Quick Start

### Windows Users
Simply **double-click `run.bat`** - it will compile and run the game automatically!

### Mac/Linux Users or Manual Setup

1. Open Terminal and navigate to this folder:
   ```bash
   cd <path-to-Assignment3-folder>
   ```

2. Compile the code:
   - **Windows (Command Prompt):**
     ```bash
     javac -cp .;acm.jar -source 8 -target 8 advancedBreakout.java
     ```
   - **Mac/Linux:**
     ```bash
     javac -cp .:acm.jar -source 8 -target 8 advancedBreakout.java
     ```

3. Run the game:
   - **Windows:**
     ```bash
     java -cp .;acm.jar advancedBreakout
     ```
   - **Mac/Linux:**
     ```bash
     java -cp .:acm.jar advancedBreakout
     ```

## How to Play

- **Move Paddle**: Use your mouse to move the paddle left and right
- **Start Game**: Click to launch the ball
- **Objective**: Break all the bricks without letting the ball fall below the paddle
- **Lives**: You have 3 attempts to beat the game

## Troubleshooting

| Error | Solution |
|-------|----------|
| **"Could not find or load main class advancedBreakout"** | Make sure you're in the correct directory where `acm.jar` and `advancedBreakout.java` are located |
| **"acm.jar is not recognized"** | If using PowerShell instead of Command Prompt, replace `;` with `:` in the classpath |
| **"Unsupported Class Version Error"** | Make sure you have Java 8 or later installed. Check with: `java -version` |
| **Game doesn't start** | Ensure `acm.jar` is in the same directory as the compiled `.class` files |

## Files

- `advancedBreakout.java` - Main game code
- `acm.jar` - ACM Graphics Library (required for running)
- `bounce.au` - Sound file
- `run.bat` - Quick launch script for Windows
- `compile_and_run.bat` - Alternative batch script

## Installing Java 8+

**Windows:**
- Download from: https://www.oracle.com/java/technologies/downloads/
- Select Java SE 8 or later
- Run the installer and follow the prompts
- Verify by opening Command Prompt and typing: `java -version`

**Mac:**
- Use Homebrew: `brew install openjdk@8`
- Or download from Oracle's website

**Linux:**
- Ubuntu/Debian: `sudo apt-get install openjdk-8-jdk`
- Fedora: `sudo dnf install java-1.8.0-openjdk`

## Notes

- Keep `acm.jar` in the same directory as `advancedBreakout.java`
- The game requires a graphical display
- Compatible with Windows, Mac, and Linux

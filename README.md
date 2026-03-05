# Advanced Breakout Game

A Java implementation of the Breakout game using the ACM Graphics Library.

## Requirements

- **Java 8 or later** installed on your system
- Command Prompt (Windows) or Terminal (Mac/Linux)

## How to Compile and Run

### Option 1: Using the Batch Script (Windows Only)
```bash
double-click run.bat
```

### Option 2: Manual Compilation (All Platforms)

1. **Open Command Prompt/Terminal** and navigate to this folder:
   ```bash
   cd <path-to-Assignment3-folder>
   ```

2. **Compile the code:**
   ```bash
   javac -cp .;acm.jar -source 8 -target 8 advancedBreakout.java
   ```

3. **Run the game:**
   ```bash
   java -cp .;acm.jar advancedBreakout
   ```

## Troubleshooting

- **"Could not find or load main class"**: Make sure you're in the correct directory and the classpath includes `acm.jar`
- **"acm.jar is not recognized"**: You're using PowerShell. Use Command Prompt instead, or replace `;` with `:` on Mac/Linux
- **Unsupported Class Version Error**: Use `-source 8 -target 8` flags when compiling (see instructions above)

## Files

- `advancedBreakout.java` - Main game code
- `Breakout.java` - Supporting code
- `acm.jar` - ACM Graphics Library (required)
- `bounce.au` - Sound file
- `run.bat` - Automated compilation and run script (Windows)

## Notes

Make sure to keep `acm.jar` in the same directory as the source files for compilation and execution to work properly.

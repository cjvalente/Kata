# Quiz App for Direct Supply Software Engineering Intern Kata (JavaFX)
Author: CJ Valente
Project: Direct Supply Software Engineering Internship Kata
Language: Java
Framework: JavaFX
Build Tool: Maven

## Overview
This project is a desktop trivia quiz application built with Java/JavaFX that allows users to create customizable quizes with the Open Trivia Database API.
Users can generate custom quizes by selecting:
- Number of questions
- Category
- Difficulty
- Question type
The application retrieves questions from the API, presents them, tracks the user's score, and displays a leaderboard upon completion.
This project follows the MVC pattern seperating UI controllers, service logic, and model classes.

## Features
### Quiz customization
Users can customize their quiz with:
- 1-50 questions
- Category selection via the Open Trivia Database
- Difficulty level: easy, medium, hard
- Question type: Multiple choice, true/false
### Quiz Gameplay
- Questions are loaded from the API
- Answer buttons are generated at run time
- Correct answers highlight green
- Incorrect answers highlight red
- Buttons are disabled and selected and/or correct answers fade in for visual feedback
- Score is tracked throughout the entirety of the quiz
### Results Screen
After completing the quiz, the user sees:
- Final Score
- Leaderboard loaded from file (currently dummy data)
- Option to restart the quiz
### Leaderboard
Leaderboard entries are loaded from src/main/resources/leaderboard.txt
Each entry contains rank, name, score
Displayed via JavaFX TableView

## Architecture
This application is organized using an MVC-style structure
### Controllers
Handle all UI logic and user interaction
SetUpController - Configure quiz settings
QuizController - Manages quiz gameplay
ResultsController - Displays score and leaderboard
### Models
Respresent application data
Question - Stores question text, all answers, and correct answer
LeaderboardEntry - Represents a leaderboard row
QuizSession - Stores session data, such as a player name
### Services
Handle External data and API communication
TriviaApiClient - Handles open trivia database API requests.

## API Integration
This application uses the Open Trivia Database API
Category Endpoint: https://opentdb.com/api_category.php
Question Endpoint Example for 10 questions and all other categories are "any": https://opentdb.com/api.php?amount=10

## Maven Build System
This project uses Maven for dependency management and build configuration
The pom.xml manages:
- Java version
- JavaFX dependencies
- Gson for JSON parsing
- Apache commons text for HTML decode

## How to Run

### 1. Clone the repository
git clone https://github.com/cjvalente/Kata.git

### 2. Build the project with Maven
From the project root:
mvn clean install

### 3. Run the app:
You can run the JavaFX app using your IDE or Maven
From Intellij/VsCode
Run: Main.java

## UI Screens
### Welcome Screen
Allows the user to enter their name
### Setup Screen
Users customize their quiz with number of questions, cateogry, difficulty, and question type.

### Quiz Screen
Displays: current question, answer choices, progress bar, next buton

### Results Screen
Displays: final score, leaderboard, play again button

## Error handling
This application includes error handling for:
- API Request failures
- invalid parameters
- rate limiting
- network timeout
- file loading issues
Errors are displayed via JavaFX Alert

## Future Improvements
Potential Improvement include
- Accurate and persistent leaderboard using a database
- Creating users
- Timer for quiz questions
- High score tracking
- Improved UI
- Multiplayer quiz
- User can create their own quiz

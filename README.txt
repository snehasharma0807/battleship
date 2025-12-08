=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: 51061765
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays
  I used 10x10 2D arrays to represent the player's and computer's board. Each
  cell in the first array stores the state of that position on the board (whether
  that cell is empty (no ship, hasn't been fired at), has a ship (has a ship, hasn't
  been fired at), is a hit (has a ship that has been fired at), or is a miss (no ship,
  was fired at). Another 2D array called shipLocs helps me track which ships is in
  each cell so I could see which ship was hit. I used 2D arrays because the Battleship
  gameboard is a 2D grid that doesn't change in size. This meant that 2D arrays were
  a really natural way to represent this information & look up specific positions.

  2. JUnit Testable Component
  The game logic is mainly in BattleshipGame and BattleshipBoard, which are independent
  of the GUI components in GameBoard. This allowed me to test the game logic through calls
  to various functions. I implemented 20 JUnit tests covering validations of placing ships
  on the board, how to shoot at the board, checking the state of the game, verifying that
  File I/O seems to be functioning as intended, and testing edge cases like file errors
  and bound errors.

  3. Collections
  I used a TreeMap<Integer, Shot> to store the history of all shots fired during the game.
  The Integer represents the shot number and the Shot is an object that contains the shot's
  row, column, result, and who it was fired by. I then allowed users to view the full history
  of shots in a given game stored through the TreeMap. TreeMap made sense to store the shots
  in chronological order, since TreeMaps are ordered data structures. Also, because of the
  key-value structure of TreeMaps, it was easy to retrieve specific shots and iterate through
  shots in order.

  4. File I/O
  I used File I/O for two things. First, it allowed me to store the entire game state to a
  file for the user to load later, even if the game is exited. The board state, ship positions,
  shot history, etc. were all stored. Second, I used File I/O to track and save the player's win
  history to display basic statistics about how often they win vs. the computer.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
    BattleshipBoard: Manages a 10x10 game board & stores the cell states and ship
        locations. Handles ship placement, processing shots, and tracking whether
        all of the ships have been sunk or not.
    BattleshipGame: Where the main game functionality happens (manages both player
        and the computer board, tracks whose turn it is, fires shots, implements
        the save and loading functionality, etc.)
    Ship: Abstract class that defined the common behaviors shared amongst ships.
        It tracks each ship's name, size, hit count, and allows iteractions with
        the ships like checking if its sunk, hitting the ship, etc.
    BattleShip, Carrier, Destroyer, Cruiser, Submarine: Subclasses that extend
        Ship, the difference being the respective lengths of the ships.
    Cell: An enum that represents the four possible states of a cell in the board:
        EMPTY (no ship, hasn't been fired at), SHIP (has a ship, hasn't been fired
        at), HIT (has a ship that has been fired at), MISS (no ship, was fired at).
    Game: Contains main method to launch and run my code.
    GameBoard: Where all of the GUI happens (extends JPanel). Creates visual display
        of the game.
    GameState: An enum that represents the current game state: MY_TURN (the user is
        placing the ships), OPP_TURN (computer placing ships), PLAYING_CURRENTLY
        (game is being played), GAME_ENDED (game is over).
    GameStats: Tracks the player and computer's wins and saves/formats/loads the
        statistics.
    Opponent: Logic for the computer to fire shots. It randomly fires shots until
        one hits a ship, targets adjacent cells until the ship has sunk, then goes
        back to random shots.
    ResultOfShot: An enum that represents the outcome of a fired shot: HIT, MISS,
        SUNK, or REPEAT.
    Shot: Stores information about a single shot fired (row, column, the result
        of that shot, and who fired it).


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  I faced a lot of issues when I was creating the logic for the computer's shots.
  Originally, my computer opponent was firing shots randomly. However, this led to a really
  basic game, since it was super easy to beat the computer every time. So, I decided to try and
  figure out how logic would work for smarter guesses by the computer. It took a lot of
  trial and error, but I finally got there after I mapped down the flow of logic on paper.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
  I was able to encapsulate all of the variables in every class. However, there are some methods
  that I feel like I could have turned private, because outside classes don't (and shouldn't ever
  need to) interact with them. If I could, I would go through a lot of the helper methods
  and verify whether they should be private or public.



========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.
  The rules for my Battleship game mainly game from this website:
  - https://gameonfamily.com/blogs/tutorials/battleship?srsltid=AfmBOor7lu8QhIRtVbJrrv0rnwYJ2rDIdiyFMa9X4xO7ngbArwPVTotj

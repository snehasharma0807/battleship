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

  2. Inheritance/Subtyping

  3. Collections

  4. File I/O

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

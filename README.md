I've implemented the game logic using Akka in the files in `app/models/`. The
umpire and players are represented by actors. The umpire requests plays (i.e.
one of rock, paper, scissors, lizard or spock) from the players, who send back a
play. The umpire computes points and keeps a score tally.

I've tried to implement the UI using play with some progress, but my lack of web
development experience proves to be a limiting factor in what I can do here.
I've put together a web app that goes through the game setup process asking for
the user's name and the number of AI players and rounds, validating the format
of the inputs given for the players and rounds.

Connecting the two parts together was more than I could manage. Please bear in
mind that this is the first web app I've ever built!

#### Instructions

- The web app can be build and run with `sbt run`, and can be played with by
  navigating to `localhost:9000`.
- A game can be simulated with output on the command line by opening the REPL
  with `sbt console` and then running `models.Manager.runGame(players, rounds)`
  where players and rounds are integers.

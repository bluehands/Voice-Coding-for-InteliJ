// See https://aka.ms/new-console-template for more information

using GameOfLife;

while (true)
{
    string? input;
    Console.WriteLine("Welcome to Game of Life. Which setup do you want to play?");
    input = Console.ReadLine();
    var game = input != null ? new GameMaster(input) : new GameMaster();
    Console.Clear();
    Console.WriteLine(game.CurrentGeneration());
    Console.WriteLine("Start setup generated. Want to evolve this setup? [yes] no");
    input = Console.ReadLine();
    var playing = input != null && !input.ToLower().Contains('n');
    while (playing)
    {
        Console.Clear();
        Console.WriteLine(game.NextGeneration());
        Console.WriteLine("Another Generation? [yes] no");
        input = Console.ReadLine();
        if (input != null)
        {
            input = input.ToLower();
            if (input.Contains('n')) playing = false;
        }
    }
    Console.WriteLine("Game Finished! Another Round? [yes] no");
    input = Console.ReadLine();
    if (input != null)
    {
        input = input.ToLower();
        if (input.Contains('n')) break;
    }
}
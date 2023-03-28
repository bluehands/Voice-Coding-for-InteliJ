namespace GameOfLife;

public enum SetUp
{
    basic
}

public class GameMaster
{
    private GameBoard board;
    private Visualizer visual;

    public GameMaster()
    {
        board = BlockSetup();
        visual = new Visualizer(board);
    }
    
    public GameMaster(string setUp)
    {
        switch (setUp)
        {
            case "basic":
                board = BlinkerSetup();
                break;
            case "slider":
                board = SliderSetup();
                break;
            case "dissolver":
                board = DissolverSetup();
                break;
            case "lwss":
                board = LwSsSetup();
                break;
            default: 
                board = BlockSetup();
                break;
        }
        visual = new Visualizer(board);
    }

    public string NextGeneration()
    {
        var nullError = false;
        for (var x = 0; x <= board.MaximumXIndex(); x++)
        {
            for (var y = 0; y <= board.MaximumYIndex(); y++)
            {
                var neighbors = board.GetNeighbors(x, y);
                if (neighbors == null)
                {
                    nullError = true;
                    break;
                }

                board.GetCell(x, y).CalculateNextGeneration(neighbors);
            }
        }

        if (nullError) return visual.GenerateVisualization() + "\nError: Could not calculate next generation!";
        for (int x = 0; x <= board.MaximumXIndex(); x++)
        {
            for (int y = 0; y <= board.MaximumYIndex(); y++)
            {
                board.GetCell(x, y).StartNextGeneration();
            }
        }

        return visual.GenerateVisualization();
    }

    /*public string NextGeneration2()
    {
        var nullError = false;
        for (int i = 0; i < board.MaximumXIndex(); i++)
        {
            for (int j = 0; j < board.MaximumYIndex(); j++)
            {
                var neighbor = board.GetNeighbors(i, j);
                if (neighbor == null)
                {
                    nullError = true;
                    break;
                }
                board.GetCell(i, j).CalculateNextGeneration(neighbor);
            }
        }
        return "";
    }*/
    public string NextGeneration2()
    {
        var nullError = false;
        
        return "";
    }

    public string CurrentGeneration()
    {
        return visual.GenerateVisualization();
    }

    private GameBoard BlockSetup()
    {
        var gameBoard = new GameBoard(6, 6);
        gameBoard.GetCell(2, 2).Alive = true;
        gameBoard.GetCell(2, 3).Alive = true;
        gameBoard.GetCell(3, 2).Alive = true;
        gameBoard.GetCell(3, 3).Alive = true;
        return gameBoard;
    }

    // private GameBoard TestSetup()
    // {
    //     var gameBoard = new GameBoard(6, 6);
    //     gameBoard.GetCell(2, 2).Alive = true;
    //     gameBoard.GetCell(2, 3).Alive = true;
    //     gameBoard.GetCell(3, 2).Alive = true;
    //     gameBoard.GetCell(3, 3).Alive = true;
    //     return gameBoard;
    // }
   
    
    
    private GameBoard BlinkerSetup()
    {
        var gameBoard = new GameBoard(5, 5);
        gameBoard.GetCell(2, 1).Alive = true;
        gameBoard.GetCell(2, 2).Alive = true;
        gameBoard.GetCell(2, 3).Alive = true;
        return gameBoard;
    }

    private GameBoard SliderSetup()
    {
        var gameBoard = new GameBoard(10, 10);
        gameBoard.GetCell(0, 2).Alive = true;
        gameBoard.GetCell(1, 2).Alive = true;
        gameBoard.GetCell(2, 2).Alive = true;
        gameBoard.GetCell(2, 1).Alive = true;
        gameBoard.GetCell(1, 0).Alive = true;
        return gameBoard;
    }

    private GameBoard DissolverSetup()
    {
        var gameBoard = new GameBoard(21, 21);
        gameBoard.GetCell(9, 7).Alive = true;
        gameBoard.GetCell(10, 7).Alive = true;
        gameBoard.GetCell(11, 7).Alive = true;
        gameBoard.GetCell(9, 8).Alive = true;
        gameBoard.GetCell(11, 8).Alive = true;
        gameBoard.GetCell(9, 9).Alive = true;
        gameBoard.GetCell(11, 9).Alive = true;
        gameBoard.GetCell(9, 11).Alive = true;
        gameBoard.GetCell(11, 11).Alive = true;
        gameBoard.GetCell(9, 12).Alive = true;
        gameBoard.GetCell(11, 12).Alive = true;
        gameBoard.GetCell(9, 13).Alive = true;
        gameBoard.GetCell(10, 13).Alive = true;
        gameBoard.GetCell(11, 13).Alive = true;
        return gameBoard;
    }
    
    private GameBoard LwSsSetup()
    {
        var gameBoard = new GameBoard(10, 40);
        gameBoard.GetCell(0, 8).Alive = true;
        gameBoard.GetCell(0, 6).Alive = true;
        gameBoard.GetCell(1, 5).Alive = true;
        gameBoard.GetCell(2, 5).Alive = true;
        gameBoard.GetCell(3, 5).Alive = true;
        gameBoard.GetCell(4, 5).Alive = true;
        gameBoard.GetCell(4, 6).Alive = true;
        gameBoard.GetCell(4, 7).Alive = true;
        gameBoard.GetCell(3, 8).Alive = true;
        return gameBoard;
    }
}
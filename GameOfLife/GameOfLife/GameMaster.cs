namespace GameOfLife;

public enum SetUp
{
    basic
}

public class GameMaster
{
    private GameBoard _gameBoard;
    private Visualizer _visualizer;

    public GameMaster()
    {
        _gameBoard = BlockSetup();
        _visualizer = new Visualizer(_gameBoard);
    }
    
    public GameMaster(string setUp)
    {
        switch (setUp)
        {
            case "basic":
                _gameBoard = BlinkerSetup();
                break;
            case "slider":
                _gameBoard = SliderSetup();
                break;
            case "dissolver":
                _gameBoard = DissolverSetup();
                break;
            case "lwss":
                _gameBoard = LwSsSetup();
                break;
            default: 
                _gameBoard = BlockSetup();
                break;
        }
        _visualizer = new Visualizer(_gameBoard);
    }

    public string NextGeneration()
    {
        var nullError = false;
        for (var x = 0; x <= _gameBoard.MaximumXIndex(); x++)
        {
            for (var y = 0; y <= _gameBoard.MaximumYIndex(); y++)
            {
                var neighbors = _gameBoard.GetNeighbors(x, y);
                if (neighbors == null)
                {
                    nullError = true;
                    break;
                }
                _gameBoard.GetCell(x,y).CalculateNextGeneration(neighbors);
            }
        }

        if (nullError) return _visualizer.GenerateVisualization() + "\nError: Could not calculate next generation!";
        for (int x = 0; x <= _gameBoard.MaximumXIndex(); x++)
        {
            for (int y = 0; y <= _gameBoard.MaximumYIndex(); y++)
            {
                _gameBoard.GetCell(x, y).StartNextGeneration();
            }
        }
        return _visualizer.GenerateVisualization();
    }

    public string CurrentGeneration()
    {
        return _visualizer.GenerateVisualization();
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
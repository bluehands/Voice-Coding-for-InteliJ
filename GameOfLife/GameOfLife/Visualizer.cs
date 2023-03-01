namespace GameOfLife;

public class Visualizer
{
    private readonly GameBoard _board;
    
    public Visualizer(GameBoard board)
    {
        _board = board;
    }

    public String GenerateVisualization()
    {
        var visualizationString = "\n";
        for (int y = 0; y <= _board.MaximumYIndex(); y++)
        {
            for (int x = 0; x <= _board.MaximumXIndex(); x++)
            {
                visualizationString += " " + (_board.GetCell(x, y).Alive ? "X": " ");
            }
            visualizationString += "\n";
        }
        return visualizationString;
    }
}
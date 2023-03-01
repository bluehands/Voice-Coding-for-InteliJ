namespace GameOfLife;

public class GameBoard
{
    private readonly Cell[][] _cellBoard;
    private readonly int _maximumYIndex;
    private readonly int _maximumXIndex;
    
    public GameBoard(int height, int width)
    {
        _maximumYIndex = height - 1;
        _maximumXIndex = width - 1;
        _cellBoard = new Cell[width][];
        for (var w = 0; w < width; w++)
        {
            _cellBoard[w] = new Cell[height];
            for (var h = 0; h < height; h++)
            {
                _cellBoard[w][h] = new Cell();
            }
        }
    }

    public int MaximumYIndex()
    {
        return _maximumYIndex;
    }

    public int MaximumXIndex()
    {
        return _maximumXIndex;
    }

    public Cell GetCell(int x, int y)
    {
        return _cellBoard[x][y];
    }

    public Cell[]? GetNeighbors(int x, int y)
    {
        Cell[]? neighbors = null;
        if (x == 0)
        {
            if (y == 0)
            {
                neighbors = new[]{
                    _cellBoard[1][0],
                    _cellBoard[0][1], _cellBoard[1][1] 
                };
            }
            else if (y == _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[0][y - 1], _cellBoard[1][y - 1],
                    _cellBoard[1][y]
                };
            }
            else if (0 < y && y < _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[0][y - 1], _cellBoard[1][y - 1]
                                        , _cellBoard[1][y],
                    _cellBoard[0][y + 1], _cellBoard[1][y + 1]
                };
            }
        }
        else if (x == _maximumXIndex)
        {
            if (y == 0)
            {
                neighbors = new[]{
                    _cellBoard[x - 1][0],
                    _cellBoard[x - 1][1], _cellBoard[x][1]
                };
            }
            else if (y == _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[x - 1][y - 1], _cellBoard[x][y - 1],
                    _cellBoard[x - 1][y]
                };
            }
            else if (0 < y && y < _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[x - 1][y - 1], _cellBoard[x][y - 1],
                    _cellBoard[x - 1][y],
                    _cellBoard[x - 1][y + 1], _cellBoard[x][y + 1]
                };
            }
        }
        else if (0 < x && x < _maximumXIndex)
        {
            if (y == 0)
            {
                neighbors = new[] {
                    _cellBoard[x - 1][0], _cellBoard[x + 1][0],
                    _cellBoard[x - 1][1], _cellBoard[x][1], _cellBoard[x + 1][1]
                    };
            }
            else if (y == _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[x - 1][y - 1], _cellBoard[x][y - 1], _cellBoard[x + 1][y - 1],
                    _cellBoard[x - 1][y], _cellBoard[x + 1][y]
                };
            }
            else if (0 < y && y < _maximumYIndex)
            {
                neighbors = new[] {
                    _cellBoard[x - 1][y - 1], _cellBoard[x][y - 1], _cellBoard[x + 1][y - 1],
                    _cellBoard[x - 1][y], _cellBoard[x + 1][y],
                    _cellBoard[x - 1][y + 1], _cellBoard[x][y + 1], _cellBoard[x + 1][y + 1] 
                };
            }
        }
        return neighbors;
    }
}
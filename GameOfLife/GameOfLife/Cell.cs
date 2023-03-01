namespace GameOfLife;

public class Cell
{
    private bool _aliveNextGen;
    public bool Alive { get; set; }

    public void CalculateNextGeneration(Cell[] neighbors)
    {
        var livingNeighbors = 0;
        foreach (var neighbor in neighbors)
        {
            if (neighbor.Alive) livingNeighbors++;
        }

        switch (livingNeighbors)
        {
            case 3:
                _aliveNextGen = true;
                break;
            case 2:
                _aliveNextGen = Alive;
                break;
            case < 2:
            case > 3:
                _aliveNextGen = false;
                break;
        }
    }

    public void StartNextGeneration()
    {
        Alive = _aliveNextGen;
    }
}
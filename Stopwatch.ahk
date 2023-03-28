!+g::
StartTime := A_TickCount
return

!+j::
ElapsedTime := A_TickCount - StartTime
FileAppend, %ElapsedTime%`n, E:\BA-Coding\Stopwatch.txt
return
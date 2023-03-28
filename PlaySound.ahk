index := 1

#1::
SoundPlay, C:\Users\Public\Documents\VoiceCodingPlugin\BatchRecordings\insert.wav, Wait
SoundPlay, C:\Users\Public\Documents\VoiceCodingPlugin\BatchRecordings\%index%.wav
StartTime := A_TickCount
index++
return

#4::
SoundPlay, C:\Users\Public\Documents\VoiceCodingPlugin\BatchRecordings\insert.wav, Wait
index := 1
return

#2::
ElapsedTime := A_TickCount - StartTime
FileAppend, %ElapsedTime%`n, E:\BA-Coding\Serenade_Timer.txt
return

#3::
SoundPlay, C:\Users\Public\Documents\VoiceCodingPlugin\BatchRecordings\%index%.wav, Wait
return
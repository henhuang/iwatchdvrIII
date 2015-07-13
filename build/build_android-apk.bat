set PATH=%PATH%;D:\work\Android\tools\apache-ant-1.9.2\bin

set workfd=autobuild_android-apk_%random%
set rootfd=%temp%\%workfd%
robocopy ..\src "%rootfd%" /a-:rash /e /xo /purge /r:0 /xd LibCore LibPeerMedia WebService

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::

set src=iWatchDVR2_OEM
set dst=iWatchDVR2
cd /d %rootfd%
md release\android\apk
md build\log

:: seems ADT bug in windows (?)
rd /s /q ZXing-2.1\android\bin\res\crunch

for /f "tokens=*" %%i in ('dir %src% /a:d /b') do (
robocopy %src%\%%i %dst% /e
cd %dst%
echo ============%%i============%cd%
call ant release >release.log
move bin\iwatchdvr2-release.apk ..\release\android\apk\%%i.apk
move release.log ..\build\log\build_android-apk_%%i.log
cd ..
)

xcopy %rootfd%\build\log %~dp0.\log /i /y 
xcopy %rootfd%\release\android\apk %~dp0..\release\android\apk /i /y
pause

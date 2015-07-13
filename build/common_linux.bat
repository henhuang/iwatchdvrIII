set workfd=autobuild_%platform%_%random%

net use %remote% /user:%user% %password%
md "%remote%\%workfd%"
robocopy .. "%remote%\%workfd%" /a-:rash /e /xo /purge /r:0 /xd build debug intermediate iViewDVR4G iWatchDVR2_OEM remote res 

set script="%temp%\%workfd%.txt"
set log=build_%platform%.log

echo source /etc/profile > %script%
echo cd %home%/%workfd%/src >> %script%
echo make platform=%platform% ^> %log% 2^>^&1 >> %script%

plink -ssh -batch -pw %password% -m %script% %user%@%host%

md log
copy /y "%remote%\%workfd%\src\%log%" log\
robocopy "%remote%\%workfd%\release\%platform%" "..\release\%platform%" /a-:rash /e /xo /purge /r:0

echo source /etc/profile > %script%
echo rm -rf %home%/%workfd% > %script%

plink -ssh -batch -pw %password% -m %script% %user%@%host%

del %script%
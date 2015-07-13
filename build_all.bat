rd/s/q release

cd build
rd/s/q log
md log

for %%f in (build_*.bat) do start "%%f" /i cmd.exe /c %%f

explorer log
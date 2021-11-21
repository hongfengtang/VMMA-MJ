rem @echo off

setlocal


title RHS

cd /d d:\VMMA

rem
rem Execute Java Command
rem
echo "ICMA Starting..."
rem copy .\data\mcma.db.org .\data\mcma.db

for /f "delims=" %%i in ('wmic process where "name='java.exe'" get commandline ^| findstr "VMMA"') do set a=%%i

if NOT "%a%"=="" (
	echo The application is running
	pause
	goto END
)

rem if "%1"=="h" goto begin 

rem start mshta vbscript:createobject("wscript.shell").run("""%~nx0"" h",0)(window.close)&&exit 

:begin 

:chw
rasdial dial1 /d
rasdial dial1

@ping -n 2 192.168.244.3>nul ||@ping -n 3 127.0>nul & goto chw

rem cd D:\VMMA\
cmd /c java -jar VMMA-0.0.1-SNAPSHOT.jar



:END
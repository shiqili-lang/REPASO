@echo off
setlocal EnableDelayedExpansion

set MODE=%1
if "%MODE%"=="" set MODE=help
set TARGET_CLASS=%2

set SRC_DIR=src
set TEST_DIR=test
set BIN_DIR=bin
set LIBS_DIR=libs
set MANIFEST=META-INF\MANIFEST.MF
set MAIN_CLASS=aplicacion.Main
set JAR_NAME=app.jar
set RUNNER_CLASS=edu.classroom.tdd.ConsoleLauncherLite

call :build_cp

if /i "%MODE%"=="compile" goto :do_compile
if /i "%MODE%"=="run" goto :do_run
if /i "%MODE%"=="test" goto :do_test
if /i "%MODE%"=="testclass" goto :do_testclass
if /i "%MODE%"=="jar" goto :do_jar
if /i "%MODE%"=="clean" goto :do_clean
if /i "%MODE%"=="all" goto :do_all

echo.
echo Uso: build.bat [compile^|run^|test^|testclass NOMBRE_COMPLETO^|jar^|clean^|all]
echo.
echo Flujo TDD recomendado:
echo   1. build.bat test
echo   2. tocar src/ o test/
echo   3. build.bat testclass aplicacion.CalculadoraTest
echo   4. repetir
goto :eof

:build_cp
set CP_LIB=
if exist "%LIBS_DIR%" (
  for %%F in ("%LIBS_DIR%\*.jar") do (
    if "!CP_LIB!"=="" (
      set CP_LIB=%%~fF
    ) else (
      set CP_LIB=!CP_LIB!;%%~fF
    )
  )
)
goto :eof

:collect_files
set FILES=
if exist "%SRC_DIR%" (
  for /r "%SRC_DIR%" %%F in (*.java) do set FILES=!FILES! "%%F"
)
if exist "%TEST_DIR%" (
  for /r "%TEST_DIR%" %%F in (*.java) do set FILES=!FILES! "%%F"
)
goto :eof

:do_compile
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"
call :collect_files
if "!FILES!"=="" (
  echo No se encontraron archivos Java.
  exit /b 1
)
echo.
echo [compile] Compilando src/ y test/ ...
if "%CP_LIB%"=="" (
  javac -encoding UTF-8 -d "%BIN_DIR%" !FILES!
) else (
  javac -encoding UTF-8 -cp "%BIN_DIR%;%CP_LIB%" -d "%BIN_DIR%" !FILES!
)
if errorlevel 1 exit /b 1
echo [compile] OK
exit /b 0

:do_run
call :do_compile
if errorlevel 1 exit /b 1
echo.
echo [run] Ejecutando %MAIN_CLASS% ...
if "%CP_LIB%"=="" (
  java -cp "%BIN_DIR%" %MAIN_CLASS%
) else (
  java -cp "%BIN_DIR%;%CP_LIB%" %MAIN_CLASS%
)
exit /b %errorlevel%

:do_test
call :do_compile
if errorlevel 1 exit /b 1
echo.
echo [test] Ejecutando todos los tests ...
if "%CP_LIB%"=="" (
  java -cp "%BIN_DIR%" %RUNNER_CLASS%
) else (
  java -cp "%BIN_DIR%;%CP_LIB%" %RUNNER_CLASS%
)
exit /b %errorlevel%

:do_testclass
if "%TARGET_CLASS%"=="" (
  echo Debes indicar el nombre completo de la clase de test.
  echo Ejemplo: build.bat testclass aplicacion.CalculadoraTest
  exit /b 1
)
call :do_compile
if errorlevel 1 exit /b 1
echo.
echo [testclass] Ejecutando %TARGET_CLASS% ...
if "%CP_LIB%"=="" (
  java -cp "%BIN_DIR%" %RUNNER_CLASS% %TARGET_CLASS%
) else (
  java -cp "%BIN_DIR%;%CP_LIB%" %RUNNER_CLASS% %TARGET_CLASS%
)
exit /b %errorlevel%

:do_jar
call :do_compile
if errorlevel 1 exit /b 1
if not exist "%MANIFEST%" (
  echo No se encontro %MANIFEST%
  exit /b 1
)
pushd "%BIN_DIR%"
jar cfm "..\%JAR_NAME%" "..\%MANIFEST%" .
popd
if errorlevel 1 exit /b 1
echo [jar] Generado %JAR_NAME%
exit /b 0

:do_clean
if exist "%BIN_DIR%" rmdir /s /q "%BIN_DIR%"
if exist "%JAR_NAME%" del /q "%JAR_NAME%"
mkdir "%BIN_DIR%" >nul 2>nul
echo [clean] Limpieza hecha.
exit /b 0

:do_all
call :do_test
if errorlevel 1 exit /b 1
call :do_run
exit /b %errorlevel%

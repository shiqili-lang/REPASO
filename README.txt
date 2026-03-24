ARQUETIPO WINDOWS + TDD (sin Maven)

Estructura:
- src/    codigo de produccion
- test/   pruebas
- libs/   librerias
- bin/    clases compiladas

Incluye:
- build.bat para compilar, probar y ejecutar
- tareas de VSCode orientadas a TDD
- libreria ligera compatible con un subconjunto de la API de JUnit Jupiter

Comandos:
- build.bat test
- build.bat testclass aplicacion.CalculadoraTest
- build.bat compile
- build.bat run
- build.bat clean

Notas:
- Los imports de test usan org.junit.jupiter.api.*
- Cuando quieras pasar a JUnit oficial, basta con reemplazar el jar de libs/ por el oficial y ajustar el lanzador si lo deseas.

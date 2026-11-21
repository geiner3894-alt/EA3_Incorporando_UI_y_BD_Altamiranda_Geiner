# EA3. Incorporando UI y BD

Desarrollé una aplicación de escritorio en Java Swing para comprobar la conexión con una base de datos SQLite. La ventana permite escribir la URL, ingresar las credenciales y probar la conexión. Si ocurre un error, el resultado aparece en la misma ventana.

## Ejecución en Windows

Usé Visual Studio Code y el JDK 21. Para ejecutar el proyecto, abrí la carpeta que contiene `src` y `lib` y escribí estos dos comandos en la terminal:

```powershell
javac -encoding UTF-8 -d bin src\ConexionTester.java src\PruebaJdbc.java
java -cp "bin;lib/*" ConexionTester
```

Para la prueba con SQLite, dejé la URL `jdbc:sqlite:inventario.db`, mantuve vacíos los campos de usuario y contraseña y pulsé **Probar conexión**. Cuando la conexión se establece, la aplicación muestra en verde el nombre del motor.

El informe en PDF contiene las capturas de las pruebas. El ZIP reúne el informe, el código fuente, el controlador de SQLite y el archivo `Codificacion.csv`.

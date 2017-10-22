
## Introducción

El starter para trabajar con jdbc desde Spring Boot es ``spring-boot-starter-jdbc``, con esto obtendremos las siguientes ventajas de autoconfiguración:

1. Incluye la librería **tomcat-jdbc** para el manejo del Pool de Conexiones.

1. Si no tenemos un DataSource configurado y en el classpath tenemos cargado el driver de una base de datos como H2, HSQL o Derby, Spring Boot registrará automáticamente un DataSource para usar la base de datos en memoria.

1. Si tenemos los archivos schema.sql y data.sql en la carpeta **/resource** Spring Boot se encargará de ejecutarlos en la base de datos para una inicialización de datos.

## Primer ejemplo
Vamos a realizar un sencillo servicio rest que devuelva las áreas o departamentos que existen en una empresa. Usaremos la base de datos H2 en memoría y le cargaremos datos mediante los archivos  **schema.sql** y **data.sql**

Agreguemos pues las siguientes dependencias al **build.gradle**

    compile('org.springframework.boot:spring-boot-starter-jdbc')
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('com.h2database:h2')
    
Para cargar con datos a la base de datos automáticamente cada vez que se inicie el proyecto, debemos agregar estos dos archivos dentro de la carpeta **/resources**

---
**schema.sql**

```sql
CREATE TABLE departamento (
  id IDENTITY PRIMARY KEY,
  nombre VARCHAR(64) NOT NULL
);

CREATE TABLE empleado (
  id IDENTITY PRIMARY KEY,
  nombre VARCHAR(64) NOT NULL,
  departamento_id INT,
  CONSTRAINT FK_EMP_DEPT_ID FOREIGN KEY(departamento_id) REFERENCES departamento(id)
);
```
---
**data.sql**

```sql
insert into departamento(id, nombre) values(1, 'Sistemas');
insert into departamento(id, nombre) values(2, 'Contabilidad');
insert into departamento(id, nombre) values(3, 'Auditoria');
insert into departamento(id, nombre) values(4, 'Recursos Humanos');
insert into departamento(id, nombre) values(5, 'Mantenimiento');

insert into empleado(id, nombre, departamento_id) values(1, 'Ascari Romo', 1);
insert into empleado(id, nombre, departamento_id) values(2, 'Juan Zavala', 2);
insert into empleado(id, nombre, departamento_id) values(3, 'Antonio Pérez', 3);
insert into empleado(id, nombre, departamento_id) values(4, 'Marco Ávila', 3);
insert into empleado(id, nombre, departamento_id) values(5, 'Pedro Martínez', 1);
```
---

Con solo realizar esta configuración como mínimo podemos ejecutar el proyecto con bootRun y en el log de la aplicación observar la ejecución de nuestros scripts sql:

    2017-10-15 00:32:24.109  INFO 11888 --- [           main] o.s.jdbc.datasource.init.ScriptUtils     : Executing SQL script from URL [file:/Users/ascariromopedraza/Documents/Blogs/blog/codigo-tutoriales-blog/spring-jdbc/build/resources/main/schema.sql]
    2017-10-15 00:32:24.171  INFO 11888 --- [           main] o.s.jdbc.datasource.init.ScriptUtils     : Executed SQL script from URL [file:/Users/ascariromopedraza/Documents/Blogs/blog/codigo-tutoriales-blog/spring-jdbc/build/resources/main/schema.sql] in 62 ms.
    2017-10-15 00:32:24.175  INFO 11888 --- [           main] o.s.jdbc.datasource.init.ScriptUtils     : Executing SQL script from URL [file:/Users/ascariromopedraza/Documents/Blogs/blog/codigo-tutoriales-blog/spring-jdbc/build/resources/main/data.sql]
    2017-10-15 00:32:24.187  INFO 11888 --- [           main] o.s.jdbc.datasource.init.ScriptUtils     : Executed SQL script from URL [file:/Users/ascariromopedraza/Documents/Blogs/blog/codigo-tutoriales-blog/spring-jdbc/build/resources/main/data.sql] in 12 ms.
    2

Por default, cuando Spring Boot tiene el starter **spring-boot-starter-jdbc** y no hemos configurado ningún DataSource y tenemos cargada la dependencia para alguna de las bases de datos como H2, HSQL o Derby, Spring Boot configurará el DataSource automáticamente para trabajar con la base de datos en memoria.

Veamos algo más de acción y mediante un sencillo servicio REST visualicemos los datos:

```java
@RestController
public class JdbcController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/departamentos")
    public List<Departamento> getUsers(){
        return jdbcTemplate.query("select * from departamento",
                new BeanPropertyRowMapper<>(Departamento.class));
    }
}

```

También necesitamos los siguientes POJOS.

```java
public class Departamento {

    private int id;
    private String nombre;
    
    // setters y getters
}
```

```java
public class Empleado {

    private int id;
    private String nombre;

    private Departamento departamento;
    
    //setters y getters
}
```

Ingresa desde tu navegador a [http://localhost:8080/departamentos](http://localhost:8080/departamentos) y verás que tenemos los departamentos que cargamos desde el archivo **data.sql**

---

## Conexión a MySQL


Con muy poco código hemos hecho un pequeño servicio que devuelve datos de una base de datos. Esta es la magia de Spring Boot. Claro está que en la mayoría de las aplicaciones necesitaremos algo más que una base de datos en memoria.

Vamos a configurar algunos parámetros de conexión a una base de datos MySQL para que Spring Boot pueda crear un DataSource usando estos datos. Agregamos las siguientes líneas:

---
**application.properties**

    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    spring.datasource.url=jdbc:mysql://127.0.0.1:3306/springbootdb
    spring.datasource.username=root
    spring.datasource.password=ask123
    
---


Desde luego que debes tener creada la base de datos ``springbootdb`` en mysql y cambiar los datos de username y password por los que tengas configurados tu.

Mediante estos datos, Spring Boot podrá configurar un DataSource que se conecte a MySQL en lugar de hacerlo a H2. También es necesario agregar la dependencia del controlador jdbc para mysql.

---
**build.gradle**
    
    compile('mysql:mysql-connector-java')

---

Spring Boot intentará inicializar la base de datos mediante los archivos **schema.sql** y **data.sql**, sin embargo estos scripts generarán un error debido a que mysql tiene una sintáxis ligeramente diferente. Debemos crear un nuevo archivo:

---
**schema-mysql.sql**

```sql
CREATE TABLE IF NOT EXISTS departamento (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(64) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS empleado (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(64) NOT NULL,
  departamento_id MEDIUMINT,
  PRIMARY KEY(id),
  FOREIGN KEY(departamento_id) REFERENCES departamento(id)

);
```

---

Si intentamos correr el proyecto, Spring Boot lanzará una excepción debido a que seguirá intentanto inicializar la base de datos mysql con el archivo schema.sql y no con schema-mysql.sql. Spring Boot puede inicializar la base de datos desde los archivos **schema-${platform}.sql** y **data-${platform}.sql** en donde ${platform} es el valor asignado en la propiedad **spring.datasource.platform** que colocamos en el **application.properties**. Los valores pueden ser hsqldb, h2, oracle, mysql, postgresql, etc. De esta forma debemos colocar la siguiente línea:

---
**application.properties**
    
    spring.datasource.platform=mysql
    
---

Con esto Spring Boot intentará inicializar mysql con el archivo schema-mysql.sql, sin embargo después también intentará ejecutar el archivo schema.sql.

Para evitar esto, al archivo **schema.sql** lo renombramos a **schema-h2.sql**

El archivo data.sql no tiene problemas y las sentencias inserts que contiene funcionan bien para mysql y h2, por lo tanto este archivo queda intacto.

Si ejecutamos de nueva cuenta el proyecto, veremos el mismo resultado solo que ahora nuestra aplicación se conecta a una base de datos MySQL y no a H2 en memoria.

Si detenemos la aplicación y la volvemos a ejecutar, Spring Boot nuevamente inicializará la base de datos con los archivos schema-mysql.sql y data.sql sin embargo obtendremos el siguiente error:

``Duplicate entry '1' for key 'PRIMARY'``
    
Para evitar que Spring Boot inicialice a la base de datos debemos establecer en false a la propiedad **spring.datasource.initialize** debido a que por default es true.

---
**application.properties**

    spring.datasource.initialize=false

---

## Pruebas Unitarias

Aunque en una prueba unitaria se deben aislar los factores externos como una base de datos, en ocasiones puede ser interesante o necesario tener un par de pruebas que nos permitan evaluar el correcto funcionamiento de una función en particular que interactua con una base de datos. Sin embargo, esto en ocasiones llega a ser un verdadero problema cuando el equipo de desarrollo lo integran varias personas y todas usan la misma base de datos de desarrollo.

La mayoría de las veces la información que existe en una base de datos de desarrollo cambia con el tiempo y esto provoca que nuestras pruebas unitarias no pasen el test. Cuando nos encontramos en este escenario podemos hacer que alguna de nuestras pruebas utilicen una base de datos embebida en la memoria del equipo!

Vamos a crear una nueva clase repositorio:

```java
@Repository
public class DepartamentoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Departamento> findAll(){
        return jdbcTemplate.query("select * from departamento",
                new BeanPropertyRowMapper<>(Departamento.class));
    }
}
```

Para indicarle a Spring Boot que utilice un DataSource que se conecte a una base de datos H2 y que **únicamente** lo haga cuando estemos ejecutando la aplicación con el profile test, debemos configurar el DataSource de forma programática. 

Basta añadir el siguiente código a una clase que tenga la anotación ``@Configuration``. En este ejemplo, lo he puesto en la clase del punto de partida de toda aplicación Spring Boot, es decir, aquella que tiene la anotación ``@SpringBootApplication``

```java
@Bean
@Profile("test")
public DataSource testDataSource() {

    return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
}
```

Finalmente, creamos nuestra prueba unitaria estableciendo el profile a "test" para que Spring Boot inyecte el DataSource de conexión a H2 y no a MySQL. Sin embargo, recordemos que en el **application.properties** hemos establecido la plataforma a mysql **spring.datasource.platform=mysql**

Spring Boot buscará el script schema-mysql.sql en lugar de schema-h2.sql. Para cambiar esto, lo único que necesitamos es crear un nuevo archivo de propiedades que se ejecute con el perfil "test".

---
**application-test.properties**

    spring.datasource.platform=h2
    
---

Spring Boot automáticamente cargará las propiedades del application-test.properties y ahí es donde cambiamos que la plataforma será h2 en lugar de mysql. Con esto ya estamos en posibilidades de crear nuestra prueba unitaria.

```java
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test") //establecemos a "test" el profile para iniciar esta prueba.
public class SpringJdbcApplicationTests {

	@Autowired
	DepartamentoRepository repository;

	@Test
	public void contarDepartamentos() {
		List<Departamento> departamentos = repository.findAll();
		assertNotNull(departamentos); // validamos que la lista no sea null
		assertEquals(5, departamentos.size()); // validamos que existan 5 departamentos
	}
}
```

Al ejecutar la prueba Spring Boot configurará el DataSource para conectarse a la base de datos embebida H2 y no a MySQL.

Puedes leer otro pequeño tutorial que escribí sobre los [profiles en Spring Boot](https://windoctor7.github.io/Personalizando-Configuracion-Spring.html)

Como hemos podido revisar, Spring Boot facilita enormemente el trabajo con Spring. Con muy poco código podemos crear potentes aplicaciones.

Déjame saber que te pareció este tutorial con un comentario.

Finalmente te dejo el video demostrativo de este tutorial y no olvides que puedes descargar el código fuente.



## Introducción
Una de las tendencias en el desarrollo web moderno es tener un API RESTful como back-end y como front-end una aplicación desarrollada en Angular 2, además que mediante un API RESTful podemos desarrollar también aplicaciones móviles que utilicen estos mismos servicios.

La autenticación entre el API RESTful y sus consumidores es conveniente realizarla mediante tokens, específicamente usando el estándar [JSON Web Token](https://tools.ietf.org/html/rfc7519). La autenticación basada en tokens proporciona varias ventajas de las cuales no hablaremos aquí.

El siguiente diagrama muestra el flujo general de un proceso de autenticación basada en token.

![Image of Yaktocat](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/auth_jwt.png)  

1. El cliente envía sus credenciales (usuario y password) al servidor.
1. Si las credenciales son válidas, el servidor devuelve al cliente un token de acceso.
1. El cliente solicita un recurso protegido. En la petición, se envía el token de acceso.
1. El servidor valida el token y en caso de ser válido, devuelve el recurso solicitado.

---

## JSON Web Token
[JWT](https://jwt.io/introduction/) consta de 3 partes separadas por un punto ( . )

- Header
- Payload
- Signature

Cada una de estas partes se codifica en base64 de tal forma que el token generado tiene una apariencia como esta,

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
    .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9
    .TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
    
#### Header
El header consta de dos partes, el tipo de token y el algoritmo de hash.

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

Si al JSON anterior lo codificamos en base64 tendremos nuestra primer parte del JWT.

#### Payload
El payload contiene datos como: iss (issuer), exp (expiration time) y sub  (subject)

- **iss** es quien emitió el token
- **exp** contiene la fecha de expiración del token
- **sub** indica el usuario del token

Además podemos indicar otros campos como el nombre, roles, etc.

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true,
  "exp": "1425390142"
}
```

Al codificar el json anterior en base 64, obtenemos la segunda parte de nuestro JWT. Para más información recomiendo leer el [siguiente enlace](https://jwt.io/introduction/)

---

## Creando la aplicación web
Crearemos un sencillo servicio que devuelva una lista de usuarios

```java
@RestController
public class UsuariosController {

    @GetMapping(path = "/users")
    public List<Usuario> getUsers(){
        return Arrays.asList(new Usuario(1,"Paco"), new Usuario(2,"Pedro"), new Usuario(3, "Juan"));
    }
}
```

Si ejecutamos esta aplicación e ingresamos a http://localhost:8080/users nos pedirá un usuario y contraseña. Esto es así porque Spring Boot otorga una configuración de seguridad por defecto. El usuario por defecto es: user

La contraseña la podremos ver en la consola

    Using default security password: 8b22178d-8caa-4121-a525-9c551ffdfdb6

---

## Seguridad con JWT
En este punto nuestro servicio **/users** está expuesto a todo mundo. Necesitamos agregar la capa de seguridad, para ello incluimos las siguientes dependencias a nuestro archivo build.gradle (o POM.xml en caso de maven)

    compile group: 'org.springframework.boot', name: 'spring-boot-starter-security', version: '1.5.3.RELEASE'
    compile group: 'io.jsonwebtoken', name: 'jjwt', version: '0.7.0'

Ahora definiremos las reglas de seguridad mediante una clase a la que llamaremos ``SecurityConfig``

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
            .antMatchers("/login").permitAll() //permitimos el acceso a /login a cualquiera
            .anyRequest().authenticated() //cualquier otra peticion requiere autenticacion
            .and()
            // Las peticiones /login pasaran previamente por este filtro
            .addFilterBefore(new LoginFilter("/login", authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
                
            // Las demás peticiones pasarán por este filtro para validar el token
            .addFilterBefore(new JwtFilter(),
                    UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Creamos una cuenta de usuario por default
        auth.inMemoryAuthentication()
                .withUser("ask")
                .password("123")
                .roles("ADMIN");
    }
}
```

Note que ``LoginFilter`` y ``JwtFilter`` son clases que nosotros debemos crear y tendrán la función de filtros.

``LoginFilter``se encargará de interceptar las peticiones que provengan de **/login** y obtener el username y password que vienen en el body de la petición. 

```java
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    public LoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {

        // obtenemos el body de la peticion que asumimos viene en formato JSON
        InputStream body = req.getInputStream();

        // Asumimos que el body tendrá el siguiente JSON  {"username":"ask", "password":"123"}
        // Realizamos un mapeo a nuestra clase User para tener ahi los datos
        User user = new ObjectMapper().readValue(body, User.class);

        // Finalmente autenticamos
        // Spring comparará el user/password recibidos
        // contra el que definimos en la clase SecurityConfig
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        // Si la autenticacion fue exitosa, agregamos el token a la respuesta
        JwtUtil.addAuthentication(res, auth.getName());
    }
}

class User {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

Los comentarios en el código explican por si solo su funcionamiento. Lo que hemos hecho hasta aquí es definir nuestras reglas de acceso en la clase SecurityConfig e indicamos un user/password por default que se carga en memoria, para pequeños servicios esto está bien, sin embargo en casos más críticos tendremos más usuarios y deberíamos almacenarlos en una base de datos. En otro cookbook explicaré como hacer esto, por lo pronto nos bastará tener un único usuario cargado en memoria. 

Cuando llega una petición **/login** nuestro filtro ``LoginFilter`` se encargará de validar las credenciales y en caso de ser válidas, creará un JWT y se enviará de regreso al cliente. A partir de aquí el cliente deberá enviar este mismo token al servidor cada vez que solicite recursos protegidos. Podemos observar que tenemos una clase de utilidad llamada ``JwtUtil`` la cuál usamos para crear el token.

```java
public class JwtUtil {

    // Método para crear el JWT y enviarlo al cliente en el header de la respuesta
    static void addAuthentication(HttpServletResponse res, String username) {

        String token = Jwts.builder()
            .setSubject(username)
                
            // Vamos a asignar un tiempo de expiracion de 1 minuto
            // solo con fines demostrativos en el video que hay al final
            .setExpiration(new Date(System.currentTimeMillis() + 60000))
            
            // Hash con el que firmaremos la clave
            .signWith(SignatureAlgorithm.HS512, "P@tit0")
            .compact();

        //agregamos al encabezado el token
        res.addHeader("Authorization", "Bearer " + token);
    }

    // Método para validar el token enviado por el cliente
    static Authentication getAuthentication(HttpServletRequest request) {
        
        // Obtenemos el token que viene en el encabezado de la peticion
        String token = request.getHeader("Authorization");
        
        // si hay un token presente, entonces lo validamos
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey("P@tit0")
                    .parseClaimsJws(token.replace("Bearer", "")) //este metodo es el que valida
                    .getBody()
                    .getSubject();

            // Recordamos que para las demás peticiones que no sean /login
            // no requerimos una autenticacion por username/password 
            // por este motivo podemos devolver un UsernamePasswordAuthenticationToken sin password
            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}
```

Nuevamente los comentarios en el código explican su funcionamiento.

Finalmente implementamos nuestro segundo filtro. Este filtro tendra como función "validar" el token proporcionado por el cliente. Pongo entre comillas validar puesto que esta tarea no la hará propiamente el filtro, sino que usará nuestra clase de utilidad ``JwtUtil`` 

```java
/**
 * Las peticiones que no sean /login pasarán por este filtro
 * el cuál se encarga de pasar el "request" a nuestra clase de utilidad JwtUtil
 * para que valide el token.
 */
public class JwtFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {


        Authentication authentication = JwtUtil.getAuthentication((HttpServletRequest)request);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }
}
```

---

## Diagrama general

El siguiente diagrama muestra de forma resumida lo que tenemos. Para una mejor comprensión ve el video demostrativo para ver el proyecto en funcionamiento.


![Image of Yaktocat](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/jwt_spring.png)  

## Video demostrativo

<iframe width="560" height="315" src="https://www.youtube.com/embed/tofgJwV5HNA?rel=0?ecver=1" frameborder="0" allowfullscreen></iframe>

**No olvides descargar el código fuente y dejar tus comentarios.**

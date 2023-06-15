<p>Авторизация в Rest приложениях происходит через token - ключ по аналогии с sessionId в servlet.</p>
<p>Рассмотрим приложение на Spring boot.</p>
<p>pom.xml</p>
<pre><code class="java hljs"><code class="java">&lt;?xml version=<span class="hljs-string">"1.0"</span> encoding=<span class="hljs-string">"UTF-8"</span>?&gt;<br>&lt;project xmlns=<span class="hljs-string">"http://maven.apache.org/POM/4.0.0"</span><br>         xmlns:xsi=<span class="hljs-string">"http://www.w3.org/2001/XMLSchema-instance"</span><br>         xsi:schemaLocation=<span class="hljs-string">"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"</span>&gt;<br>    &lt;modelVersion&gt;<span class="hljs-number">4.0</span><span class="hljs-number">.0</span>&lt;/modelVersion&gt;<br><br>    &lt;groupId&gt;ru.job4j&lt;/groupId&gt;<br>    &lt;artifactId&gt;url&lt;/artifactId&gt;<br>    &lt;version&gt;<span class="hljs-number">1.0</span>-SNAPSHOT&lt;/version&gt;<br>    &lt;parent&gt;<br>        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;<br>        &lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;<br>        &lt;version&gt;<span class="hljs-number">2.2</span><span class="hljs-number">.2</span>.RELEASE&lt;/version&gt;<br>        &lt;relativePath/&gt; &lt;!-- lookup parent from repository --&gt;<br>    &lt;/parent&gt;<br><br>    &lt;dependencies&gt;<br><br>        &lt;dependency&gt;<br>            &lt;groupId&gt;com.auth0&lt;/groupId&gt;<br>            &lt;artifactId&gt;java-jwt&lt;/artifactId&gt;<br>            &lt;version&gt;<span class="hljs-number">3.4</span><span class="hljs-number">.0</span>&lt;/version&gt;<br>        &lt;/dependency&gt;<br><br><br>        &lt;dependency&gt;<br>            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;<br>            &lt;artifactId&gt;spring-boot-starter-security&lt;/artifactId&gt;<br>        &lt;/dependency&gt;<br><br>        &lt;dependency&gt;<br>            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;<br>            &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;<br>        &lt;/dependency&gt;<br><br>    &lt;/dependencies&gt;<br><br>    &lt;build&gt;<br>        &lt;plugins&gt;<br>            &lt;plugin&gt;<br>                &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;<br>                &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;<br>            &lt;/plugin&gt;<br>        &lt;/plugins&gt;<br>    &lt;/build&gt;<br>&lt;/project&gt;</code></code></pre>
<p>Модель данных - пользователь системы.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> java.util.Objects;<br><br><span class="hljs-keyword">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">Person</span> </span>{<br>    <span class="hljs-keyword">private</span> String username;<br>    <span class="hljs-keyword">private</span> String password;<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> String <span class="hljs-title">getUsername</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> username;<br>    }<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">void</span> <span class="hljs-title">setUsername</span><span class="hljs-params">(String username)</span> </span>{<br>        <span class="hljs-keyword">this</span>.username = username;<br>    }<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> String <span class="hljs-title">getPassword</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> password;<br>    }<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">void</span> <span class="hljs-title">setPassword</span><span class="hljs-params">(String password)</span> </span>{<br>        <span class="hljs-keyword">this</span>.password = password;<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">boolean</span> <span class="hljs-title">equals</span><span class="hljs-params">(Object o)</span> </span>{<br>        <span class="hljs-keyword">if</span> (<span class="hljs-keyword">this</span> == o) <span class="hljs-keyword">return</span> <span class="hljs-keyword">true</span>;<br>        <span class="hljs-keyword">if</span> (o == <span class="hljs-keyword">null</span> || getClass() != o.getClass()) <span class="hljs-keyword">return</span> <span class="hljs-keyword">false</span>;<br>        Person person = (Person) o;<br>        <span class="hljs-keyword">return</span> Objects.equals(username, person.username) &amp;&amp;<br>                Objects.equals(password, person.password);<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">int</span> <span class="hljs-title">hashCode</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> Objects.hash(username, password);<br>    }<br>}</code></pre>
<p>Пользователей будем хранить в памяти. В дальнейшем этот класс можно заменить на базу данных.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> org.springframework.stereotype.Component;<br><br><span class="hljs-keyword">import</span> java.util.ArrayList;<br><span class="hljs-keyword">import</span> java.util.List;<br><span class="hljs-keyword">import</span> java.util.concurrent.ConcurrentHashMap;<br><br><span class="hljs-meta">@Component</span><br><span class="hljs-meta">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">UserStore</span> </span>{<br>    <span class="hljs-keyword">private</span> <span class="hljs-keyword">final</span> ConcurrentHashMap&lt;String, Person&gt; users = <span class="hljs-keyword">new</span> ConcurrentHashMap&lt;&gt;();<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">void</span> <span class="hljs-title">save</span><span class="hljs-params">(Person person)</span> </span>{<br>        users.put(person.getUsername(), person);<br>    }<br><br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> Person <span class="hljs-title">findByUsername</span><span class="hljs-params">(String username)</span> </span>{<br>        <span class="hljs-keyword">return</span> users.get(username);<br>    }<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> List&lt;Person&gt; <span class="hljs-title">findAll</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> ArrayList&lt;&gt;(users.values());<br>    }<br>}</code></pre>
<p>Как и в Spring MVC нужно создать сервис UserDetailsService. Этот сервис будет загружать в SecutiryHolder детали авторизованного пользователя.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> org.springframework.security.core.userdetails.User;<br><span class="hljs-keyword">import</span> org.springframework.security.core.userdetails.UserDetails;<br><span class="hljs-keyword">import</span> org.springframework.security.core.userdetails.UserDetailsService;<br><span class="hljs-keyword">import</span> org.springframework.security.core.userdetails.UsernameNotFoundException;<br><span class="hljs-keyword">import</span> org.springframework.stereotype.Service;<br><br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> java.util.Collections.emptyList;<br><br><span class="hljs-meta">@Service</span><br><span class="hljs-meta">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">UserDetailsServiceImpl</span> <span class="hljs-keyword">implements</span> <span class="hljs-title">UserDetailsService</span> </span>{<br>    <span class="hljs-keyword">private</span> UserStore users;<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-title">UserDetailsServiceImpl</span><span class="hljs-params">(UserStore users)</span> </span>{<br>        <span class="hljs-keyword">this</span>.users = users;<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> UserDetails <span class="hljs-title">loadUserByUsername</span><span class="hljs-params">(String username)</span> <span class="hljs-keyword">throws</span> UsernameNotFoundException </span>{<br>        Person user = users.findByUsername(username);<br>        <span class="hljs-keyword">if</span> (user == <span class="hljs-keyword">null</span>) {<br>            <span class="hljs-keyword">throw</span> <span class="hljs-keyword">new</span> UsernameNotFoundException(username);<br>        }<br>        <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> User(user.getUsername(), user.getPassword(), emptyList());<br>    }<br>}</code></pre>
<p>Сделаем контроллер для регистрации пользователя и получения списках всех пользователей системы.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;<br><span class="hljs-keyword">import</span> org.springframework.web.bind.annotation.*;<br><br><span class="hljs-keyword">import</span> java.util.List;<br><br><span class="hljs-meta">@RestController</span><br><span class="hljs-meta">@RequestMapping</span>(<span class="hljs-string">"/users"</span>)<br><span class="hljs-keyword">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">UserController</span> </span>{<br><br>    <span class="hljs-keyword">private</span> UserStore users;<br>    <span class="hljs-keyword">private</span> BCryptPasswordEncoder encoder;<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-title">UserController</span><span class="hljs-params">(UserStore users,</span></span><br><span class="hljs-function"><span class="hljs-params">                          BCryptPasswordEncoder encoder)</span> </span>{<br>        <span class="hljs-keyword">this</span>.users = users;<br>        <span class="hljs-keyword">this</span>.encoder = encoder;<br>    }<br><br>    <span class="hljs-meta">@PostMapping</span>(<span class="hljs-string">"/sign-up"</span>)<br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">void</span> <span class="hljs-title">signUp</span><span class="hljs-params">(@RequestBody Person person)</span> </span>{<br>        person.setPassword(encoder.encode(person.getPassword()));<br>        users.save(person);<br>    }<br><br>    <span class="hljs-meta">@GetMapping</span>(<span class="hljs-string">"/all"</span>)<br>    <span class="hljs-function"><span class="hljs-keyword">public</span> List&lt;Person&gt; <span class="hljs-title">findAll</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> users.findAll();<br>    }<br>}</code></pre>
<p>Пароли хешируются и прямом виде не хранятся в базе.</p>
<p><br><strong>JWT - Java web token.</strong></p>
<p>По аналогии с servlet создаем фильтр, который отлавливает пользователя.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> com.auth0.jwt.JWT;<br><span class="hljs-keyword">import</span> com.fasterxml.jackson.databind.ObjectMapper;<br><span class="hljs-keyword">import</span> org.springframework.security.authentication.AuthenticationManager;<br><span class="hljs-keyword">import</span> org.springframework.security.authentication.UsernamePasswordAuthenticationToken;<br><span class="hljs-keyword">import</span> org.springframework.security.core.Authentication;<br><span class="hljs-keyword">import</span> org.springframework.security.core.AuthenticationException;<br><span class="hljs-keyword">import</span> org.springframework.security.core.userdetails.User;<br><span class="hljs-keyword">import</span> org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;<br><br><span class="hljs-keyword">import</span> javax.servlet.FilterChain;<br><span class="hljs-keyword">import</span> javax.servlet.ServletException;<br><span class="hljs-keyword">import</span> javax.servlet.http.HttpServletRequest;<br><span class="hljs-keyword">import</span> javax.servlet.http.HttpServletResponse;<br><span class="hljs-keyword">import</span> java.io.IOException;<br><span class="hljs-keyword">import</span> java.util.ArrayList;<br><span class="hljs-keyword">import</span> java.util.Date;<br><br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> com.auth0.jwt.algorithms.Algorithm.HMAC512;<br><br><span class="hljs-keyword">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">JWTAuthenticationFilter</span> <span class="hljs-keyword">extends</span> <span class="hljs-title">UsernamePasswordAuthenticationFilter</span> </span>{<br><br>    <span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">final</span> String SECRET = <span class="hljs-string">"SecretKeyToGenJWTs"</span>;<br>    <span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">final</span> <span class="hljs-keyword">long</span> EXPIRATION_TIME = <span class="hljs-number">864_000_000</span>; <span class="hljs-comment">/* 10 days */</span><br>    <span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">final</span> String TOKEN_PREFIX = <span class="hljs-string">"Bearer "</span>;<br>    <span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">final</span> String HEADER_STRING = <span class="hljs-string">"Authorization"</span>;<br>    <span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">final</span> String SIGN_UP_URL = <span class="hljs-string">"/users/sign-up"</span>;<br><br>    <span class="hljs-keyword">private</span> AuthenticationManager auth;<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-title">JWTAuthenticationFilter</span><span class="hljs-params">(AuthenticationManager auth)</span> </span>{<br>        <span class="hljs-keyword">this</span>.auth = auth;<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> Authentication <span class="hljs-title">attemptAuthentication</span><span class="hljs-params">(HttpServletRequest req, HttpServletResponse res)</span></span><br><span class="hljs-function">            <span class="hljs-keyword">throws</span> AuthenticationException </span>{<br>        <span class="hljs-keyword">try</span> {<br>            Person creds = <span class="hljs-keyword">new</span> ObjectMapper()<br>                    .readValue(req.getInputStream(), Person<span class="hljs-class">.<span class="hljs-keyword">class</span>)</span>;<br><br>            <span class="hljs-keyword">return</span> auth.authenticate(<br>                    <span class="hljs-keyword">new</span> UsernamePasswordAuthenticationToken(<br>                            creds.getUsername(),<br>                            creds.getPassword(),<br>                            <span class="hljs-keyword">new</span> ArrayList&lt;&gt;())<br>            );<br>        } <span class="hljs-keyword">catch</span> (IOException e) {<br>            <span class="hljs-keyword">throw</span> <span class="hljs-keyword">new</span> RuntimeException(e);<br>        }<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">protected</span> <span class="hljs-keyword">void</span> <span class="hljs-title">successfulAuthentication</span><span class="hljs-params">(HttpServletRequest req,</span></span><br><span class="hljs-function"><span class="hljs-params">                                            HttpServletResponse res,</span></span><br><span class="hljs-function"><span class="hljs-params">                                            FilterChain chain,</span></span><br><span class="hljs-function"><span class="hljs-params">                                            Authentication auth)</span> <span class="hljs-keyword">throws</span> IOException, ServletException </span>{<br><br>        String token = JWT.create()<br>                .withSubject(((User) auth.getPrincipal()).getUsername())<br>                .withExpiresAt(<span class="hljs-keyword">new</span> Date(System.currentTimeMillis() + EXPIRATION_TIME))<br>                .sign(HMAC512(SECRET.getBytes()));<br>        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);<br>    }<br>}</code></pre>
<p>Метод attempt проверяет, что логин и пароль верные.</p>
<p>Метод success генерирует token.</p>
<p>Второй фильтр будет проверять, что в заголовке запроса есть token и если его нет, то отправлять статус 403.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> com.auth0.jwt.JWT;<br><span class="hljs-keyword">import</span> com.auth0.jwt.algorithms.Algorithm;<br><span class="hljs-keyword">import</span> org.springframework.security.authentication.AuthenticationManager;<br><span class="hljs-keyword">import</span> org.springframework.security.authentication.UsernamePasswordAuthenticationToken;<br><span class="hljs-keyword">import</span> org.springframework.security.core.context.SecurityContextHolder;<br><span class="hljs-keyword">import</span> org.springframework.security.web.authentication.www.BasicAuthenticationFilter;<br><br><span class="hljs-keyword">import</span> javax.servlet.FilterChain;<br><span class="hljs-keyword">import</span> javax.servlet.ServletException;<br><span class="hljs-keyword">import</span> javax.servlet.http.HttpServletRequest;<br><span class="hljs-keyword">import</span> javax.servlet.http.HttpServletResponse;<br><span class="hljs-keyword">import</span> java.io.IOException;<br><span class="hljs-keyword">import</span> java.util.ArrayList;<br><br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> ru.job4j.url.JWTAuthenticationFilter.HEADER_STRING;<br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> ru.job4j.url.JWTAuthenticationFilter.SECRET;<br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> ru.job4j.url.JWTAuthenticationFilter.TOKEN_PREFIX;<br><br><span class="hljs-keyword">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">JWTAuthorizationFilter</span> <span class="hljs-keyword">extends</span> <span class="hljs-title">BasicAuthenticationFilter</span> </span>{<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-title">JWTAuthorizationFilter</span><span class="hljs-params">(AuthenticationManager authManager)</span> </span>{<br>        <span class="hljs-keyword">super</span>(authManager);<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">protected</span> <span class="hljs-keyword">void</span> <span class="hljs-title">doFilterInternal</span><span class="hljs-params">(HttpServletRequest req,</span></span><br><span class="hljs-function"><span class="hljs-params">                                    HttpServletResponse res,</span></span><br><span class="hljs-function"><span class="hljs-params">                                    FilterChain chain)</span> <span class="hljs-keyword">throws</span> IOException, ServletException </span>{<br>        String header = req.getHeader(HEADER_STRING);<br><br>        <span class="hljs-keyword">if</span> (header == <span class="hljs-keyword">null</span> || !header.startsWith(TOKEN_PREFIX)) {<br>            chain.doFilter(req, res);<br>            <span class="hljs-keyword">return</span>;<br>        }<br><br>        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);<br><br>        SecurityContextHolder.getContext().setAuthentication(authentication);<br>        chain.doFilter(req, res);<br>    }<br><br>    <span class="hljs-function"><span class="hljs-keyword">private</span> UsernamePasswordAuthenticationToken <span class="hljs-title">getAuthentication</span><span class="hljs-params">(HttpServletRequest request)</span> </span>{<br>        String token = request.getHeader(HEADER_STRING);<br>        <span class="hljs-keyword">if</span> (token != <span class="hljs-keyword">null</span>) {<br>            <span class="hljs-comment">/* parse the token. */</span><br>            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))<br>                    .build()<br>                    .verify(token.replace(TOKEN_PREFIX, <span class="hljs-string">""</span>))<br>                    .getSubject();<br><br>            <span class="hljs-keyword">if</span> (user != <span class="hljs-keyword">null</span>) {<br>                <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> UsernamePasswordAuthenticationToken(user, <span class="hljs-keyword">null</span>, <span class="hljs-keyword">new</span> ArrayList&lt;&gt;());<br>            }<br>            <span class="hljs-keyword">return</span> <span class="hljs-keyword">null</span>;<br>        }<br>        <span class="hljs-keyword">return</span> <span class="hljs-keyword">null</span>;<br>    }<br>}</code></pre>
<p>Осталось сконфигурировать Web.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> org.springframework.http.HttpMethod;<br><span class="hljs-keyword">import</span> org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;<br><span class="hljs-keyword">import</span> org.springframework.security.config.annotation.web.builders.HttpSecurity;<br><span class="hljs-keyword">import</span> org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;<br><span class="hljs-keyword">import</span> org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;<br><span class="hljs-keyword">import</span> org.springframework.security.config.http.SessionCreationPolicy;<br><span class="hljs-keyword">import</span> org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;<br><span class="hljs-keyword">import</span> org.springframework.web.cors.CorsConfiguration;<br><span class="hljs-keyword">import</span> org.springframework.web.cors.CorsConfigurationSource;<br><span class="hljs-keyword">import</span> org.springframework.web.cors.UrlBasedCorsConfigurationSource;<br><span class="hljs-keyword">import</span> org.springframework.context.annotation.Bean;<br><br><span class="hljs-keyword">import</span> <span class="hljs-keyword">static</span> ru.job4j.url.JWTAuthenticationFilter.SIGN_UP_URL;<br><br><br><span class="hljs-meta">@EnableWebSecurity</span><br><span class="hljs-meta">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">WebSecurity</span> <span class="hljs-keyword">extends</span> <span class="hljs-title">WebSecurityConfigurerAdapter</span> </span>{<br>    <span class="hljs-keyword">private</span> UserDetailsServiceImpl userDetailsService;<br>    <span class="hljs-keyword">private</span> BCryptPasswordEncoder bCryptPasswordEncoder;<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-title">WebSecurity</span><span class="hljs-params">(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder)</span> </span>{<br>        <span class="hljs-keyword">this</span>.userDetailsService = userDetailsService;<br>        <span class="hljs-keyword">this</span>.bCryptPasswordEncoder = bCryptPasswordEncoder;<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">protected</span> <span class="hljs-keyword">void</span> <span class="hljs-title">configure</span><span class="hljs-params">(HttpSecurity http)</span> <span class="hljs-keyword">throws</span> Exception </span>{<br>        http.cors().and().csrf().disable().authorizeRequests()<br>                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()<br>                .anyRequest().authenticated()<br>                .and()<br>                .addFilter(<span class="hljs-keyword">new</span> JWTAuthenticationFilter(authenticationManager()))<br>                .addFilter(<span class="hljs-keyword">new</span> JWTAuthorizationFilter(authenticationManager()))<br>                <span class="hljs-comment">/* this disables session creation on Spring Security */</span><br>                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);<br>    }<br><br>    <span class="hljs-meta">@Override</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">void</span> <span class="hljs-title">configure</span><span class="hljs-params">(AuthenticationManagerBuilder auth)</span> <span class="hljs-keyword">throws</span> Exception </span>{<br>        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);<br>    }<br><br>    <span class="hljs-meta">@Bean</span><br>    <span class="hljs-function">CorsConfigurationSource <span class="hljs-title">corsConfigurationSource</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">final</span> UrlBasedCorsConfigurationSource source = <span class="hljs-keyword">new</span> UrlBasedCorsConfigurationSource();<br>        source.registerCorsConfiguration(<span class="hljs-string">"/**"</span>, <span class="hljs-keyword">new</span> CorsConfiguration().applyPermitDefaultValues());<br>        <span class="hljs-keyword">return</span> source;<br>    }<br>}</code></pre>
<p>Переходим к main.</p>
<pre><code class="java hljs"><span class="hljs-keyword">package</span> ru.job4j.url;<br><br><span class="hljs-keyword">import</span> org.springframework.boot.SpringApplication;<br><span class="hljs-keyword">import</span> org.springframework.boot.autoconfigure.SpringBootApplication;<br><span class="hljs-keyword">import</span> org.springframework.context.annotation.Bean;<br><span class="hljs-keyword">import</span> org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;<br><br><span class="hljs-meta">@SpringBootApplication</span><br><span class="hljs-meta">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">UrlApp</span> </span>{<br><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">void</span> <span class="hljs-title">main</span><span class="hljs-params">(String[] args)</span> </span>{<br>        SpringApplication.run(UrlApp<span class="hljs-class">.<span class="hljs-keyword">class</span>, <span class="hljs-title">args</span>)</span>;<br>    }<br><br>    <span class="hljs-meta">@Bean</span><br>    <span class="hljs-function"><span class="hljs-keyword">public</span> BCryptPasswordEncoder <span class="hljs-title">passwordEncoder</span><span class="hljs-params">()</span> </span>{<br>        <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> BCryptPasswordEncoder();<br>    }<br>}</code></pre>
<p>&nbsp;</p>
<p><strong>Проверка работы через curl.</strong></p>
<p>Запустим приложение. В разделе IO мы установили программу curl. Ее будем использовать для проверки работы нашего сервера.</p>
<p><a href="http://job4j.ru/api/images/imageTaskSource?imageId=1189" data-lightbox="images"><img src="http://job4j.ru/api/images/imageTaskPreview?imageId=1189" alt="" vspace="0" hspace="0"></a></p>
<p>&nbsp;Проверим, что безопасность работает. Попробуем получить пользователей без авторизации.</p>
<pre><code class="java hljs">curl <a href="http://localhost:8080/all">http:<span class="hljs-comment">//localhost:8080/all</span></a></code></pre>
<p>Результат 403.</p>
<p><a href="http://job4j.ru/api/images/imageTaskSource?imageId=1190" data-lightbox="images"><img src="http://job4j.ru/api/images/imageTaskPreview?imageId=1190" alt="" vspace="0" hspace="0"></a></p>
<p>Зарегистрируем пользователя.</p>
<pre><code class="java hljs">curl -H <span class="hljs-string">"Content-Type: application/json"</span> -X POST -d <span class="hljs-string">'{</span><br><span class="hljs-string">    "username": "admin",</span><br><span class="hljs-string">    "password": "password"</span><br><span class="hljs-string">}'</span> <a href="http://localhost:8080/users/sign-up">http:<span class="hljs-comment">//localhost:8080/users/sign-up</span></a></code></pre>
<p>Получим у этого пользователя token.</p>
<pre><code class="java hljs">curl -i -H <span class="hljs-string">"Content-Type: application/json"</span> -X POST -d <span class="hljs-string">'{</span><br><span class="hljs-string">    "username": "admin",</span><br><span class="hljs-string">    "password": "password"</span><br><span class="hljs-string">}'</span> <a href="http://localhost:8080/login">http:<span class="hljs-comment">//localhost:8080/login</span></a></code></pre>
<p><a href="http://job4j.ru/api/images/imageTaskSource?imageId=1191" data-lightbox="images"><img src="http://job4j.ru/api/images/imageTaskPreview?imageId=1191" alt="" vspace="0" hspace="0"></a></p>
<p>&nbsp;Получим всех пользователей с этим token.</p>
<pre><code class="java hljs">curl -H <span class="hljs-string">"Authorization: Bearer xxx.yyy.zzz"</span> <a href="http://localhost:8080/users/all">http:<span class="hljs-comment">//localhost:8080/users/all</span></a></code></pre>
<p><a href="http://job4j.ru/api/images/imageTaskSource?imageId=1192" data-lightbox="images"><img src="http://job4j.ru/api/images/imageTaskPreview?imageId=1192" alt="" vspace="0" hspace="0"></a><br><br></p>
<p><strong>Задание.</strong>&nbsp;</p>
<p>1. Добавьте авторизацию и аутентификацию в сервис чата.</p>
<p>2. Загрузите код в репозиторий. Оставьте ссылку на коммит.</p>
<p>3. Переведите ответственного на Петра Арсентьева.</p></p></div></div></div><!---->


### Контакты

> email: [haoos@inbox.ru](mailto:haoos@inbox.ru) <br>
> tl: [Dima_software](https://t.me/Dima_software) <br>
> github.com: [Dima-Stepanov](https://github.com/Dima-Stepanov)

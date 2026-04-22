package by.cats.security;

import by.cats.dto.response.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF, так как для REST и разработки на разных портах это упростит жизнь
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Включаем CORS (настройка ниже)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Настройка прав доступа
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll() // Доступ всем
                        .anyRequest().authenticated() // Всё остальное только по паролю (нику)
                )

                // 4. Настройка процесса логина
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login") // URL, куда друг будет слать POST запрос
                        .usernameParameter("nickname")
                        .passwordParameter("password")

                        // ОБРАБОТЧИК УСПЕХА (твой код)
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_OK);

                            ApiResponseDto successResponse = new ApiResponseDto(true, "Вход выполнен", authentication.getName());
                            response.getWriter().print(objectMapper.writeValueAsString(successResponse));
                        })

                        // ОБРАБОТЧИК ОШИБКИ (важно для фронтенда!)
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            ApiResponseDto errorResponse = new ApiResponseDto(false, "Неверный никнейм или пользователь не существует", null);
                            response.getWriter().print(objectMapper.writeValueAsString(errorResponse));
                        })
                )

                // 5. Настройка логаута
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().print("{\"success\": true, \"message\": \"Выход выполнен\"}");
                        })
                );

        return http.build();
    }

    // Настройка CORS, чтобы друг мог достучаться до твоего компа
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Важно для передачи сессий (Cookies)
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000")); // Адреса фронтенда
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Разрешаем регистрацию и статику
//                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
//                        // Всё остальное только после входа
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")             // Наш кастомный URL
//                        .loginProcessingUrl("/login")    // URL для обработки POST-запроса (встроен в Spring)
//                        .usernameParameter("nickname")   // Поле из формы
//                        .passwordParameter("password")   // Скрытое поле из формы
//                        .defaultSuccessUrl("/game", true)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//
//    // Пример в SecurityConfig
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // адрес фронтенда
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//}


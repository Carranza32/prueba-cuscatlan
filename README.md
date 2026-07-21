# Microservicio de Reservas de Coworking

## 1. Descripción General

Este proyecto es un microservicio para la gestión de reservas de espacios de coworking, desarrollado con Spring Boot. La API REST permite a los usuarios gestionar espacios, usuarios y reservas, siguiendo los principios de una arquitectura limpia, resiliente y lista para producción.

El objetivo principal es demostrar el dominio técnico de las herramientas del ecosistema Spring, aplicando patrones de diseño y buenas prácticas de desarrollo de software para crear una base de código sólida y mantenible.

## 2. Instrucciones de Ejecución

La solución está completamente dockerizada para garantizar un entorno de ejecución consistente y facilitar la evaluación.

**Requisitos Previos:**
*   Docker
*   Docker Compose

**Pasos para levantar el entorno:**

1.  Clona este repositorio en tu máquina local.
2.  Abre una terminal en el directorio raíz del proyecto.
3.  Ejecuta el siguiente comando:
    ```bash
    docker-compose up --build
    ```
    Este comando construirá la imagen de la aplicación Spring Boot, levantará un contenedor para la base de datos PostgreSQL y finalmente iniciará la aplicación.

4.  La API estará disponible en `http://localhost:8080`.
5.  La base de datos PostgreSQL estará accesible en el puerto `5432` (para inspección externa si es necesario).

## 3. Documentación y Pruebas de la API

### Documentación Interactiva (Swagger)

Una vez que la aplicación está en ejecución, la documentación completa de la API, autogenerada con OpenAPI, está disponible en la siguiente URL. Desde aquí se pueden explorar y probar todos los endpoints de forma interactiva.

*   **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Mock de Servicio de Pago

El sistema incluye un endpoint mock para simular el servicio externo de validación de pagos, permitiendo probar el comportamiento del Circuit Breaker.

*   **Endpoint Mock:** `GET /mock/payment/validate`

## 4. Decisiones de Diseño y Justificación Técnica

A continuación se justifican las decisiones de arquitectura y los patrones implementados, en cumplimiento con los requisitos de la evaluación.

### Arquitectura en Capas
Se adoptó una arquitectura en capas clásica para separar responsabilidades y mejorar la mantenibilidad:
*   `controller`: Expone la API REST y maneja las peticiones HTTP.
*   `service`: Orquesta la lógica de negocio principal.
*   `repository`: Abstrae el acceso a datos usando Spring Data JPA.
*   `domain`: Contiene las entidades del negocio.
*   `dto` y `mapper`: Se utilizan DTOs para desacoplar la API de las entidades del dominio, evitando exponer la estructura interna de la base de datos.

### Patrón de Diseño: State
*   **Problema:** El ciclo de vida de una `Reserva` (`PENDING`, `CONFIRMED`, `CANCELLED`, etc.) implica transiciones complejas y reglas de negocio específicas para cada estado. Gestionar esto con sentencias `if/else` o `switch` en la clase `ReservationService` resultaría en un código frágil y difícil de extender.
*   **Solución:** Se implementó el **patrón State**.
    *   **Implementación:** El paquete `com.cuscatlan.coworking.domain.state` contiene una interfaz `ReservationState` y clases concretas para cada estado (`PendingState`, `ConfirmedState`, etc.).
    *   **Ventaja:** Cada clase de estado encapsula la lógica y las transiciones válidas para ese estado en particular. El `ReservationService` delega las operaciones al objeto de estado actual de la reserva, lo que resulta en un código más limpio, cohesivo y adherido al Principio Abierto/Cerrado.

### Resiliencia: Circuit Breaker (Resilience4j)
*   **Problema:** La validación de pagos depende de un servicio externo que puede ser lento o inestable. Una falla en este servicio no debe propagarse y causar una mala experiencia de usuario o el fallo de la reserva.
*   **Solución:** Se implementó un **Circuit Breaker** con Resilience4j sobre el `PaymentGatewayClient`.
    *   **Implementación:** La configuración se encuentra en `application.yaml`, definiendo umbrales de fallo y tiempos de espera. El `PaymentGatewayClient` utiliza la anotación `@CircuitBreaker`.
    *   **Fallback:** Cuando el circuito se abre, se ejecuta un método `fallback` que, en lugar de fallar, cambia el estado de la reserva a `PENDING_PAYMENT`. Esto asegura que la reserva no se pierda y pueda ser procesada más tarde.
    *   **Monitorización:** El estado de los Circuit Breakers se expone a través de Spring Boot Actuator en el endpoint `/actuator/circuitbreakers`.

### Seguridad: Spring Security y JWT
*   **Problema:** Proteger la API y permitir operaciones diferenciadas según el rol del usuario (USER vs. ADMIN).
*   **Solución:** Se implementó un sistema de autenticación y autorización basado en JSON Web Tokens (JWT).
    *   **Implementación:** El `AuthController` gestiona el registro y login. El `JwtService` genera y valida los tokens. El `JwtAuthenticationFilter` intercepta cada petición para establecer el contexto de seguridad. La `SecurityConfig` define las rutas públicas y protegidas.
    *   **Autorización:** Se utiliza la autorización a nivel de método en los servicios para controlar el acceso, por ejemplo, asegurando que un `USER` solo pueda cancelar sus propias reservas, mientras que un `ADMIN` puede gestionar todas.

### Asincronismo: Eventos de Dominio de Spring
*   **Problema:** Operaciones como el envío de un correo de confirmación no deben bloquear la respuesta HTTP principal para no degradar la experiencia del usuario.
*   **Solución:** Se utiliza el sistema de eventos de Spring para el procesamiento asíncrono.
    *   **Implementación:** Cuando una reserva se confirma, `ReservationService` publica un `ReservationConfirmedEvent`. La clase `ReservationNotificationListener` escucha este evento (`@EventListener`) y procesa la notificación en un hilo separado (`@Async`).
    *   **Ventaja:** Esto desacopla el servicio de notificaciones de la lógica de negocio principal y garantiza una respuesta rápida de la API.

### Otros Requisitos Técnicos
*   **Caché (`@Cacheable`):** Se utiliza en `ReportService` para cachear los resultados del costoso reporte de ocupación, mejorando significativamente el rendimiento en consultas repetidas.
*   **Manejo de Errores (`@ControllerAdvice`):** La clase `GlobalExceptionHandler` centraliza el manejo de excepciones, traduciendo excepciones de negocio personalizadas (ej. `OverlappingReservationException`) en respuestas HTTP claras y con el código de estado apropiado (ej. `409 Conflict`).
*   **Configuración (`@ConfigurationProperties`):** Se utilizan clases como `JwtProperties` para agrupar y validar la configuración de la aplicación de forma centralizada, en lugar de dispersar anotaciones `@Value`.

## 5. Alcance y Posibles Mejoras

Dado el límite de tiempo, se priorizó la construcción de un núcleo de backend robusto y bien diseñado que cumpliera con los requisitos técnicos más críticos.

**Fuera de Alcance:**
*   **Pruebas de Integración Exhaustivas:** Aunque el proyecto está configurado para soportar pruebas de integración con Testcontainers, no se alcanzó a desarrollar una suite completa. El foco se puso en las pruebas unitarias de la lógica de negocio clave (`ReservationServiceTest`).
*   **Interfaz de Usuario (Frontend):** No se desarrolló un frontend, ya que no era parte de los requisitos.

**Posibles Mejoras con más tiempo:**
*   **Ampliar la Cobertura de Tests:** Añadir más pruebas de integración para los flujos de `Auth` y `Spaces`, y tests de contrato para el servicio de pago.
*   **Sistema de Notificaciones Real:** Reemplazar el log actual por una integración real con un servicio de email (ej. SendGrid) y una cola de mensajes (ej. RabbitMQ) para mayor resiliencia.
*   **Optimización de Consultas:** Analizar y optimizar consultas complejas de la base de datos, especialmente para los reportes.

## 6. Entregables
1.  **Repositorio Git:** El historial de commits refleja el proceso de desarrollo.
2.  **Código Fuente:** La aplicación Spring Boot completa.
3.  **Dockerfile y docker-compose.yml:** Para la ejecución del entorno.
4.  **README.md:** Este mismo archivo.
5.  **Colección Postman/HTTP:** Se adjuntaría un archivo `api-examples.http` para probar los endpoints fácilmente desde un cliente HTTP.

# ToDoList Application

## Overview
This README contains a short technical summary of the work done in this project for the ToDoList application. It describes the main changes added during the development process, including new pages, controllers, templates, tests, and other relevant implementation details.

## Setup & Execution

System requirements:
- Java 8 SDK
- Maven 3.6+

Run the app using:
```
$ mvn spring-boot:run
```

Once running, open your browser and connect to:
- [http://localhost:8080/login](http://localhost:8080/login)

## New Classes Implemented

### `GlobalModelAttributes.java`
**Location:** `src/main/java/todolist/controller/GlobalModelAttributes.java`

This `@ControllerAdvice` component provides global model attributes to all Thymeleaf templates. It intercepts every request and injects:
- `usuarioLogeado`: Boolean indicating if a user is logged in
- `usuarioName`: The name of the logged-in user (fallback to email if name is null)
- `usuarioId`: The ID of the logged-in user

This eliminates the need to add these attributes manually in every controller method and ensures consistent navbar behavior across all pages.

### HomeController - Extended Methods
**Location:** `src/main/java/todolist/controller/HomeController.java`

Two new methods were added to the existing `HomeController`:
- `showRegisteredUsers()`: Maps `/registered` endpoint to display all registered users
- `showRegisteredUserDescription()`: Maps `/registered/{id}` endpoint to display individual user details

The `UsuarioService.findAll()` method was also added to support retrieving all registered users from the database.

## New Thymeleaf Templates

### `fragments.html` - Navbar Fragment
A Bootstrap 5 navbar fragment was added with:
- Dynamic conditional rendering based on login status
- Left-side brand link (`ToDoList`) pointing to `/about`
- Left-side `Tasks` link (visible only when logged in)
- Right-side user dropdown with `Account` and `Log out` options (when logged in)
- Right-side `Login` and `Register` links (visible when not logged in)

### `registeredUsers.html`
Displays a list of all registered users with:
- User ID column
- Email column (clickable link to user description page)
- Bootstrap striped table styling
- Integration with the shared navbar

### `registeredUserDescription.html`
Shows comprehensive user information:
- User ID
- Email address
- Full name
- Birth date
- Explicitly excludes password from display
- "Back to users" navigation link

## Updated Templates

The following existing templates were updated to include the navbar fragment:
- `about.html`
- `listaTareas.html`
- `formNuevaTarea.html`
- `formEditarTarea.html`

The `formLogin.html` and `formRegistro.html` pages intentionally do NOT include the navbar, aligning with the requirement that authentication pages have a different layout.

Additionally, all templates were updated to translate UI text from Spanish to English:
- Button labels, form fields, and navigation text
- Page titles and headings
- Error messages and validation text

## Code Examples

### HomeController New Methods

```java
@GetMapping("/registered")
public String showRegisteredUsers(Model model) {
    List<UsuarioData> usuarios = usuarioService.findAll();
    model.addAttribute("usuarios", usuarios);
    return "registeredUsers";
}

@GetMapping("/registered/{id}")
public String showRegisteredUserDescription(@PathVariable Long id, Model model) {
    UsuarioData usuario = usuarioService.findById(id);
    model.addAttribute("user", usuario);
    return "registeredUserDescription";
}
```

These methods create the new web pages for showing the list of users and details about one user. The first method gets all users and sends them to the registeredUsers page. The second method gets one user by their ID and shows their information.

### UsuarioService Extension

```java
@Transactional(readOnly = true)
public List<UsuarioData> findAll() {
    List<UsuarioData> usuarios = new ArrayList<>();
    usuarioRepository.findAll().forEach(usuario ->
            usuarios.add(modelMapper.map(usuario, UsuarioData.class)));
    return usuarios;
}
```

This method adds a new feature to UsuarioService to get all registered users from the database. It uses the repository to get all user records, changes them to a simpler format with ModelMapper, and returns the list to show on the user list page.

### Navbar Fragment in fragments.html

```html
<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#" th:href="@{/about}">ToDoList</a>
        <div class="navbar-nav me-auto">
            <a th:if="${usuarioLogeado}" class="nav-link" href="#" th:href="@{/tareas}">Tasks</a>
        </div>
        <div class="navbar-nav">
            <div th:if="${usuarioLogeado}" class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                    <span th:text="${usuarioName}">User</span>
                </a>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="#">Account</a></li>
                    <li><a class="dropdown-item" href="#" th:href="@{/logout}">Log out</a></li>
                </ul>
            </div>
            <div th:if="${!usuarioLogeado}">
                <a class="nav-link" href="#" th:href="@{/login}">Login</a>
                <a class="nav-link" href="#" th:href="@{/registro}">Register</a>
            </div>
        </div>
    </div>
</nav>
```

This part of the HTML template creates the navigation bar that can be used on different pages. It shows different things depending on if the user is logged in or not - a menu with user options when logged in, or login and register links when not logged in.

### User Detail Template Snippet

```html
<table class="table table-striped">
    <tr>
        <th>ID</th>
        <td th:text="${user.id}"></td>
    </tr>
    <tr>
        <th>Email</th>
        <td th:text="${user.email}"></td>
    </tr>
    <tr>
        <th>Name</th>
        <td th:text="${user.nombre}"></td>
    </tr>
    <tr>
        <th>Birth Date</th>
        <td th:text="${user.fechaNacimiento}"></td>
    </tr>
</table>
```

This part of the HTML from the user detail page shows user information in a table. It displays the user's ID, email, name, and birth date, but does not show the password for security.

## Tests Implemented

### NavbarWebTest.java
Tests the navigation bar behavior:
- **`aboutPageShowsLoginAndRegisterWhenUserIsNotLoggedIn`**: Verifies that unauthenticated users see Login and Register links
- **`tasksPageShowsNavbarDropdownWhenUserIsLoggedIn`**: Confirms the user dropdown appears when logged in

### RegisteredUsersWebTest.java
Integration tests for the user listing and detail pages:
- **`registeredPageShowsAllRegisteredUsersAndLinksToDescription`**: Validates that `/registered` displays all users with clickable email links
- **`registeredUserDescriptionShowsDataWithoutPassword`**: Ensures `/registered/{id}` shows user data while explicitly hiding the password field

### RegisteredUsersServiceTest.java
Unit tests at the service layer:
- **`findAllReturnsAllRegisteredUsers`**: Tests `UsuarioService.findAll()` returns correct user list
- **`findByIdReturnsRegisteredUserDetails`**: Verifies `findById()` retrieves complete user information

## Key Implementation Detail: Global Model Attributes

The `GlobalModelAttributes` class is the cornerstone of the navbar integration. Here's how it works:

```java
@ControllerAdvice
public class GlobalModelAttributes {
    @Autowired
    ManagerUserSession managerUserSession;
    
    @Autowired
    UsuarioService usuarioService;
    
    @ModelAttribute
    public void addUsuarioModelAttributes(Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        if (idUsuario != null) {
            UsuarioData usuario = usuarioService.findById(idUsuario);
            if (usuario != null) {
                String usuarioNombre = usuario.getNombre();
                if (usuarioNombre == null || usuarioNombre.isBlank()) {
                    usuarioNombre = usuario.getEmail();
                }
                model.addAttribute("usuarioLogeado", true);
                model.addAttribute("usuarioName", usuarioNombre);
                model.addAttribute("usuarioId", idUsuario);
                return;
            }
        }
        model.addAttribute("usuarioLogeado", false);
    }
}
```

The `@ControllerAdvice` annotation makes this a global interceptor, and the `@ModelAttribute` method is invoked before every controller method. This approach reduces code duplication and ensures every Thymeleaf template has consistent context data for the navbar.

## Database and Service Changes

The `UsuarioService` class was enhanced with a new method:

```java
@Transactional(readOnly = true)
public List<UsuarioData> findAll() {
    List<UsuarioData> usuarios = new ArrayList<>();
    usuarioRepository.findAll().forEach(usuario ->
            usuarios.add(modelMapper.map(usuario, UsuarioData.class)));
    return usuarios;
}
```

This method retrieves all `Usuario` entities from the database and converts them to DTOs for template rendering.

## Architecture Notes

- **Separation of Concerns**: The navbar logic is centralized in `GlobalModelAttributes`, keeping controllers lean
- **Template Reusability**: The navbar fragment is included via `th:replace`, avoiding duplication
- **Conditional Rendering**: Thymeleaf's `th:if` directives handle different UI states based on authentication
- **Security**: The password field is intentionally omitted from the user detail page at the template layer

## Repository and Deployment

- **GitHub Repository**: [Agile_sfotware_development_ex2](https://github.com/hanasl1/Agile_sfotware_development_ex2.git)
- **DockerHub Image p2-todolistapp:1.1.0**: [DockerHub Image p2-todolistapp:1.1.0](https://hub.docker.com/layers/hanasl1/p2-todolistapp/1.1.0/images/sha256:54de92fd8e6e6ddc83221fafb77032d24f00ecc83aece974e02b49f4fed1bd92?uuid=F5064730-D246-44FB-A228-F395F84EE7E)
- **Trello Board**: [E2 To-Do List App](https://trello.com/invite/b/69c2b83d8dd5ae72abec2eb8/ATTIe6379c3ca03c0b0d6c67bd3bb7452548747D8F1E/e2-to-do-list-app)



## Proyecto Final Spring Reactivo
El proyecto cumple con los siguientes requisitos:

- [x] Operaciones CRUD para Estudiantes, Cursos y Matrículas.
- [x] Carga asíncrona de objetos anidados (al no haber soporte para DBRef).
- [ ] Listado de Estudiantes ordenados en forma descendente por edad.
- [x] Operativo con MongoDB 4.2.
- [x] Implementado mediante Controladores REST y Endpoints Funcionales.
- [x] Validación de objetos ingresados.
- [x] Control global de excepciones (considerando casos específicos como errores de validación y autorización).
- [x] Los servicios solo pueden ser consumidos por usuarios autenticados con tokens vigentes.

### Características:

- Los controladores están disponibles en la ruta `/anot/*`.
- Los endpoints funcionales están disponibles en la ruta `/func/*`.
- El inicio de sesión fue implementado como un endpoint funcional en la ruta `/login`.

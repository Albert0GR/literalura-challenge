package com.agrsystems.literalura.principal;


import com.agrsystems.literalura.model.*;
import com.agrsystems.literalura.repository.AutorRepository;
import com.agrsystems.literalura.repository.LibroRepository;
import com.agrsystems.literalura.service.ConsumoAPI;
import com.agrsystems.literalura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    private List<Libro> libros;

    private final Scanner teclado = new Scanner(System.in);
    private final ConvierteDatos conversor = new ConvierteDatos();

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }


    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                                       
                    1 - Buscar libros por título
                    2 - Listar libros registrados
                    3-  Listar autores registrados
                    4- Listar autores vivos en un año determinado
                    5- listar libros por idioma
                    6 - top 10 libros
                    7 - generar estadisticas
                    8 - buscar autor por nombre de la BD
                                     
                                        
                    0 - Salir
                    """;
            System.out.println(menu);
            while (!teclado.hasNextInt()) {
                System.out.println("Ingrese una opcion valida");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;

                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivosAnioDeterminado();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    top10();
                    break;
                case 7:
                    estadisticas();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 0:

                    System.out.println("Saliendo de la aplicación");
                    System.exit(0);

                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }


    private Datos buscarDatosLibros() {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        String libro = teclado.nextLine();
        String json = consumoApi.obtenerDatos(URL_BASE + libro.replace(" ", "+"));
        return conversor.obtenerDatos(json, Datos.class);
    }

    private void buscarLibroWeb() {
        Datos datos = buscarDatosLibros();
        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibros.autor().get(0);

            Optional<Libro> libroExistente = libroRepository.findByTitulo(datosLibros.titulo());
            if (libroExistente.isPresent()) {
                System.out.println("El libro ya está en la base de datos.");
                return;
            }

            Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.nombre());
            Autor autor;
            if (autorExistente.isPresent()) {
                autor = autorExistente.get();
                System.out.println("Autor existente encontrado: " + autor.getNombre());
            } else {
                autor = new Autor(datosAutor);
                autorRepository.save(autor);
                System.out.println("Nuevo autor agregado: " + autor.getNombre());
            }

            Libro nuevoLibro = new Libro(datosLibros, autor);
            libroRepository.save(nuevoLibro);
            System.out.println("Nuevo libro agregado: " + nuevoLibro.getTitulo());

        } else {
            System.out.println("El libro buscado no se encuentra. Pruebe con otro.");
        }
    }

   /* private void buscarLibroWeb() {
        Datos datos = buscarDatosLibros();
        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibros.autor().get(0);
            System.out.println("Título: " + datosLibros.titulo());
            System.out.println("Autor: " + datosAutor.nombre());
            Autor autorNuevo = new Autor(datosAutor);
            autorRepository.save(autorNuevo);
            libroRepository.save(new Libro(datosLibros, autorNuevo ));
        } else {
            System.out.println("El libro buscado no se encuentra. Pruebe con otro.");
        }
    }*/


    private void listarLibros() {
        libros = libroRepository.findAll();

        libros.forEach(System.out::println);
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        for (Autor autor : autores) {
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
            System.out.println("Fecha de defunción: " + autor.getFechaDeDefuncion());
            System.out.println("Libros: ");
            for (Libro libro : autor.getLibro()) {
                System.out.println(" - " + libro.getTitulo());
            }
            System.out.println("-------------------------------------");
        }
    }

    private void listarAutoresVivosAnioDeterminado() {

        System.out.println("Ingrese el año: ");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autores = autorRepository.findAll();
        for (Autor autor : autores) {
            boolean estaVivo = false;
            int nacimiento = Integer.parseInt(autor.getFechaDeNacimiento().split("-")[0]);
            String defuncionStr = autor.getFechaDeDefuncion();
            int defuncion = defuncionStr != null && !defuncionStr.isEmpty() ? Integer.parseInt(defuncionStr.split("-")[0]) : Integer.MAX_VALUE;

            if (nacimiento <= anio && anio <= defuncion) {
                estaVivo = true;
            }

            if (estaVivo) {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
                System.out.println("Fecha de defunción: " + (defuncionStr == null ? "N/A" : defuncionStr));
                System.out.println("Libros: ");
                for (Libro libro : autor.getLibro()) {
                    System.out.println(" - " + libro.getTitulo());
                }
                System.out.println("-------------------------------------");
            }
        }

    }

    private void listarLibrosPorIdioma() {
        String idioma = obtenerIdiomaSeleccionado();

        if (idioma.isEmpty()) {
            System.out.println("Idioma no válido.");
            return;
        }

        List<Libro> librosPorIdioma = libroRepository.findLibrosByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma especificado.");
        } else {
            System.out.println("Libros en " + idioma + ":");
            for (Libro libro : librosPorIdioma) {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor().getNombre());
                System.out.println("-------------------------------------");
            }
        }
    }

    private String obtenerIdiomaSeleccionado() {
        String idioma = "";
        String menu = """
                Seleccione el idioma del libro que desea encontrar:
                \n---------------------------------------------------
                \n1 - Español
                \n2 - Francés
                \n3 - Inglés
                \n4 - Portugués
                \n----------------------------------------------------\n""";
        System.out.println(menu);

        try {
            int opcion = Integer.parseInt(this.teclado.nextLine());
            switch (opcion) {
                case 1 -> idioma = "es";
                case 2 -> idioma = "fr";
                case 3 -> idioma = "en";
                case 4 -> idioma = "pt";
                default -> System.out.println("Opción inválida!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }

        return idioma;
    }

    private void top10() {
        System.out.println("Top 10 libros más descargados");
        var json = consumoApi.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);
    }

    private void estadisticas() {
        //Trabajando con estadisticas
        var json = consumoApi.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " + est.getCount());

    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor que desea buscar: ");
        String nombre = teclado.nextLine();

        List<Autor> autores = autorRepository.findByNombredb(nombre);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con el nombre especificado.");
        } else {
            for (Autor autor : autores) {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
                System.out.println("Fecha de defunción: " + autor.getFechaDeDefuncion());
                System.out.println("Libros: ");
                for (Libro libro : autor.getLibro()) {
                    System.out.println(" - " + libro.getTitulo());
                }
                System.out.println("-------------------------------------");
            }
        }

    }
}
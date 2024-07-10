package com.agrsystems.literalura.principal;


import com.agrsystems.literalura.model.*;
import com.agrsystems.literalura.repository.AutorRepository;
import com.agrsystems.literalura.repository.LibroRepository;
import com.agrsystems.literalura.service.ConsumoAPI;
import com.agrsystems.literalura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    private List<Libro> libros;

    private final Scanner teclado = new Scanner(System.in);
    private final ConvierteDatos conversor = new ConvierteDatos();

    public Principal(LibroRepository libroRepository,AutorRepository autorRepository){
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
                case 1 :
                    buscarLibroWeb();
                    break;

                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();

                case 0 :

                    System.out.println("Saliendo de la aplicación");
                    System.exit(0);

                break;
                default :
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
}

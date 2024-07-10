package com.agrsystems.literalura.repository;

import com.agrsystems.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Autor> findByNombredb(@Param("nombre") String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento < :anio AND a.fechaDeDefuncion > :anio")
    List<Autor> estaVivo(@Param("anio") int anio);
}

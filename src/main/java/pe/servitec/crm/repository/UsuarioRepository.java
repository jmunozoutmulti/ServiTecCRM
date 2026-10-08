package pe.servitec.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.servitec.crm.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	Optional<Usuario> findByUsernameAndActivoTrue(String username);

	boolean existsByUsername(String username);

}

package poov.vacina.model.dao.core;

//Baseado nas viagens de:
// https://github.com/hasalex/jdbc-demo/tree/master/src/main/java/fr/sewatech/demo/jdbc
// e minha

//Esse outro doido vai mais longe ainda, pegando direto dos objetos os atributos e tals
//https://github.com/pydawan/generic-dao-jdbc/tree/master/src/main/java/jdbc/dao
//Ele acabou que implementou um ORM na mão

public interface DAO<T, PK> {

	<S extends T> S create(S entity);

	<S extends T> S update(S entity);

	T findById(PK key);

	Iterable<T> findAll();

	void deleteById(PK key);

	void delete(T entity);

	// boolean existsById(PK id);
	// <S extends T> Iterable<S> saveAll(Iterable<S> entities);
	// void deleteAll(Iterable<? extends T> entities);
	// void deleteAll();
	// long count();

}

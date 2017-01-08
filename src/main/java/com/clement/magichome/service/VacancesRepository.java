
package com.clement.magichome.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.clement.magichome.object.Vacances;

@RepositoryRestResource(collectionResourceRel = "vacances", path = "vacances")
public interface VacancesRepository extends MongoRepository<Vacances, String> {

}

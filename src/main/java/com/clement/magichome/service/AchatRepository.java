
package com.clement.magichome.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.clement.magichome.object.Achat;

@RepositoryRestResource(collectionResourceRel = "achat", path = "achat")
public interface AchatRepository extends MongoRepository<Achat, String> {
	List<Achat> findByActive(Boolean active);
}


package com.clement.magichome.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.clement.magichome.object.Channel;

@RepositoryRestResource(collectionResourceRel = "channel", path = "channel")
public interface ChannelRepository extends MongoRepository<Channel, String> {
	


	List<Channel> findByEpgId(@Param("epgId") String id);

}

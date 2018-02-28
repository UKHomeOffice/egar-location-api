package uk.gov.digital.ho.egar.location.service.repository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;

@Transactional
public interface GeographicLocationPersistentRecordRepository extends JpaRepository<GeographicLocationPersistentRecord, UUID>{
	
	GeographicLocationPersistentRecord findOneByLocationUuidAndUserUuid(UUID locationUuid,UUID userUuid);
	List<GeographicLocationPersistentRecord> findAllByUserUuid(UUID userUuid);
	Long countByUserUuid(UUID userUuid);
	List<GeographicLocationPersistentRecord> findAllByUserUuidAndLocationUuidIn(UUID uuidOfUser, List<UUID> locationUuids);
	
}

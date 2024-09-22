package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.FileAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileAttach, Long> {

    List<FileAttach> getFileAttachByObjectIdInAndObjectType(List<String> objectId, ObjectType objectType);

    List<FileAttach> getFileAttachByObjectIdAndObjectType(String objectId, ObjectType objectType);

    Optional<FileAttach> getFirstByObjectIdAndObjectType(String objectId, ObjectType objectType);
}

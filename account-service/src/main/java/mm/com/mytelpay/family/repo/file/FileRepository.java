package mm.com.mytelpay.family.repo.file;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.entities.FileAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileAttach, Long> {


    @Query("UPDATE FileAttach SET status = 'INACTIVE' WHERE objectId = :objectId AND objectType = :objectType")
    int disableByObjectIdAndObjectType(String objectId, ObjectType objectType);

    @Query("select f from FileAttach f where f.objectId = :objectId and f.objectType = :objectType")
    List<FileAttach> getFilesByObjectId(String objectId, ObjectType objectType);


    @Query("UPDATE FileAttach SET status = 'INACTIVE' WHERE id IN :ids AND objectId = :objectId AND objectType = :objectType")
    int disablesByObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    void deleteAllByObjectIdAndObjectType(String objectId, ObjectType objectType);
}

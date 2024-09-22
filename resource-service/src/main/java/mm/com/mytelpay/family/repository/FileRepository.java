package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.FileAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileAttach, Long> {

    @Query("UPDATE FileAttach SET status = 'INACTIVE' WHERE objectId = :objectId AND objectType = :objectType")
    int disableByObjectIdAndObjectType(String objectId, ObjectType objectType);

    @Query("select f from FileAttach f where f.objectId = :objectId and f.objectType = :objectType")
    List<FileAttach> getFilesByObjectId(String objectId, ObjectType objectType);

    @Query("select f from FileAttach f where f.objectId in :objectIds and f.objectType = :objectType")
    List<FileAttach> getFilesByObjectIds(List<String> objectIds, ObjectType objectType);

    @Query("UPDATE FileAttach SET status = 'INACTIVE' WHERE id IN :ids AND objectId = :objectId AND objectType = :objectType")
    int disablesByObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    @Transactional
    void deleteAllByObjectIdAndObjectType(String objectId, ObjectType objectType);

    void deleteByIdInAndObjectIdAndAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    List<FileAttach> getFileAttachByIdInAndObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    @Transactional
    int deleteFileAttachByIdInAndObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    FileAttach findFirstByObjectIdAndAndObjectType(String objectId, ObjectType objectType);

    List<FileAttach> getFileAttachByObjectIdInAndObjectType(List<String> objectId, ObjectType objectType);
}

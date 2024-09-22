package mm.com.mytelpay.family.business.file;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.entities.FileAttach;

import java.util.List;

public interface FileService {

    FileAttach create(FileAttach fileAttach);

    int disableByObjectIdAndObjectType(String objectId, ObjectType objectType);

    int disableListByObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType);

    List<FileAttach> findImageByObjectIdAndType(String objectId, ObjectType objectType);
}

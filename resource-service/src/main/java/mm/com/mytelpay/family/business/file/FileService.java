package mm.com.mytelpay.family.business.file;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.FileAttach;

import java.util.List;

public interface FileService {

    FileAttach create(FileAttach fileAttach);

    List<FileAttach> findImageByObjectIdAndType(String objectId, ObjectType objectType);

    List<FileAttach> findImagesByObjectIdsAndType(List<String> objectIds, ObjectType objectType);
}

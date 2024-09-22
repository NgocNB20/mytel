package mm.com.mytelpay.family.business.file;

import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.entities.FileAttach;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl extends AccountBaseBusiness implements FileService {

    public FileServiceImpl() {
        logger = LogManager.getLogger(FileServiceImpl.class);
    }

    @Override
    public FileAttach create(FileAttach fileAttach) {
        return fileRepository.save(fileAttach);
    }


    @Override
    public int disableByObjectIdAndObjectType(String objectId, ObjectType objectType) {
        return fileRepository.disableByObjectIdAndObjectType(objectId, objectType);
    }

    @Override
    public int disableListByObjectIdAndObjectType(List<Long> ids, String objectId, ObjectType objectType) {
        return fileRepository.disablesByObjectIdAndObjectType(ids, objectId, objectType);
    }

    @Override
    public List<FileAttach> findImageByObjectIdAndType(String objectId, ObjectType objectType) {
        return fileRepository.getFilesByObjectId(objectId, objectType);
    }
}

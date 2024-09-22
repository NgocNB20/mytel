package mm.com.mytelpay.family.business.file;

import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Value("${amazonProperties.basePath}")
    public String url;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public FileAttach create(FileAttach fileAttach) {
        return fileRepository.save(fileAttach);
    }

    @Override
    public List<FileAttach> findImageByObjectIdAndType(String objectId, ObjectType objectType) {
        return fileRepository.getFilesByObjectId(objectId, objectType);
    }

    @Override
    public List<FileAttach> findImagesByObjectIdsAndType(List<String> objectIds, ObjectType objectType) {
        return fileRepository.getFilesByObjectIds(objectIds, objectType);
    }
}

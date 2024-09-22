package mm.com.mytelpay.family.business.file;

import mm.com.mytelpay.family.model.FileAttach;
import mm.com.mytelpay.family.repo.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public FileAttach create(FileAttach fileAttach) {
        return fileRepository.save(fileAttach);
    }

}

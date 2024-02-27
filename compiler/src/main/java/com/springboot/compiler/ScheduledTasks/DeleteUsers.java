package com.springboot.compiler.ScheduledTasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.compiler.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class DeleteUsers {
    
    private final UserRepository userRepository;
    
    @Value("${submission.path}")
    private String rootPath;


    @Scheduled(cron="0 0 2 * * *")
    public void deleteUser(){

        //it will give all submissions of user who are  marked for deletion and delete in DB
        List<String> ids=userRepository.deleteUser();
        for(String id:ids){

            //making path ro directory(id)
            Path path=Path.of(rootPath+id);

            //iterating over each file
            for(String fileName:path.toFile().list()){
                Path filePath=path.resolve(fileName);
                try {
                    //deleting file
                    Files.delete(filePath);
                }
                catch(IOException e){
                    //ignore
                }
            }
            try {
                //deleting directory
                Files.delete(path);
            }
            catch(IOException e){
                //ignore
            }
        }
        log.info("Scheduled user deletion done");
    }
}

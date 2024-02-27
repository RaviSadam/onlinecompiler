package com.springboot.compiler.Services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.compiler.Dtos.CompilationResponse;
import com.springboot.compiler.Dtos.CompileRequest;
import com.springboot.compiler.Dtos.GptMessage;
import com.springboot.compiler.Dtos.GptRequest;
import com.springboot.compiler.Dtos.SubmissionCount;
import com.springboot.compiler.Dtos.SubmissionDetails;
import com.springboot.compiler.Dtos.UserDetails;
import com.springboot.compiler.Dtos.UserSubmission;
import com.springboot.compiler.Dtos.UserSubmissionDetails;
import com.springboot.compiler.Dtos.UserUpdateDto;
import com.springboot.compiler.Models.Status;
import com.springboot.compiler.Projections.SubmissionCountProjection;
import com.springboot.compiler.Projections.SubmissionDetailsProjection;
import com.springboot.compiler.Projections.UserDetailsProjection;
import com.springboot.compiler.Repositories.SubmissionsRepository;
import com.springboot.compiler.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MainService {

    private final SubmissionsRepository submissionsRepository;
    private final UserRepository userRepository;
    private final CompilationService compilationService;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final static Map<String,String> EXTENSION_MAP;


    @Value("${submission.path}")
    private String path;

    @Value("${openai.auth.token}")
    private String gptToken;

    @Value("${openai.url}")
    private String gptUrl;

    @Value("${openai.model}")
    private String gptModel;


    static{
        EXTENSION_MAP=new HashMap<>();
        EXTENSION_MAP.put("python", ".py");
        EXTENSION_MAP.put("java", ".java");
        EXTENSION_MAP.put("c", ".c");
        EXTENSION_MAP.put("c++", ".cpp"); 
    }



    //returns the user details
    @Cacheable(cacheNames = "mainCache",key="#username")
    public UserDetails getUserDetails(String username){
        UserDetailsProjection projection=userRepository.getUserDetails(username);
        return UserDetails.builder().username(projection.getUsername()).email(projection.getEmail()).favLanguage(projection.getFavLanguage()).firstName(projection.getFirstName()).lastName(projection.getLastName()).gender(projection.getGender()).successSubmissionsCount(projection.getSuccessSubmissionsCount()).totalSubmissions(projection.getTotalSubmissions()).build();
    }

    public SubmissionDetails getUserSubmissionDetails(String username){
        List<SubmissionCountProjection> submissionCountProjections=submissionsRepository.getSubmissionCounts(username);
        if(submissionCountProjections.isEmpty())
            return null;
        int past24=0,total=0,successCount=0;
        List<SubmissionCount> submissionCounts=new ArrayList<>();
        for(SubmissionCountProjection submissionCountProjection:submissionCountProjections){
            submissionCounts.add(SubmissionCount.builder().count(submissionCountProjection.getCount()).language(submissionCountProjection.getLanguage()).successCount(submissionCountProjection.getSuccessCount()).build());
            total+=submissionCountProjection.getCount();
            past24+=submissionCountProjection.getPastCount();
            successCount+=submissionCountProjection.getSuccessCount();
        }
        return SubmissionDetails.builder().past24(past24).successCount(successCount).totalCount(total).username(username).submissions(submissionCounts).build();
    }

    //gives the submissions maded in last hours
    public UserSubmissionDetails getSubmissionsInLastHours(String username,int hour,String language,String s,int pageNumber,int pageSize){
        Pageable page=PageRequest.of(pageNumber, pageSize);
        Page<SubmissionDetailsProjection> response=null;

        Status status=null;
        if(s!=null)
            status=Status.valueOf(s);
        if(username!=null){
            if(language==null && status==null)
                response=submissionsRepository.getSubmissionsInLastHourUser(username, hour,page);
            else if(language==null && status!=null)
                response=submissionsRepository.getSubmissionsInLastHourUserWithStatus(username, hour,status, page);
            else if(language!=null && status==null)
                response=submissionsRepository.getSubmissionsInLastHourUserWithLanguage(username, hour,language, page);
            else
                response=submissionsRepository.getSubmissionsInLastHourUserWithStatusAndLanguage(username, hour,language, status, page);

        }
        else{
            if(language==null && status==null)
                response=submissionsRepository.getSubmissionsInLastHours(hour, page);
            else if(language==null && status!=null)
                response=submissionsRepository.getSubmissionsInLastHoursWithStatus(hour, status, page);
            else if(language!=null && status==null)
                response=submissionsRepository.getSubmissionsInLastHoursWithLanguage(hour, language, page);
            else
                response=submissionsRepository.getSubmissionsInLastHoursWithStatusAndLanguage(hour, language, status, page);
        }
        
        int pages=response.getTotalPages();
        int size=response.getSize();
        int number=response.getNumber()+1;
        List<UserSubmission> userSubmissions=response.stream().map((projection)->UserSubmission.builder().language(projection.getLanguage()).username(projection.getUsername()).submissionTime(projection.getDate()).status(projection.getStatus()).submissionId(projection.getSubmissionId()).build()).collect(Collectors.toList());
        return UserSubmissionDetails.builder().pageNumber(number).pageSize(size).totlaPages(pages).userSubmissions(userSubmissions).build();

    }

    //update user details
    public void updateUser(String username,UserUpdateDto userUpdateDto){
        if(userUpdateDto.getPassword()!=null && !userUpdateDto.getPassword().isBlank())
            userUpdateDto.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        userRepository.updateUser(userUpdateDto.getFirstName(),userUpdateDto.getLastName(),userUpdateDto.getFaviourateLanguage(),userUpdateDto.getPassword(),userUpdateDto.getGender());
    }


    //compiles the give code
    public CompilationResponse compileAndRunCode(String username,CompileRequest request){
        CompilationResponse response= compilationService.compileAndRunCode(request);

        if(response.getStatus().compareTo(Status.SUCCESS)==0 && request.getExpectedOutput()!=null){
            response.setOutput(response.getOutput().replaceAll("[\\r\\n]", ""));
            if(!request.getExpectedOutput().equals(response.getOutput())){
                response.setStatus(Status.WRONG_ANSWER);
                response.setErrorDetails("Logical Error");
            }
        }

        response.setRunTime(response.getRunTime().substring(2));


        submissionsRepository.insertIntoSubmissions(request.getLanguage(), response.getRunTime(), response.getStatus().toString(), new Date(System.currentTimeMillis()), response.getSubmissionId(),username);
        response.setUsername(username);

        return response;
    }


    @SuppressWarnings("null")
    public void deleteSubmission(String username,String submissionId){
        
        int count=submissionsRepository.deleteSubmission(submissionId, username);
        if(count==1){
            Path directoryPath=Path.of(path+submissionId);
            System.out.println(directoryPath);
            try {
                File file=directoryPath.toFile();
                //delete files in Directory
                for(String fileName:file.list()){
                    Path filePath=directoryPath.resolve(fileName);
                    Files.delete(filePath);
                }
                //delete directory
                Files.delete(file.toPath());
                cacheManager.getCache("mainCache").evict(submissionId);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new AccessDeniedException("Not allowed to delete submission");
    }

    @CacheEvict(cacheNames="mainCache",key = "#username")
    public void deleteUser(String username){
        userRepository.makrForDeletion(username);
    }

    //gives the supported languages
    public Set<String> getLanguages(){
        log.info("Cache requested");
        return Set.of("C","C++","Java","Pyton");
    }

    //gives submission details based on submissionId
    @CacheEvict(cacheNames="mainCache",key = "#submissionId")
    public CompilationResponse getSubmissionDetails(String submissionId){
        
        SubmissionDetailsProjection details=submissionsRepository.getSubmissionDetails(submissionId);
        if(details==null || details.getSubmissionId()==null)
            return new CompilationResponse();
        CompilationResponse response=CompilationResponse.builder()
                                                        .language(details.getLanguage())
                                                        .runTime(details.getRunTime())
                                                        .status(details.getStatus())
                                                        .submissionId(details.getSubmissionId())
                                                        .username(details.getUsername())
                                                        .build();
        Path path=this.getPath(submissionId);
        File sourceFile=path.resolve("Main"+EXTENSION_MAP.get(response.getLanguage())).toFile();
        File inputFile=path.resolve("input.txt").toFile();
        File outputFile=path.resolve("output.txt").toFile();

        response.setCode(readFileData(sourceFile));
        response.setInput(readFileData(inputFile));
        response.setOutput(readFileData(outputFile));

        return response;
    }

    //gives user submissions
    public UserSubmissionDetails getUserSubmissionDetails(String username,int pageNumber,int pageSize,String language,String s){
        Pageable page=PageRequest.of(pageNumber, pageSize);
        Page<SubmissionDetailsProjection> response=null;

        Status status=null;
        if(s!=null)
            status=Status.valueOf(s);

        
        if(language==null && status==null)
            response=submissionsRepository.getUserSubmissionDetails(username, page);
        else if(language==null && status!=null)
            response=submissionsRepository.getUserSubmissionDetailsWithStatus(username, status, page);
        else if(language!=null && status==null)
            response=submissionsRepository.getUserSubmissionDetailsWithLanguage(username, language, page);
        else
            response=submissionsRepository.getUserSubmissionDetailsWithStatusAndLanguage(username, language, status, page);
        
        int pages=response.getTotalPages();
        int size=response.getSize();
        int number=response.getNumber()+1;
        List<UserSubmission> userSubmissions=response.stream().map((projection)->UserSubmission.builder().language(projection.getLanguage()).status(projection.getStatus()).submissionId(projection.getSubmissionId()).build()).collect(Collectors.toList());
        return UserSubmissionDetails.builder().pageNumber(number).pageSize(size).totlaPages(pages).username(username).userSubmissions(userSubmissions).build();
    }


    @SuppressWarnings({ "null", "resource" })
    public Object[] downloadFile(String submissionId){
        File file=new File(path+submissionId);
        String fileName=null;
        if(!file.exists())
            throw new UsernameNotFoundException("Submission not exists");

        for(String name:file.list()){
            if(name.startsWith("Main")){
                fileName=name;
                break;
            }
        }
        File file2=new File(path+submissionId+"/"+fileName);
        if(file2.exists()){
            try {
                Resource resource=new ByteArrayResource((new FileInputStream(file2)).readAllBytes());
                return new Object[]{fileName,resource};
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Object[]{};
    }

    public List<String> getMarkedUsers(){
        return userRepository.getMarkedUsers();
    }


    @SuppressWarnings("null")
    public String getAlgorithm(String code){
        GptMessage gptMessage=GptMessage.builder().content(code+"  Just give algorithm for above code no additional content is requried").role("user").build();
        GptRequest gptRequest=GptRequest.builder().model(gptModel).messages(List.of(gptMessage)).frequency_penalty(0).max_tokens(256).presence_penalty(0).temperature(1).top_p(1).build();
        String body=null;
        try {
            body = objectMapper.writeValueAsString(gptRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","Bearer "+gptToken);

        HttpEntity<String> entity=new HttpEntity<String>(body, headers);
                   
        ResponseEntity<String> response= restTemplate.postForEntity(gptUrl,entity,String.class);
        JsonNode jsonNode=null;
        try {
            jsonNode = objectMapper.readTree(response.getBody()).get("choices");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get(0).get("message").get("content").asText();
    }




    private Path getPath(String id){
        return Path.of(path+id);
    }

    private String readFileData(File file){
        BufferedInputStream inputStream=null;
        try{
            inputStream = new BufferedInputStream(new FileInputStream(file));
            String res=new String(inputStream.readAllBytes());
            inputStream.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occured while reading data";
        }
    }
}
